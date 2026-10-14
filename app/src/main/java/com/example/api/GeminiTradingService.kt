package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.model.MarketParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiTradingService(private val okHttpClient: OkHttpClient = defaultClient()) {

    companion object {
        private const val MODEL_PRIMARY = "gemini-2.5-flash"
        private const val MODEL_FALLBACK = "gemini-3.5-flash"

        fun defaultClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }
    }

    suspend fun analyzeChart(bitmap: Bitmap, apiKey: String): Result<MarketParameters> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Gemini API Key missing. Please configure your key in Settings or AI Studio Secrets.")
            )
        }

        val base64Image = bitmapToBase64(bitmap)
        val promptText = """
You are the execution parser for Gold Baba Formula V1.0 and XMM Model on XAUUSD M5.
Analyze the provided TradingView chart screenshot and extract the exact parameters.
Output ONLY a raw valid JSON object with no markdown ticks, no backticks, no prose:
{
  "p0": <current price as float>,
  "resistance": <nearest overhead resistance / recent wick high level as float>,
  "support": <nearest demand / recent wick low level as float>,
  "velocity": <rate of change points/min typically -0.6 to +0.6 based on candle body expansion>,
  "acceleration": <rate of acceleration points/min^2 typically -0.08 to +0.08 based on momentum>,
  "atr": 3.4,
  "liquidity_state": "<Sweep / BOS / Retest / Compression>"
}
""".trimIndent()

        // Try primary model first, then fallback
        val primaryResult = executeGeminiRequest(MODEL_PRIMARY, apiKey, promptText, base64Image)
        if (primaryResult.isSuccess) {
            return@withContext primaryResult
        }

        // Retry with fallback model if primary returned error
        executeGeminiRequest(MODEL_FALLBACK, apiKey, promptText, base64Image)
    }

    private fun executeGeminiRequest(
        modelName: String,
        apiKey: String,
        promptText: String,
        base64Image: String
    ): Result<MarketParameters> {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()

            // Part 1: Text Prompt
            val textPart = JSONObject().apply {
                put("text", promptText)
            }
            partsArray.put(textPart)

            // Part 2: Inline Image Data
            val imagePart = JSONObject().apply {
                val inlineData = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Image)
                }
                put("inlineData", inlineData)
            }
            partsArray.put(imagePart)

            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            put("contents", contentsArray)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(endpoint)
            .post(requestBody)
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMessage = parseErrorMessage(responseString, response.code)
                return Result.failure(Exception("Gemini API Error ($modelName, ${response.code}): $errorMessage"))
            }

            val marketParams = parseGeminiResponse(responseString)
            Result.success(marketParams)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseGeminiResponse(responseJsonString: String): MarketParameters {
        val root = JSONObject(responseJsonString)
        val candidates = root.optJSONArray("candidates")
            ?: throw IllegalStateException("No candidates found in Gemini response.")

        if (candidates.length() == 0) {
            throw IllegalStateException("Empty candidates list from Gemini.")
        }

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        if (parts.length() == 0) {
            throw IllegalStateException("Empty parts list from candidate.")
        }

        var rawText = parts.getJSONObject(0).getString("text")
        // Clean markdown backticks
        rawText = rawText.replace("```json", "")
            .replace("```", "")
            .trim()

        // Locate json block if any extra text exists
        val startBrace = rawText.indexOf('{')
        val endBrace = rawText.lastIndexOf('}')
        if (startBrace != -1 && endBrace != -1 && endBrace > startBrace) {
            rawText = rawText.substring(startBrace, endBrace + 1)
        }

        val json = JSONObject(rawText)

        val p0 = json.optDouble("p0", 2650.0)
        val resistance = json.optDouble("resistance", p0 + 5.0)
        val support = json.optDouble("support", p0 - 5.0)
        val velocity = json.optDouble("velocity", 0.0)
        val acceleration = json.optDouble("acceleration", 0.0)
        val atr = json.optDouble("atr", 3.4)
        val liquidityState = json.optString("liquidity_state", "Compression")

        return MarketParameters(
            p0 = p0,
            resistance = resistance,
            support = support,
            velocity = velocity,
            acceleration = acceleration,
            atr = atr,
            liquidityState = liquidityState
        )
    }

    private fun parseErrorMessage(errorBody: String, httpCode: Int): String {
        return try {
            val json = JSONObject(errorBody)
            if (json.has("error")) {
                val errObj = json.getJSONObject("error")
                errObj.optString("message", "HTTP $httpCode")
            } else {
                "HTTP $httpCode: $errorBody"
            }
        } catch (e: Exception) {
            "HTTP $httpCode: $errorBody"
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Scale down if image is huge to optimize network speed & avoid memory spikes
        val maxDimension = 1280
        val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val targetWidth: Int
            val targetHeight: Int
            if (ratio > 1) {
                targetWidth = maxDimension
                targetHeight = (maxDimension / ratio).toInt()
            } else {
                targetHeight = maxDimension
                targetWidth = (maxDimension * ratio).toInt()
            }
            Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        } else {
            bitmap
        }

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

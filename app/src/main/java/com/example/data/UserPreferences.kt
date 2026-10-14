package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gold_baba_prefs", Context.MODE_PRIVATE)

    var customApiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value.trim()).apply()

    var isBilingualHindi: Boolean
        get() = prefs.getBoolean(KEY_BILINGUAL, true)
        set(value) = prefs.edit().putBoolean(KEY_BILINGUAL, value).apply()

    fun getEffectiveApiKey(): String {
        val userKey = customApiKey
        if (userKey.isNotBlank()) return userKey
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }

    fun hasApiKey(): Boolean {
        return getEffectiveApiKey().isNotBlank()
    }

    companion object {
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_BILINGUAL = "bilingual_hindi"
    }
}

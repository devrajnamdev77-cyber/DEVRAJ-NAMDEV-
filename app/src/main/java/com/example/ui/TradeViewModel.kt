package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.api.GeminiTradingService
import com.example.data.GoldBabaDatabase
import com.example.data.ScanHistoryEntity
import com.example.data.UserPreferences
import com.example.engine.XMMCalculator
import com.example.model.MarketParameters
import com.example.model.SampleChart
import com.example.model.SignalVerdict
import com.example.model.XMMResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

sealed interface ScanUiState {
    object Idle : ScanUiState
    data class Loading(val status: String) : ScanUiState
    data class Success(val result: XMMResult) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

class TradeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application)
    private val geminiService = GeminiTradingService()
    private val historyDao = GoldBabaDatabase.getDatabase(application).scanHistoryDao()

    // API Key & Preferences State
    private val _apiKey = MutableStateFlow(userPrefs.getEffectiveApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _isBilingual = MutableStateFlow(userPrefs.isBilingualHindi)
    val isBilingual: StateFlow<Boolean> = _isBilingual.asStateFlow()

    // Flagship Scanner State
    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    private val _currentBitmap = MutableStateFlow<Bitmap?>(null)
    val currentBitmap: StateFlow<Bitmap?> = _currentBitmap.asStateFlow()

    private val _selectedSample = MutableStateFlow<SampleChart?>(null)
    val selectedSample: StateFlow<SampleChart?> = _selectedSample.asStateFlow()

    // Terminal / Console Logs
    private val _terminalLogs = MutableStateFlow<List<String>>(
        listOf(
            "[SYSTEM] Gold Baba XMM & V9 Engine initialized.",
            "[INFO] Autonomous XAUUSD M5 Analytical Model online.",
            "[READY] Terminal active. Ready for chart scans or manual simulation."
        )
    )
    val terminalLogs: StateFlow<List<String>> = _terminalLogs.asStateFlow()

    fun logTerminal(message: String) {
        val time = java.text.SimpleDateFormat("HH:mm:ss", Locale.US).format(java.util.Date())
        _terminalLogs.value = _terminalLogs.value + "[$time] $message"
    }

    fun clearTerminalLogs() {
        _terminalLogs.value = listOf("[SYSTEM] Console buffer cleared.")
    }

    fun executeTerminalCommand(cmd: String) {
        val clean = cmd.trim().lowercase(Locale.US)
        logTerminal("> $cmd")
        when {
            clean == "status" || clean == "--status" -> {
                val keyStatus = if (_apiKey.value.isNotBlank()) "CONNECTED" else "UNCONFIGURED"
                val chartStatus = if (_currentBitmap.value != null) "LOADED (${_selectedSample.value?.title ?: "Custom"})" else "NONE"
                logTerminal("[STATUS] Engine: Gold Baba XMM V9 (Autonomous M5)")
                logTerminal("[STATUS] Gemini API: $keyStatus")
                logTerminal("[STATUS] Buffer Chart: $chartStatus")
                logTerminal("[STATUS] Active Simulated Price: $${_manualParams.value.p0}")
            }
            clean == "test-bos" || clean == "bos" -> {
                val sample = sampleCharts.firstOrNull { it.id == "bullish" }
                if (sample != null) {
                    selectSampleChart(sample)
                    executeScan()
                }
            }
            clean == "test-sweep" || clean == "sweep" -> {
                val sample = sampleCharts.firstOrNull { it.id == "bearish" }
                if (sample != null) {
                    selectSampleChart(sample)
                    executeScan()
                }
            }
            clean == "help" || clean == "--help" || clean == "?" -> {
                logTerminal("[HELP] Available Commands:")
                logTerminal("  • status       : Show engine and connection telemetry")
                logTerminal("  • test-bos     : Load & solve Bullish Demand BOS scenario")
                logTerminal("  • test-sweep   : Load & solve Bearish Supply Sweep scenario")
                logTerminal("  • scan         : Run XMM solver on loaded chart")
                logTerminal("  • clear        : Reset terminal buffer")
                logTerminal("  • help         : Print this manual")
            }
            clean == "scan" || clean == "run" -> {
                executeScan()
            }
            clean == "clear" || clean == "cls" -> {
                clearTerminalLogs()
            }
            else -> {
                logTerminal("[CMD] Unrecognized instruction '$cmd'. Type 'help' for command list.")
            }
        }
    }

    // Available Sample Charts
    val sampleCharts = listOf(
        SampleChart(
            id = "bullish",
            title = "Demand BOS",
            subtitle = "Bullish $2648.50",
            drawableResId = R.drawable.sample_chart_bullish,
            parameters = MarketParameters(
                p0 = 2648.50,
                resistance = 2658.00,
                support = 2645.00,
                velocity = 0.45,
                acceleration = 0.05,
                atr = 3.4,
                liquidityState = "BOS"
            )
        ),
        SampleChart(
            id = "bearish",
            title = "Supply Sweep",
            subtitle = "Bearish $2662.30",
            drawableResId = R.drawable.sample_chart_bearish,
            parameters = MarketParameters(
                p0 = 2662.30,
                resistance = 2664.50,
                support = 2649.00,
                velocity = -0.42,
                acceleration = -0.05,
                atr = 3.6,
                liquidityState = "Sweep"
            )
        ),
        SampleChart(
            id = "range",
            title = "Range Chop",
            subtitle = "Neutral $2652.10",
            drawableResId = R.drawable.sample_chart_range,
            parameters = MarketParameters(
                p0 = 2652.10,
                resistance = 2656.00,
                support = 2647.00,
                velocity = 0.02,
                acceleration = 0.01,
                atr = 2.8,
                liquidityState = "Compression"
            )
        )
    )

    // Manual Lab Interactive State
    private val _manualParams = MutableStateFlow(
        MarketParameters(
            p0 = 2650.00,
            resistance = 2658.00,
            support = 2644.00,
            velocity = 0.30,
            acceleration = 0.04,
            atr = 3.4,
            liquidityState = "BOS"
        )
    )
    val manualParams: StateFlow<MarketParameters> = _manualParams.asStateFlow()

    private val _manualResult = MutableStateFlow(XMMCalculator.calculate(_manualParams.value))
    val manualResult: StateFlow<XMMResult> = _manualResult.asStateFlow()

    // History Flow
    val historyList: StateFlow<List<XMMResult>> = historyDao.getAllScans()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setApiKey(newKey: String) {
        userPrefs.customApiKey = newKey
        _apiKey.value = userPrefs.getEffectiveApiKey()
    }

    fun toggleBilingual() {
        val newVal = !_isBilingual.value
        userPrefs.isBilingualHindi = newVal
        _isBilingual.value = newVal
    }

    fun selectSampleChart(sample: SampleChart) {
        _selectedSample.value = sample
        val context = getApplication<Application>()
        val bitmap = BitmapFactory.decodeResource(context.resources, sample.drawableResId)
        _currentBitmap.value = bitmap
        logTerminal("CHART LOADED: Sample '${sample.title}' (P0=${sample.parameters.p0}, S=${sample.parameters.support}, R=${sample.parameters.resistance})")
    }

    fun setImageUri(uri: Uri) {
        _selectedSample.value = null
        try {
            val context = getApplication<Application>()
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                _currentBitmap.value = bitmap
                logTerminal("IMAGE LOADED: Custom TradingView M5 screenshot from device.")
            }
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            logTerminal("ERROR: Failed to decode selected image: ${e.message}")
        }
    }

    fun clearImage() {
        _currentBitmap.value = null
        _selectedSample.value = null
        logTerminal("BUFFER: Cleared current chart screenshot.")
    }

    fun executeScan() {
        val bitmap = _currentBitmap.value
        if (bitmap == null) {
            _scanUiState.value = ScanUiState.Error(
                if (_isBilingual.value) "कृपया XAUUSD M5 का स्क्रीनशॉट या सैम्पल चार्ट चुनें!"
                else "Please upload a chart screenshot or select a sample chart first!"
            )
            logTerminal("WARNING: Scan triggered without image selected.")
            return
        }

        val key = _apiKey.value.trim()
        val sample = _selectedSample.value

        // If a built-in sample is active and user hasn't configured an API key yet,
        // compute using known parameters so user can instantly test the formula!
        if (sample != null && key.isBlank()) {
            logTerminal("EXECUTE: Running offline XMM Formula on '${sample.title}'...")
            val result = XMMCalculator.calculate(sample.parameters)
            _scanUiState.value = ScanUiState.Success(result)
            logTerminal("RESULT: ${result.verdict.labelEn} | Score: ${result.score} | P_fut: ${String.format(Locale.US, "%.2f", result.pFuture)} | Gravity: ${String.format(Locale.US, "%.2f", result.gravity)}")
            saveResultToHistory(result, "Sample: ${sample.title}")
            return
        }

        if (key.isBlank()) {
            _scanUiState.value = ScanUiState.Error(
                if (_isBilingual.value) "कृपया अपनी Gemini API Key डालें (Settings में जा कर सेट करें)!"
                else "Please configure your Gemini API Key in Settings to scan charts!"
            )
            logTerminal("ERROR: Gemini API Key missing.")
            return
        }

        viewModelScope.launch {
            logTerminal("AI DISPATCH: Sending M5 chart to Gemini 2.5/3.5 Flash...")
            _scanUiState.value = ScanUiState.Loading(
                if (_isBilingual.value) "⏳ AI मॉडल चार्ट पढ़ रहा है और XMM फॉर्मूला लागू कर रहा है..."
                else "⏳ Analyzing M5 candles & computing XMM formula..."
            )

            val apiResult = geminiService.analyzeChart(bitmap, key)
            apiResult.onSuccess { params ->
                logTerminal("AI PARSE SUCCESS: P0=${params.p0}, R=${params.resistance}, S=${params.support}, V=${params.velocity}, A=${params.acceleration}, ATR=${params.atr}, State=${params.liquidityState}")
                val calculated = XMMCalculator.calculate(params)
                _scanUiState.value = ScanUiState.Success(calculated)
                logTerminal("XMM SOLVED: ${calculated.verdict.labelEn} | Score: ${calculated.score} | Target: ${String.format(Locale.US, "%.2f", calculated.target1)}")
                saveResultToHistory(calculated, if (sample != null) "Sample: ${sample.title}" else "Screenshot Scan")
            }.onFailure { err ->
                logTerminal("AI ERROR: ${err.message}")
                // If API failed but we have a sample selected, offer sample computation fallback
                if (sample != null) {
                    val fallbackResult = XMMCalculator.calculate(sample.parameters)
                    _scanUiState.value = ScanUiState.Success(fallbackResult)
                    logTerminal("FALLBACK: Computed deterministic formula on sample parameters.")
                    saveResultToHistory(fallbackResult, "Sample (Local Fallback)")
                } else {
                    _scanUiState.value = ScanUiState.Error(
                        (if (_isBilingual.value) "एनालिसिस में समस्या आई: " else "Analysis failed: ") +
                                (err.message ?: "Unknown error")
                    )
                }
            }
        }
    }

    private fun saveResultToHistory(result: XMMResult, note: String) {
        viewModelScope.launch {
            val entity = ScanHistoryEntity.fromDomain(result.copy(chartNote = note))
            historyDao.insertScan(entity)
        }
    }

    fun deleteHistoryItem(result: XMMResult) {
        viewModelScope.launch {
            historyDao.deleteScan(ScanHistoryEntity.fromDomain(result))
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAll()
        }
    }

    // Manual Lab Updates
    fun updateManualParams(
        p0: Double = _manualParams.value.p0,
        r: Double = _manualParams.value.resistance,
        s: Double = _manualParams.value.support,
        v: Double = _manualParams.value.velocity,
        a: Double = _manualParams.value.acceleration,
        atr: Double = _manualParams.value.atr,
        state: String = _manualParams.value.liquidityState
    ) {
        val updated = MarketParameters(
            p0 = p0,
            resistance = r,
            support = s,
            velocity = v,
            acceleration = a,
            atr = atr,
            liquidityState = state
        )
        _manualParams.value = updated
        _manualResult.value = XMMCalculator.calculate(updated)
    }

    fun copySignalToClipboard(result: XMMResult) {
        val context = getApplication<Application>()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = buildString {
            appendLine("🪙 GOLD BABA - XAUUSD M5 SIGNAL")
            appendLine("Verdict: ${result.verdict.labelEn}")
            appendLine("Model Score: ${if (result.score > 0) "+" else ""}${result.score}")
            appendLine("Current Price (P0): $${String.format(Locale.US, "%.2f", result.p0)}")
            appendLine("Predicted Future (P_fut): $${String.format(Locale.US, "%.2f", result.pFuture)}")
            appendLine("Overhead R: $${String.format(Locale.US, "%.2f", result.resistance)} | Demand S: $${String.format(Locale.US, "%.2f", result.support)}")
            appendLine("Velocity (V): ${String.format(Locale.US, "%.2f", result.velocity)} pts/min")
            appendLine("Gravity (G): ${String.format(Locale.US, "%.2f", result.gravity)}")
            if (result.verdict != SignalVerdict.STRICT_WAIT) {
                appendLine("Entry Zone: $${String.format(Locale.US, "%.2f", result.entryMin)} - $${String.format(Locale.US, "%.2f", result.entryMax)}")
                appendLine("Stop Loss (SL): $${String.format(Locale.US, "%.2f", result.stopLoss)}")
                appendLine("Target 1 (TP1): $${String.format(Locale.US, "%.2f", result.target1)}")
                appendLine("Target 2 (TP2): $${String.format(Locale.US, "%.2f", result.target2)}")
            }
            appendLine("Model: XMM V9 Kinematic Momentum")
        }
        val clip = ClipData.newPlainText("Gold Baba Signal", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Trade signal copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
    }
}

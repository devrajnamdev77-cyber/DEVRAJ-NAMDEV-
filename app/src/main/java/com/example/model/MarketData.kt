package com.example.model

enum class SignalVerdict(val labelEn: String, val labelHi: String) {
    STRONG_BUY("STRONG BUY 🟢", "मजबूत खरीदारी (STRONG BUY) 🟢"),
    STRONG_SELL("STRONG SELL 🔴", "मजबूत बिकवाली (STRONG SELL) 🔴"),
    STRICT_WAIT("STRICT WAIT / NEUTRAL ⚪", "सख्त इंतज़ार / न्यूट्रल ⚪")
}

data class MarketParameters(
    val p0: Double,
    val resistance: Double,
    val support: Double,
    val velocity: Double,
    val acceleration: Double,
    val atr: Double = 3.4,
    val liquidityState: String = "Compression"
)

data class XMMResult(
    val p0: Double,
    val pFuture: Double,
    val resistance: Double,
    val support: Double,
    val velocity: Double,
    val acceleration: Double,
    val gravity: Double,
    val atr: Double,
    val liquidityState: String,
    val score: Int,
    val verdict: SignalVerdict,
    val entryMin: Double,
    val entryMax: Double,
    val stopLoss: Double,
    val target1: Double,
    val target2: Double,
    val actionsEn: List<String>,
    val actionsHi: List<String>,
    val timestamp: Long = System.currentTimeMillis(),
    val id: Long = 0,
    val chartNote: String = "XAUUSD M5"
)

data class SampleChart(
    val id: String,
    val title: String,
    val subtitle: String,
    val drawableResId: Int,
    val parameters: MarketParameters
)

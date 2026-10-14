package com.example.engine

import com.example.model.MarketParameters
import com.example.model.SignalVerdict
import com.example.model.XMMResult
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

object XMMCalculator {
    const val DEFAULT_TIME_T = 5.0
    const val DEFAULT_GRAVITY_K = 0.50

    fun calculate(data: MarketParameters): XMMResult {
        val p0 = data.p0
        val r = data.resistance
        val s = data.support
        val v = data.velocity
        val a = data.acceleration
        val atr = if (data.atr > 0) data.atr else 3.4
        val t = DEFAULT_TIME_T

        // Distances with min distance safeguard of 0.2 points
        val dR = max(0.2, r - p0)
        val dS = max(0.2, p0 - s)

        // Gravity Calculation: G = k * ((1 / dR) - (1 / dS)) * ATR
        var g = DEFAULT_GRAVITY_K * ((1.0 / dR) - (1.0 / dS)) * atr

        // Support Rebound Adjustment
        if (dS <= 1.5 && p0 > s && v >= 0) {
            g = abs(g)
        }

        // Kinematic Future Price: P_future = P0 + Vt + 0.5*A*t^2 + G
        val pFuture = p0 + (v * t) + (0.5 * a * t.pow(2.0)) + g

        // Model Score Matrix (-100 to +100)
        var score = 0
        score += if (v > 0) 25 else -25
        score += if (a > 0) 15 else -15
        score += if (pFuture > p0) 20 else -20

        val state = data.liquidityState.trim()
        if (state.equals("BOS", ignoreCase = true) && v > 0) score += 15
        if (state.equals("BOS", ignoreCase = true) && v < 0) score -= 15
        if (state.equals("Sweep", ignoreCase = true) && v < 0) score -= 10
        if (state.equals("Sweep", ignoreCase = true) && v > 0) score += 10

        // Clamp score between -100 and +100
        score = score.coerceIn(-100, 100)

        val verdict: SignalVerdict
        val entryMin: Double
        val entryMax: Double
        val stopLoss: Double
        val target1: Double
        val target2: Double
        val actionsEn = mutableListOf<String>()
        val actionsHi = mutableListOf<String>()

        if (score >= 50) {
            verdict = SignalVerdict.STRONG_BUY
            entryMin = p0
            entryMax = p0 + 0.50
            stopLoss = s - 1.20
            target1 = pFuture
            target2 = pFuture + 2.50

            actionsEn.add("Entry Zone: ${formatPrice(entryMin)} - ${formatPrice(entryMax)}")
            actionsEn.add("Stop Loss (SL): ${formatPrice(stopLoss)} (Secured below Demand Base)")
            actionsEn.add("Target 1 (TP1): ${formatPrice(target1)} (P_future level)")
            actionsEn.add("Target 2 (TP2): ${formatPrice(target2)} (Extended Liquidity Run)")

            actionsHi.add("एंट्री ज़ोन: ${formatPrice(entryMin)} - ${formatPrice(entryMax)}")
            actionsHi.add("स्टॉप लॉस (SL): ${formatPrice(stopLoss)} (Demand Base के नीचे सुरक्षित)")
            actionsHi.add("टारगेट 1 (TP1): ${formatPrice(target1)} (P_future स्तर)")
            actionsHi.add("टारगेट 2 (TP2): ${formatPrice(target2)} (एक्सटेंडेड लिक्विडिटी रन)")
        } else if (score <= -50) {
            verdict = SignalVerdict.STRONG_SELL
            entryMin = p0 - 0.50
            entryMax = p0
            stopLoss = r + 1.20
            target1 = pFuture
            target2 = pFuture - 2.50

            actionsEn.add("Entry Zone: ${formatPrice(entryMin)} - ${formatPrice(entryMax)}")
            actionsEn.add("Stop Loss (SL): ${formatPrice(stopLoss)} (Secured above Overhead Shelf)")
            actionsEn.add("Target 1 (TP1): ${formatPrice(target1)} (P_future level)")
            actionsEn.add("Target 2 (TP2): ${formatPrice(target2)} (Extended Dump level)")

            actionsHi.add("एंट्री ज़ोन: ${formatPrice(entryMin)} - ${formatPrice(entryMax)}")
            actionsHi.add("स्टॉप लॉस (SL): ${formatPrice(stopLoss)} (Overhead Shelf के ऊपर सुरक्षित)")
            actionsHi.add("टारगेट 1 (TP1): ${formatPrice(target1)} (P_future स्तर)")
            actionsHi.add("टारगेट 2 (TP2): ${formatPrice(target2)} (एक्सटेंडेड डंप स्तर)")
        } else {
            verdict = SignalVerdict.STRICT_WAIT
            entryMin = p0
            entryMax = p0
            stopLoss = 0.0
            target1 = pFuture
            target2 = 0.0

            actionsEn.add("Market is compressed or trapped between Supply & Demand bounds.")
            actionsEn.add("Recommendation: Wait for breakout confirmation or rejection candle close.")

            actionsHi.add("मार्केट संकुचित (Compression) या रेजिस्टेंस/सपोर्ट के बीच फंसा हुआ है।")
            actionsHi.add("सलाह: ब्रेकआउट कन्फर्मेशन या स्पष्ट रिजेक्शन कैंडल क्लोज़ का इंतज़ार करें।")
        }

        return XMMResult(
            p0 = p0,
            pFuture = pFuture,
            resistance = r,
            support = s,
            velocity = v,
            acceleration = a,
            gravity = g,
            atr = atr,
            liquidityState = state,
            score = score,
            verdict = verdict,
            entryMin = entryMin,
            entryMax = entryMax,
            stopLoss = stopLoss,
            target1 = target1,
            target2 = target2,
            actionsEn = actionsEn,
            actionsHi = actionsHi
        )
    }

    private fun formatPrice(price: Double): String {
        return String.format(Locale.US, "%.2f", price)
    }
}

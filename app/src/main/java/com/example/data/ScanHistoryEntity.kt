package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.MarketParameters
import com.example.model.SignalVerdict
import com.example.model.XMMResult

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
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
    val verdict: String,
    val entryMin: Double,
    val entryMax: Double,
    val stopLoss: Double,
    val target1: Double,
    val target2: Double,
    val chartNote: String = "XAUUSD M5"
) {
    fun toDomain(): XMMResult {
        val verdictEnum = try {
            SignalVerdict.valueOf(verdict)
        } catch (e: Exception) {
            SignalVerdict.STRICT_WAIT
        }

        val params = MarketParameters(
            p0 = p0,
            resistance = resistance,
            support = support,
            velocity = velocity,
            acceleration = acceleration,
            atr = atr,
            liquidityState = liquidityState
        )
        val calculated = com.example.engine.XMMCalculator.calculate(params)

        return calculated.copy(
            id = id,
            timestamp = timestamp,
            chartNote = chartNote
        )
    }

    companion object {
        fun fromDomain(result: XMMResult): ScanHistoryEntity {
            return ScanHistoryEntity(
                id = result.id,
                timestamp = result.timestamp,
                p0 = result.p0,
                pFuture = result.pFuture,
                resistance = result.resistance,
                support = result.support,
                velocity = result.velocity,
                acceleration = result.acceleration,
                gravity = result.gravity,
                atr = result.atr,
                liquidityState = result.liquidityState,
                score = result.score,
                verdict = result.verdict.name,
                entryMin = result.entryMin,
                entryMax = result.entryMax,
                stopLoss = result.stopLoss,
                target1 = result.target1,
                target2 = result.target2,
                chartNote = result.chartNote
            )
        }
    }
}

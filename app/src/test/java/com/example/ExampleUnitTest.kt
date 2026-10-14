package com.example

import com.example.engine.XMMCalculator
import com.example.model.MarketParameters
import com.example.model.SignalVerdict
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBullishBOS_StrongBuyVerdict() {
        val params = MarketParameters(
            p0 = 2648.50,
            resistance = 2658.00,
            support = 2645.00,
            velocity = 0.45,
            acceleration = 0.05,
            atr = 3.4,
            liquidityState = "BOS"
        )
        val result = XMMCalculator.calculate(params)

        assertTrue("Expected score >= 50, but got ${result.score}", result.score >= 50)
        assertEquals(SignalVerdict.STRONG_BUY, result.verdict)
        assertTrue(result.pFuture > result.p0)
        assertEquals(params.support - 1.20, result.stopLoss, 0.01)
        assertEquals(result.pFuture, result.target1, 0.01)
        assertEquals(result.pFuture + 2.50, result.target2, 0.01)
    }

    @Test
    fun testBearishSweep_StrongSellVerdict() {
        val params = MarketParameters(
            p0 = 2662.30,
            resistance = 2664.50,
            support = 2649.00,
            velocity = -0.42,
            acceleration = -0.05,
            atr = 3.6,
            liquidityState = "Sweep"
        )
        val result = XMMCalculator.calculate(params)

        assertTrue("Expected score <= -50, but got ${result.score}", result.score <= -50)
        assertEquals(SignalVerdict.STRONG_SELL, result.verdict)
        assertTrue(result.pFuture < result.p0)
        assertEquals(params.resistance + 1.20, result.stopLoss, 0.01)
        assertEquals(result.pFuture, result.target1, 0.01)
        assertEquals(result.pFuture - 2.50, result.target2, 0.01)
    }

    @Test
    fun testRangeChop_StrictWaitVerdict() {
        val params = MarketParameters(
            p0 = 2652.10,
            resistance = 2656.00,
            support = 2647.00,
            velocity = 0.02,
            acceleration = -0.01,
            atr = 2.8,
            liquidityState = "Compression"
        )
        val result = XMMCalculator.calculate(params)

        assertEquals(SignalVerdict.STRICT_WAIT, result.verdict)
        assertTrue(result.score in -49..49)
    }
}

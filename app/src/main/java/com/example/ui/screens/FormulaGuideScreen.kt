package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NeutralOrange
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FormulaGuideScreen(
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "XMM & V9 BLUEPRINT GUIDE",
                    color = GoldBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isHindi)
                        "XAUUSD M5 के लिए क्वांटिटेटिव किनेमैटिक मॉडल और गोल्ड बाबा फॉर्मूला की पूरी जानकारी।"
                    else
                        "Complete quantitative mathematical specification of the XMM Kinematic Gold Engine.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Section 1: Kinematic Future Price
        FormulaCard(
            title = "1. KINEMATIC PRICE PROJECTION (भविष्य का मूल्य)",
            formula = "P_future = P₀ + (V · t) + ½(A · t²) + G",
            explanation = if (isHindi)
                "P₀ वर्तमान मूल्य है। V कैंडल विस्तार से मापी गई गति (pts/min) है, A त्वरण (pts/min²) है, t = 5 मिनट (M5 कैंडल अवधि) है, और G मार्केट ग्रेविटी है।"
            else
                "P₀ is active price. V is candle expansion velocity (pts/min), A is momentum acceleration (pts/min²), t = 5 minutes, and G is the gravity vector exerted by nearby order blocks."
        )

        // Section 2: Market Gravity Formula
        FormulaCard(
            title = "2. MARKET GRAVITY VECTOR (मार्केट ग्रेविटी)",
            formula = "G = k · [ (1 / d_R) - (1 / d_S) ] · ATR",
            explanation = if (isHindi)
                "रेजिस्टेंस (d_R) और सपोर्ट (d_S) की दूरी के अनुसार ग्रेविटी चुंबक की तरह काम करती है। यदि सपोर्ट केवल 1.5 अंक दूर हो (d_S ≤ 1.5) और P₀ > S तथा V ≥ 0, तो रिबाउंड की वजह से ग्रेविटी धनात्मक (+|G|) हो जाती है।"
            else
                "Order flow gravity pulls price toward overhead liquidity or demand pools. Constant k = 0.50. When price bounces within 1.5 pts of demand (d_S ≤ 1.5) with positive velocity, gravity inverts upward (+|G|)."
        )

        // Section 3: Score Matrix
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "3. MODEL SCORE MATRIX (-100 to +100)",
                    color = GoldBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                HorizontalDivider(color = BorderDark, thickness = 1.dp)

                ScoreRuleRow("Velocity Direction (V > 0 / V < 0)", "+25 / -25 pts")
                ScoreRuleRow("Acceleration Vector (A > 0 / A < 0)", "+15 / -15 pts")
                ScoreRuleRow("Projected Shift (P_future > P₀)", "+20 / -20 pts")
                ScoreRuleRow("Break of Structure (BOS Alignment)", "+15 / -15 pts")
                ScoreRuleRow("Liquidity Sweep (Rejection Alignment)", "+10 / -10 pts")
            }
        }

        // Section 4: Execution Rules
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "4. SIGNAL EXECUTION THRESHOLDS",
                    color = GoldBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                HorizontalDivider(color = BorderDark, thickness = 1.dp)

                // Strong Buy
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🟢 STRONG BUY (SCORE ≥ +50)",
                        color = BullishGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Entry: [P₀, P₀ + 0.50] | SL: Support - 1.20 | TP1: P_future | TP2: P_future + 2.50",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Strong Sell
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "🔴 STRONG SELL (SCORE ≤ -50)",
                        color = com.example.ui.theme.BearishRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Entry: [P₀ - 0.50, P₀] | SL: Resistance + 1.20 | TP1: P_future | TP2: P_future - 2.50",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Strict Wait
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "⚪ STRICT WAIT / NEUTRAL (-49 to +49)",
                        color = NeutralOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi)
                            "मार्केट संकुचित (Compression) या रेजिस्टेंस/सपोर्ट के बीच फंसा हुआ है। ट्रेड न लें।"
                        else
                            "Market is trapped in compression or equilibrium between bounds. No entry permitted.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FormulaCard(
    title: String,
    formula: String,
    explanation: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                color = GoldBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Surface(
                color = SurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formula,
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Text(
                text = explanation,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ScoreRuleRow(rule: String, points: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = rule, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = points,
            color = GoldAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

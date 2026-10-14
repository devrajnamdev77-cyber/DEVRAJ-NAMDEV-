package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SampleChart
import com.example.model.SignalVerdict
import com.example.model.XMMResult
import com.example.ui.theme.BearishBg
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.BullishBg
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NeutralBg
import com.example.ui.theme.NeutralOrange
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun SignalBadge(verdict: SignalVerdict, isHindi: Boolean, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label, icon) = when (verdict) {
        SignalVerdict.STRONG_BUY -> Quad(
            BullishBg,
            BullishGreen,
            if (isHindi) "STRONG BUY 🟢" else "STRONG BUY 🟢",
            Icons.Default.TrendingUp
        )
        SignalVerdict.STRONG_SELL -> Quad(
            BearishBg,
            BearishRed,
            if (isHindi) "STRONG SELL 🔴" else "STRONG SELL 🔴",
            Icons.Default.TrendingDown
        )
        SignalVerdict.STRICT_WAIT -> Quad(
            NeutralBg,
            NeutralOrange,
            if (isHindi) "STRICT WAIT / NEUTRAL ⚪" else "STRICT WAIT / NEUTRAL ⚪",
            Icons.Default.TrendingFlat
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor),
        modifier = modifier.testTag("signal_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun ScoreDisplay(score: Int, modifier: Modifier = Modifier) {
    val scoreColor = when {
        score >= 50 -> BullishGreen
        score <= -50 -> BearishRed
        else -> NeutralOrange
    }
    val scoreText = if (score > 0) "+$score" else "$score"

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "SCORE:",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = scoreText,
                color = scoreColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
        // Visual Score Bar
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(BorderDark)
        ) {
            val progressFraction = ((score + 100) / 200f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .height(6.dp)
                    .background(scoreColor)
            )
        }
    }
}

@Composable
fun MetricsTableCard(result: XMMResult, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "KINEMATIC MODEL METRICS",
                    color = GoldBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                // Liquidity Badge
                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Text(
                        text = result.liquidityState.uppercase(Locale.US),
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderDark, thickness = 1.dp)

            MetricRow(
                label = "Active Price (P₀):",
                value = String.format(Locale.US, "$%.2f", result.p0),
                valueColor = TextPrimary
            )
            MetricRow(
                label = "Future Calculated (P_future):",
                value = String.format(Locale.US, "$%.2f", result.pFuture),
                valueColor = if (result.pFuture > result.p0) BullishGreen else BearishRed,
                isHighlight = true
            )
            MetricRow(
                label = "Overhead Resistance (R):",
                value = String.format(Locale.US, "$%.2f", result.resistance),
                valueColor = BearishRed
            )
            MetricRow(
                label = "Demand Support (S):",
                value = String.format(Locale.US, "$%.2f", result.support),
                valueColor = BullishGreen
            )
            MetricRow(
                label = "Velocity (V):",
                value = String.format(Locale.US, "%s%.2f pts/min", if (result.velocity > 0) "+" else "", result.velocity),
                valueColor = if (result.velocity > 0) BullishGreen else if (result.velocity < 0) BearishRed else TextMuted
            )
            MetricRow(
                label = "Acceleration (A):",
                value = String.format(Locale.US, "%s%.2f pts/min²", if (result.acceleration > 0) "+" else "", result.acceleration),
                valueColor = if (result.acceleration > 0) BullishGreen else if (result.acceleration < 0) BearishRed else TextMuted
            )
            MetricRow(
                label = "Market Gravity (G):",
                value = String.format(Locale.US, "%s%.2f", if (result.gravity > 0) "+" else "", result.gravity),
                valueColor = ElectricBlue
            )
            MetricRow(
                label = "ATR(14) Estimate:",
                value = String.format(Locale.US, "%.2f pts", result.atr),
                valueColor = TextSecondary
            )
        }
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    valueColor: Color,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isHighlight) TextPrimary else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (isHighlight) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = if (isHighlight) 14.sp else 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ActionPlanCard(
    result: XMMResult,
    isHindi: Boolean,
    onCopySignal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (isHindi) result.actionsHi else result.actionsEn

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi) "ACTION PLAN & EXECUTION LEVELS" else "ACTION PLAN & EXECUTION LEVELS",
                    color = GoldBright,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                IconButton(
                    onClick = onCopySignal,
                    modifier = Modifier.size(36.dp).testTag("copy_signal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Signal",
                        tint = GoldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = BorderDark, thickness = 1.dp)

            items.forEach { note ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        color = GoldAccent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = note,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            if (result.verdict != SignalVerdict.STRICT_WAIT) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = CardBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "SL DIST", color = TextMuted, fontSize = 11.sp)
                            val slDist = Math.abs(result.p0 - result.stopLoss)
                            Text(
                                text = String.format(Locale.US, "%.2f pts", slDist),
                                color = BearishRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TP1 DIST", color = TextMuted, fontSize = 11.sp)
                            val tpDist = Math.abs(result.target1 - result.p0)
                            Text(
                                text = String.format(Locale.US, "%.2f pts", tpDist),
                                color = BullishGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "R : R", color = TextMuted, fontSize = 11.sp)
                            val slDist = Math.max(0.1, Math.abs(result.p0 - result.stopLoss))
                            val tpDist = Math.abs(result.target1 - result.p0)
                            val rr = tpDist / slDist
                            Text(
                                text = String.format(Locale.US, "1 : %.2f", rr),
                                color = GoldBright,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChartUploadBox(
    previewBitmap: Bitmap?,
    isHindi: Boolean,
    onSelectImage: () -> Unit,
    onClearImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(
                width = 2.dp,
                color = if (previewBitmap != null) ElectricBlue else BorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelectImage() }
            .padding(16.dp)
            .testTag("upload_chart_box"),
        contentAlignment = Alignment.Center
    ) {
        if (previewBitmap != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = "Chart Screenshot Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BullishGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isHindi) "✓ चार्ट लोड हो चुका है" else "✓ Chart Loaded",
                            color = BullishGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    OutlinedButton(
                        onClick = onClearImage,
                        modifier = Modifier.height(32.dp).testTag("clear_image_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear",
                            tint = BearishRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "हटाएं" else "Clear",
                            color = BearishRed,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 14.dp)
            ) {
                Surface(
                    color = SurfaceElevated,
                    shape = CircleShape,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Text(
                    text = if (isHindi) "📁 स्क्रीनशॉट चुनने के लिए यहाँ टैप करें" else "📁 Tap to select TradingView M5 screenshot",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isHindi) "XAUUSD M5 चार्ट ( गैलरी या सैम्पल चार्ट )" else "XAUUSD M5 chart (Gallery or quick sample)",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun QuickSampleChips(
    samples: List<SampleChart>,
    selectedSample: SampleChart?,
    onSelectSample: (SampleChart) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "QUICK TEST SAMPLES (सैम्पल चार्ट्स):",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            samples.forEach { sample ->
                val isSelected = selectedSample?.id == sample.id
                Surface(
                    color = if (isSelected) SurfaceElevated else CardBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) GoldAccent else BorderDark
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectSample(sample) }
                        .testTag("sample_chip_${sample.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = sample.title,
                            color = if (isSelected) GoldBright else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = sample.subtitle,
                            color = TextMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingAnalysisSpinner(statusText: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = pulse)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CircularProgressIndicator(
                color = GoldAccent,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = "XMM KINEMATIC SCANNING",
                    color = GoldBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = statusText,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

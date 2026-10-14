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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TradeViewModel
import com.example.ui.components.ActionPlanCard
import com.example.ui.components.MetricsTableCard
import com.example.ui.components.ScoreDisplay
import com.example.ui.components.SignalBadge
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ManualLabScreen(
    viewModel: TradeViewModel,
    modifier: Modifier = Modifier
) {
    val manualParams by viewModel.manualParams.collectAsState()
    val manualResult by viewModel.manualResult.collectAsState()
    val isBilingual by viewModel.isBilingual.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                    text = "XMM KINEMATIC SIMULATOR",
                    color = GoldBright,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tweak gold physics parameters in real time to test kinematic trajectory & gravity shifts.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Live Simulation Result Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SignalBadge(verdict = manualResult.verdict, isHindi = isBilingual)
                ScoreDisplay(score = manualResult.score)
            }
        }

        // Sliders & Controls Card
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "PARAMETER CONTROLS",
                    color = GoldBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Current Price P0 Slider
                ParameterSlider(
                    title = "Active Price (P₀)",
                    valueText = String.format(Locale.US, "$%.2f", manualParams.p0),
                    value = manualParams.p0.toFloat(),
                    valueRange = 2620f..2680f,
                    steps = 120,
                    onValueChange = { viewModel.updateManualParams(p0 = (it * 100).roundToInt() / 100.0) }
                )

                // Overhead Resistance R Slider
                ParameterSlider(
                    title = "Overhead Resistance (R)",
                    valueText = String.format(Locale.US, "$%.2f", manualParams.resistance),
                    value = manualParams.resistance.toFloat(),
                    valueRange = 2630f..2700f,
                    steps = 140,
                    onValueChange = { viewModel.updateManualParams(r = (it * 100).roundToInt() / 100.0) }
                )

                // Demand Support S Slider
                ParameterSlider(
                    title = "Demand Support (S)",
                    valueText = String.format(Locale.US, "$%.2f", manualParams.support),
                    value = manualParams.support.toFloat(),
                    valueRange = 2600f..2670f,
                    steps = 140,
                    onValueChange = { viewModel.updateManualParams(s = (it * 100).roundToInt() / 100.0) }
                )

                // Velocity V Slider
                ParameterSlider(
                    title = "Velocity (V)",
                    valueText = String.format(Locale.US, "%s%.2f pts/min", if (manualParams.velocity > 0) "+" else "", manualParams.velocity),
                    value = manualParams.velocity.toFloat(),
                    valueRange = -0.80f..0.80f,
                    steps = 32,
                    onValueChange = { viewModel.updateManualParams(v = (it * 100).roundToInt() / 100.0) }
                )

                // Acceleration A Slider
                ParameterSlider(
                    title = "Acceleration (A)",
                    valueText = String.format(Locale.US, "%s%.3f pts/min²", if (manualParams.acceleration > 0) "+" else "", manualParams.acceleration),
                    value = manualParams.acceleration.toFloat(),
                    valueRange = -0.10f..0.10f,
                    steps = 40,
                    onValueChange = { viewModel.updateManualParams(a = (it * 1000).roundToInt() / 1000.0) }
                )

                // ATR(14) Slider
                ParameterSlider(
                    title = "ATR(14) Volatility",
                    valueText = String.format(Locale.US, "%.2f pts", manualParams.atr),
                    value = manualParams.atr.toFloat(),
                    valueRange = 1.5f..6.0f,
                    steps = 45,
                    onValueChange = { viewModel.updateManualParams(atr = (it * 10).roundToInt() / 10.0) }
                )

                // Liquidity State Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Liquidity State:",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("BOS", "Sweep", "Retest", "Compression").forEach { state ->
                            val isSelected = manualParams.liquidityState.equals(state, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateManualParams(state = state) },
                                label = { Text(state, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAccent,
                                    selectedLabelColor = androidx.compose.ui.graphics.Color.Black,
                                    containerColor = CardBackground,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) GoldAccent else BorderDark,
                                    selectedBorderColor = GoldAccent,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier.weight(1f).testTag("chip_state_$state")
                            )
                        }
                    }
                }
            }
        }

        // Live Model Metrics Table
        MetricsTableCard(result = manualResult)

        // Action Plan & Levels Card
        ActionPlanCard(
            result = manualResult,
            isHindi = isBilingual,
            onCopySignal = { viewModel.copySignalToClipboard(manualResult) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ParameterSlider(
    title: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextMuted, fontSize = 12.sp)
            Text(
                text = valueText,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = GoldAccent,
                activeTrackColor = ElectricBlue,
                inactiveTrackColor = BorderDark
            )
        )
    }
}

package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScanUiState
import com.example.ui.TradeViewModel
import com.example.ui.components.ActionPlanCard
import com.example.ui.components.ChartUploadBox
import com.example.ui.components.LoadingAnalysisSpinner
import com.example.ui.components.MetricsTableCard
import com.example.ui.components.QuickSampleChips
import com.example.ui.components.ScoreDisplay
import com.example.ui.components.SignalBadge
import com.example.ui.theme.BearishBg
import com.example.ui.theme.BearishRed
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
fun ScannerScreen(
    viewModel: TradeViewModel,
    onOpenApiKeyDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val apiKey by viewModel.apiKey.collectAsState()
    val isBilingual by viewModel.isBilingual.collectAsState()
    val scanState by viewModel.scanUiState.collectAsState()
    val currentBitmap by viewModel.currentBitmap.collectAsState()
    val selectedSample by viewModel.selectedSample.collectAsState()

    // Zero-permission Android Photo Picker for picking TradingView screenshots
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "XMM & V9 GOLD ENGINE",
                    color = GoldBright,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Autonomous XAUUSD M5 Analytical Model",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // API Key Status Banner
        Surface(
            color = if (apiKey.isNotBlank()) SurfaceElevated else CardBackground,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (apiKey.isNotBlank()) BorderDark else NeutralOrange
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenApiKeyDialog() }
                .testTag("api_key_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = if (apiKey.isNotBlank()) BullishGreen else NeutralOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (apiKey.isNotBlank()) "Gemini AI: Connected" else "Gemini API Key Needed",
                            color = if (apiKey.isNotBlank()) BullishGreen else NeutralOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (apiKey.isNotBlank()) "Key active • Tap to configure" else "Tap to enter Google AI Studio Key",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                ) {
                    Text(
                        text = "CONFIG",
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Quick Test Samples
        QuickSampleChips(
            samples = viewModel.sampleCharts,
            selectedSample = selectedSample,
            onSelectSample = { viewModel.selectSampleChart(it) }
        )

        // Upload Box
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "TRADINGVIEW M5 CHART SCREENSHOT:",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            ChartUploadBox(
                previewBitmap = currentBitmap,
                isHindi = isBilingual,
                onSelectImage = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onClearImage = { viewModel.clearImage() }
            )
        }

        // Submit Scan Button
        val isLoading = scanState is ScanUiState.Loading
        Button(
            onClick = { viewModel.executeScan() },
            enabled = !isLoading && currentBitmap != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                contentColor = Color.White,
                disabledContainerColor = SurfaceElevated,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("analyze_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "EXECUTE FORMULA & SCAN",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Loading Spinner
        AnimatedVisibility(
            visible = scanState is ScanUiState.Loading,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (scanState is ScanUiState.Loading) {
                LoadingAnalysisSpinner(
                    statusText = (scanState as ScanUiState.Loading).status
                )
            }
        }

        // Error Banner
        AnimatedVisibility(
            visible = scanState is ScanUiState.Error,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (scanState is ScanUiState.Error) {
                Surface(
                    color = BearishBg,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BearishRed),
                    modifier = Modifier.fillMaxWidth().testTag("scan_error_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = BearishRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = (scanState as ScanUiState.Error).message,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Scan Results Card
        AnimatedVisibility(
            visible = scanState is ScanUiState.Success,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (scanState is ScanUiState.Success) {
                val result = (scanState as ScanUiState.Success).result
                Column(
                    modifier = Modifier.fillMaxWidth().testTag("result_card"),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header with Signal Badge & Score Box
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
                            SignalBadge(verdict = result.verdict, isHindi = isBilingual)
                            ScoreDisplay(score = result.score)
                        }
                    }

                    // Metrics Table Card
                    MetricsTableCard(result = result)

                    // Action Plan & Levels Card
                    ActionPlanCard(
                        result = result,
                        isHindi = isBilingual,
                        onCopySignal = { viewModel.copySignalToClipboard(result) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

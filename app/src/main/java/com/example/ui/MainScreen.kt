package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.TerminalDialog
import com.example.ui.screens.FormulaGuideScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ManualLabScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AppTab(val titleEn: String, val titleHi: String) {
    SCANNER("Scanner", "स्कैनर"),
    LAB("Simulator", "लैब"),
    HISTORY("History", "हिस्ट्री"),
    GUIDE("Formula", "फॉर्मूला")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TradeViewModel) {
    var selectedTab by remember { mutableStateOf(AppTab.SCANNER) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showTerminalDialog by remember { mutableStateOf(false) }

    val apiKey by viewModel.apiKey.collectAsState()
    val isBilingual by viewModel.isBilingual.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Surface(
                            color = SurfaceElevated,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "GB",
                                    color = GoldBright,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        Text(
                            text = "  GOLD BABA",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Surface(
                            color = SurfaceElevated,
                            shape = CircleShape,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "XMM V9",
                                color = GoldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Bilingual toggle button
                    IconButton(
                        onClick = { viewModel.toggleBilingual() },
                        modifier = Modifier.testTag("toggle_lang_btn")
                    ) {
                        Surface(
                            color = if (isBilingual) SurfaceElevated else CardBackground,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isBilingual) GoldAccent else BorderDark)
                        ) {
                            Text(
                                text = if (isBilingual) "हिन्दी" else "EN",
                                color = if (isBilingual) GoldBright else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Terminal Console Button
                    IconButton(
                        onClick = { showTerminalDialog = true },
                        modifier = Modifier.testTag("top_bar_terminal_btn")
                    ) {
                        Surface(
                            color = SurfaceElevated,
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ">_",
                                    color = GoldBright,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Key Config Button
                    IconButton(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier.testTag("top_bar_key_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key",
                            tint = if (apiKey.isNotBlank()) GoldAccent else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBackground,
                contentColor = TextSecondary,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == AppTab.SCANNER,
                    onClick = { selectedTab = AppTab.SCANNER },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = "Scanner",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBilingual) AppTab.SCANNER.titleHi else AppTab.SCANNER.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == AppTab.SCANNER) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldBright,
                        selectedTextColor = GoldBright,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("nav_scanner")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.LAB,
                    onClick = { selectedTab = AppTab.LAB },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Simulator",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBilingual) AppTab.LAB.titleHi else AppTab.LAB.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == AppTab.LAB) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldBright,
                        selectedTextColor = GoldBright,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("nav_lab")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.HISTORY,
                    onClick = { selectedTab = AppTab.HISTORY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBilingual) AppTab.HISTORY.titleHi else AppTab.HISTORY.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == AppTab.HISTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldBright,
                        selectedTextColor = GoldBright,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("nav_history")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.GUIDE,
                    onClick = { selectedTab = AppTab.GUIDE },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Formula Guide",
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isBilingual) AppTab.GUIDE.titleHi else AppTab.GUIDE.titleEn,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == AppTab.GUIDE) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoldBright,
                        selectedTextColor = GoldBright,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    ),
                    modifier = Modifier.testTag("nav_guide")
                )
            }
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                AppTab.SCANNER -> ScannerScreen(
                    viewModel = viewModel,
                    onOpenApiKeyDialog = { showApiKeyDialog = true }
                )
                AppTab.LAB -> ManualLabScreen(
                    viewModel = viewModel
                )
                AppTab.HISTORY -> HistoryScreen(
                    viewModel = viewModel
                )
                AppTab.GUIDE -> FormulaGuideScreen(
                    isHindi = isBilingual
                )
            }
        }
    }

    if (showApiKeyDialog) {
        ApiKeyDialog(
            initialKey = apiKey,
            onSaveKey = { newKey -> viewModel.setApiKey(newKey) },
            onDismiss = { showApiKeyDialog = false }
        )
    }

    if (showTerminalDialog) {
        TerminalDialog(
            logs = terminalLogs,
            onClearLogs = { viewModel.clearTerminalLogs() },
            onExecuteCommand = { cmd -> viewModel.executeTerminalCommand(cmd) },
            onDismiss = { showTerminalDialog = false }
        )
    }
}

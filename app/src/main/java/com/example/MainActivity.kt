package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.ui.ApexAutoViewModel
import com.example.ui.components.CarScreen
import com.example.ui.components.CarSideNavRail
import com.example.ui.components.CarTopBar
import com.example.ui.components.CustomizeCardsDialog
import com.example.ui.components.GeminiVoiceDialog
import com.example.ui.components.SpeedometerOverlay
import com.example.ui.screens.AppDrawerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MediaScreen
import com.example.ui.screens.NavigationScreen
import com.example.ui.screens.ObdDiagnosticsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ApexAutoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isNightMode by viewModel.isNightMode.collectAsState()

            MyApplicationTheme(darkTheme = isNightMode) {
                // Request permissions at runtime
                RequestAutomotivePermissions()

                CarHeadUnitApp(viewModel = viewModel)
            }
        }
    }

    @Composable
    private fun RequestAutomotivePermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { _ -> }

        LaunchedEffect(Unit) {
            val needed = permissions.filter {
                ContextCompat.checkSelfPermission(this@MainActivity, it) != PackageManager.PERMISSION_GRANTED
            }
            if (needed.isNotEmpty()) {
                launcher.launch(needed.toTypedArray())
            }
        }
    }
}

@Composable
fun CarHeadUnitApp(viewModel: ApexAutoViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isNightMode by viewModel.isNightMode.collectAsState()
    val isMph by viewModel.isMph.collectAsState()
    val showSpeedOverlay by viewModel.showSpeedOverlay.collectAsState()
    val showCustomizeDialog by viewModel.showCustomizeDialog.collectAsState()
    val showVoiceDialog by viewModel.showVoiceDialog.collectAsState()

    val cardConfigs by viewModel.cardConfigs.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val navigationState by viewModel.navigationState.collectAsState()
    val musicState by viewModel.musicState.collectAsState()
    val radioStation by viewModel.currentRadioStation.collectAsState()
    val radioPresets by viewModel.radioPresets.collectAsState()
    val youTubeItems by viewModel.youTubeItems.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

    val isListening by viewModel.isListening.collectAsState()
    val isVoiceLoading by viewModel.isVoiceLoading.collectAsState()
    val lastVoiceQuery by viewModel.lastVoiceQuery.collectAsState()
    val voiceReply by viewModel.voiceReply.collectAsState()

    val isScanningBluetooth by viewModel.obdManager.isScanning.collectAsState()
    val discoveredDevices by viewModel.obdManager.discoveredDevices.collectAsState()

    // BackHandler to safely return to Dashboard screen on hardware back
    BackHandler(enabled = currentScreen != CarScreen.DASHBOARD) {
        viewModel.selectScreen(CarScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            CarTopBar(
                isNightMode = isNightMode,
                onToggleNightMode = viewModel::toggleNightMode,
                onOpenVoiceAssistant = viewModel::openVoiceDialog,
                onOpenCustomizeCards = viewModel::openCustomizeDialog,
                obdConnected = telemetry.obdConnected,
                tempC = weatherState.tempCelsius,
                condition = weatherState.condition
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Side Navigation Rail
                CarSideNavRail(
                    currentScreen = currentScreen,
                    onSelectScreen = viewModel::selectScreen,
                    showSpeedOverlay = showSpeedOverlay,
                    onToggleSpeedOverlay = viewModel::toggleSpeedOverlay
                )

                // Main Screen Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    when (currentScreen) {
                        CarScreen.DASHBOARD -> {
                            DashboardScreen(
                                cardConfigs = cardConfigs,
                                telemetry = telemetry,
                                navigationState = navigationState,
                                musicState = musicState,
                                radioStation = radioStation,
                                radioPresets = radioPresets,
                                weather = weatherState,
                                youTubeItems = youTubeItems,
                                isMph = isMph,
                                isNightMode = isNightMode,
                                showSpeedOverlay = showSpeedOverlay,
                                onToggleUnit = viewModel::toggleSpeedUnit,
                                onToggleNightMode = viewModel::toggleNightMode,
                                onToggleSpeedOverlay = viewModel::toggleSpeedOverlay,
                                onTriggerSimulatedFault = viewModel.obdManager::triggerSimulatedFault,
                                onOpenFullMap = { viewModel.selectScreen(CarScreen.NAVIGATION) },
                                onOpenDiagnostics = { viewModel.selectScreen(CarScreen.OBD_TELEMETRY) },
                                onTogglePlayMusic = viewModel::togglePlayMusic,
                                onNextTrack = viewModel::nextTrack,
                                onPrevTrack = viewModel::prevTrack,
                                onSelectRadioPreset = viewModel::selectRadioPreset,
                                onSeekRadioPrev = viewModel::seekRadioPrev,
                                onSeekRadioNext = viewModel::seekRadioNext,
                                onToggleRadioBand = viewModel::toggleRadioBand,
                                onLaunchYouTube = viewModel::launchYouTube
                            )
                        }
                        CarScreen.NAVIGATION -> {
                            NavigationScreen(
                                navigationState = navigationState,
                                onSelectDestination = viewModel::selectDestination
                            )
                        }
                        CarScreen.MEDIA -> {
                            MediaScreen(
                                musicState = musicState,
                                radioStation = radioStation,
                                radioPresets = radioPresets,
                                youTubeItems = youTubeItems,
                                isMoving = telemetry.speedKmh > 2f,
                                onTogglePlayMusic = viewModel::togglePlayMusic,
                                onNextTrack = viewModel::nextTrack,
                                onPrevTrack = viewModel::prevTrack,
                                onSelectRadioPreset = viewModel::selectRadioPreset,
                                onSeekRadioPrev = viewModel::seekRadioPrev,
                                onSeekRadioNext = viewModel::seekRadioNext,
                                onToggleRadioBand = viewModel::toggleRadioBand,
                                onLaunchYouTube = viewModel::launchYouTube
                            )
                        }
                        CarScreen.OBD_TELEMETRY -> {
                            ObdDiagnosticsScreen(
                                telemetry = telemetry,
                                isScanningBluetooth = isScanningBluetooth,
                                discoveredDevices = discoveredDevices,
                                onScanBluetooth = viewModel.obdManager::scanBluetoothDevices,
                                onConnectDevice = viewModel.obdManager::connectDevice,
                                onClearDtcCodes = viewModel.obdManager::clearDtcCodes,
                                onTriggerTestFault = viewModel.obdManager::triggerSimulatedFault,
                                onAskGeminiAboutDtc = { code ->
                                    viewModel.openVoiceDialog()
                                    viewModel.sendVoiceQuery("Explain vehicle diagnostic error code $code, its causes and how to fix it.")
                                }
                            )
                        }
                        CarScreen.APP_DRAWER -> {
                            AppDrawerScreen(
                                installedApps = installedApps,
                                onLaunchApp = viewModel::launchApp
                            )
                        }
                    }

                    // Floating Speedometer Overlay (when enabled, visible over any screen)
                    if (showSpeedOverlay) {
                        SpeedometerOverlay(
                            telemetry = telemetry,
                            isMph = isMph,
                            onDismiss = viewModel::toggleSpeedOverlay,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        )
                    }
                }
            }

            // Customize Cards Modal
            if (showCustomizeDialog) {
                CustomizeCardsDialog(
                    configs = cardConfigs,
                    onToggleCardVisibility = viewModel::toggleCardVisibility,
                    onMoveCardUp = viewModel::moveCardUp,
                    onMoveCardDown = viewModel::moveCardDown,
                    onResetDefaults = viewModel::resetCardDefaults,
                    onDismiss = viewModel::dismissCustomizeDialog
                )
            }

            // Gemini Voice Assistant Dialog
            if (showVoiceDialog) {
                GeminiVoiceDialog(
                    isListening = isListening,
                    isLoading = isVoiceLoading,
                    lastUserQuery = lastVoiceQuery,
                    assistantReply = voiceReply,
                    onSendQuery = viewModel::sendVoiceQuery,
                    onDismiss = viewModel::dismissVoiceDialog
                )
            }
        }
    }
}

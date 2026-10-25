package com.example.ui

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiVoiceAssistant
import com.example.data.location.CarLocationManager
import com.example.data.model.DashboardCardConfig
import com.example.data.model.DashboardCardType
import com.example.data.model.InstalledAppItem
import com.example.data.model.MusicState
import com.example.data.model.NavigationState
import com.example.data.model.RadioStation
import com.example.data.model.VehicleTelemetry
import com.example.data.model.WeatherData
import com.example.data.model.YouTubeCardItem
import com.example.data.obd.ObdManager
import com.example.ui.components.CarScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ApexAutoViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    val obdManager = ObdManager(context)
    val locationManager = CarLocationManager(context)
    val voiceAssistant = GeminiVoiceAssistant(context)

    // Current Screen
    private val _currentScreen = MutableStateFlow(CarScreen.DASHBOARD)
    val currentScreen: StateFlow<CarScreen> = _currentScreen.asStateFlow()

    // Night Mode Theme
    private val _isNightMode = MutableStateFlow(true)
    val isNightMode: StateFlow<Boolean> = _isNightMode.asStateFlow()

    // Speed unit MPH vs KM/H
    private val _isMph = MutableStateFlow(true)
    val isMph: StateFlow<Boolean> = _isMph.asStateFlow()

    // Floating Speed HUD Overlay
    private val _showSpeedOverlay = MutableStateFlow(false)
    val showSpeedOverlay: StateFlow<Boolean> = _showSpeedOverlay.asStateFlow()

    // Customize Cards Dialog visibility
    private val _showCustomizeDialog = MutableStateFlow(false)
    val showCustomizeDialog: StateFlow<Boolean> = _showCustomizeDialog.asStateFlow()

    // Voice Command Dialog visibility
    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    // Voice assistant state
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isVoiceLoading = MutableStateFlow(false)
    val isVoiceLoading: StateFlow<Boolean> = _isVoiceLoading.asStateFlow()

    private val _lastVoiceQuery = MutableStateFlow("")
    val lastVoiceQuery: StateFlow<String> = _lastVoiceQuery.asStateFlow()

    private val _voiceReply = MutableStateFlow("")
    val voiceReply: StateFlow<String> = _voiceReply.asStateFlow()

    // Dashboard Cards Configuration
    private val defaultConfigs = listOf(
        DashboardCardConfig(DashboardCardType.SPEEDOMETER, isVisible = true, order = 0),
        DashboardCardConfig(DashboardCardType.NAVIGATION, isVisible = true, order = 1),
        DashboardCardConfig(DashboardCardType.MUSIC, isVisible = true, order = 2),
        DashboardCardConfig(DashboardCardType.RADIO, isVisible = true, order = 3),
        DashboardCardConfig(DashboardCardType.WEATHER, isVisible = true, order = 4),
        DashboardCardConfig(DashboardCardType.OBD_DIAGNOSTICS, isVisible = true, order = 5),
        DashboardCardConfig(DashboardCardType.YOUTUBE, isVisible = true, order = 6),
        DashboardCardConfig(DashboardCardType.QUICK_TOGGLES, isVisible = true, order = 7)
    )

    private val _cardConfigs = MutableStateFlow(defaultConfigs)
    val cardConfigs: StateFlow<List<DashboardCardConfig>> = _cardConfigs.asStateFlow()

    // Vehicle Telemetry
    val telemetry: StateFlow<VehicleTelemetry> = obdManager.telemetry

    // Navigation State
    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    // Music State
    private val sampleTracks = listOf(
        MusicState("Midnight Highway", "Synthwave Roadsters", "Turbo Nights 2026", 248, 82, true, 0.8f, "Spotify", 0xFFE50914),
        MusicState("Neon Velocity", "Apex Electro", "Overdrive FM", 215, 34, true, 0.8f, "Car Sync", 0xFF9E0B19),
        MusicState("Cruising Coastline", "Sunset Drifters", "Pacific Echoes", 270, 110, true, 0.8f, "USB Media", 0xFFB71C1C)
    )
    private var currentTrackIndex = 0
    private val _musicState = MutableStateFlow(sampleTracks[0])
    val musicState: StateFlow<MusicState> = _musicState.asStateFlow()

    // Radio State
    private val radioStations = listOf(
        RadioStation("r1", "Pure Rock 98.7", "98.7 FM", "FM", "Classic Rock", "Deep Purple - Highway Star", true),
        RadioStation("r2", "Electro Beats 101.5", "101.5 FM", "FM", "EDM / Synth", "Kavinsky - Nightcall", true),
        RadioStation("r3", "Top 40 Live 104.3", "104.3 FM", "FM", "Pop Hits", "Dua Lipa - Levitating", true),
        RadioStation("r4", "NPR News & Road", "88.5 FM", "FM", "News & Traffic", "Hourly Drive Bulletin", false),
        RadioStation("r5", "Smooth Jazz 94.1", "94.1 FM", "FM", "Jazz", "Miles Davis - So What", false),
        RadioStation("r6", "Country Road 107.9", "107.9 FM", "FM", "Country", "Luke Combs - Fast Car", false)
    )
    private val _currentRadioStation = MutableStateFlow(radioStations[0])
    val currentRadioStation: StateFlow<RadioStation> = _currentRadioStation.asStateFlow()

    private val _radioPresets = MutableStateFlow(radioStations)
    val radioPresets: StateFlow<List<RadioStation>> = _radioPresets.asStateFlow()

    // YouTube Items
    private val _youTubeItems = MutableStateFlow(
        listOf(
            YouTubeCardItem("yt1", "2026 Hypercar Canyon Run • POV 4K", "Apex Motorsport", "12:45", "Automotive", "1.4M views"),
            YouTubeCardItem("yt2", "Trackday Exhaust Sound & Drift Highlights", "Speed Hunters", "08:19", "Motorsport", "850K views"),
            YouTubeCardItem("yt3", "Cross Country Road Trip Highway Beats", "Drive Music", "45:00", "Music", "2.1M views"),
            YouTubeCardItem("yt4", "Top 10 Car Care & ECU Diagnostics Hacks", "Garage Tech", "16:30", "Tech", "540K views")
        )
    )
    val youTubeItems: StateFlow<List<YouTubeCardItem>> = _youTubeItems.asStateFlow()

    // Weather State
    private val _weatherState = MutableStateFlow(WeatherData())
    val weatherState: StateFlow<WeatherData> = _weatherState.asStateFlow()

    // Installed Apps
    private val _installedApps = MutableStateFlow<List<InstalledAppItem>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppItem>> = _installedApps.asStateFlow()

    private var musicPlaybackTicker: Job? = null

    init {
        locationManager.startListening()
        loadInstalledApps()
        startMusicTicker()
    }

    private fun startMusicTicker() {
        musicPlaybackTicker?.cancel()
        musicPlaybackTicker = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                if (_musicState.value.isPlaying) {
                    _musicState.update { current ->
                        val nextPos = if (current.currentPositionSec >= current.durationSec) 0 else current.currentPositionSec + 1
                        current.copy(currentPositionSec = nextPos)
                    }
                }
            }
        }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val apps = mutableListOf<InstalledAppItem>()
            try {
                val resolveInfos = pm.queryIntentActivities(intent, 0)
                for (info in resolveInfos) {
                    val label = info.loadLabel(pm).toString()
                    val pkg = info.activityInfo.packageName
                    if (pkg != context.packageName) {
                        val isAuto = label.contains("Map", true) ||
                                label.contains("Music", true) ||
                                label.contains("Radio", true) ||
                                label.contains("Waze", true) ||
                                label.contains("Spotify", true) ||
                                label.contains("YouTube", true)
                        apps.add(InstalledAppItem(label, pkg, isAuto))
                    }
                }
            } catch (_: Exception) {}

            // Always ensure key automotive and utility launchers exist
            if (apps.isEmpty() || apps.size < 6) {
                val fallbacks = listOf(
                    InstalledAppItem("Google Maps", "com.google.android.apps.maps", true),
                    InstalledAppItem("Spotify Music", "com.spotify.music", true),
                    InstalledAppItem("Waze Navigation", "com.waze", true),
                    InstalledAppItem("YouTube", "com.google.android.youtube", true),
                    InstalledAppItem("Vehicle Settings", "com.android.settings", true),
                    InstalledAppItem("Phone / Dialer", "com.android.dialer", true),
                    InstalledAppItem("Messages", "com.android.mms", false),
                    InstalledAppItem("Chrome Browser", "com.android.chrome", false)
                )
                apps.addAll(fallbacks.filter { fb -> apps.none { it.packageName == fb.packageName } })
            }
            _installedApps.value = apps.sortedByDescending { it.isAutomotiveRecommended }
        }
    }

    fun selectScreen(screen: CarScreen) {
        _currentScreen.value = screen
    }

    fun toggleNightMode() {
        _isNightMode.value = !_isNightMode.value
    }

    fun toggleSpeedUnit() {
        _isMph.value = !_isMph.value
    }

    fun toggleSpeedOverlay() {
        _showSpeedOverlay.value = !_showSpeedOverlay.value
    }

    fun openCustomizeDialog() {
        _showCustomizeDialog.value = true
    }

    fun dismissCustomizeDialog() {
        _showCustomizeDialog.value = false
    }

    fun openVoiceDialog() {
        _showVoiceDialog.value = true
        _isListening.value = true
    }

    fun dismissVoiceDialog() {
        _showVoiceDialog.value = false
        _isListening.value = false
        voiceAssistant.stopSpeaking()
    }

    // Voice query trigger
    fun sendVoiceQuery(query: String) {
        _lastVoiceQuery.value = query
        _isListening.value = false
        _isVoiceLoading.value = true
        viewModelScope.launch {
            val curTele = telemetry.value
            val contextInfo = "Speed: ${curTele.speedKmh.toInt()} km/h, DTC: ${curTele.dtcCodes.joinToString { it.code }}, Nav: ${_navigationState.value.destinationName}"
            val reply = voiceAssistant.queryGemini(query, contextInfo)
            _voiceReply.value = reply
            _isVoiceLoading.value = false
        }
    }

    // Music controls
    fun togglePlayMusic() {
        _musicState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun nextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % sampleTracks.size
        _musicState.value = sampleTracks[currentTrackIndex]
    }

    fun prevTrack() {
        currentTrackIndex = if (currentTrackIndex - 1 < 0) sampleTracks.size - 1 else currentTrackIndex - 1
        _musicState.value = sampleTracks[currentTrackIndex]
    }

    // Radio controls
    fun selectRadioPreset(station: RadioStation) {
        _currentRadioStation.value = station
        Toast.makeText(context, "Tuned to ${station.name}", Toast.LENGTH_SHORT).show()
    }

    fun seekRadioNext() {
        val curIdx = radioStations.indexOfFirst { it.id == _currentRadioStation.value.id }
        val nextIdx = (curIdx + 1).coerceAtMost(radioStations.size - 1)
        _currentRadioStation.value = radioStations[nextIdx]
    }

    fun seekRadioPrev() {
        val curIdx = radioStations.indexOfFirst { it.id == _currentRadioStation.value.id }
        val prevIdx = (curIdx - 1).coerceAtLeast(0)
        _currentRadioStation.value = radioStations[prevIdx]
    }

    fun toggleRadioBand() {
        val newBand = if (_currentRadioStation.value.band == "FM") "AM" else "FM"
        val newFreq = if (newBand == "AM") "820 AM" else "98.7 FM"
        _currentRadioStation.update { it.copy(band = newBand, frequency = newFreq) }
    }

    // Navigation controls
    fun selectDestination(name: String) {
        _navigationState.update {
            it.copy(
                destinationName = name,
                nextInstruction = "In 250m, Keep Straight towards $name",
                distanceRemainingKm = 8.5f,
                etaRemainingMinutes = 11
            )
        }
        _currentScreen.value = CarScreen.NAVIGATION
        Toast.makeText(context, "Routing to $name", Toast.LENGTH_SHORT).show()
    }

    // YouTube Launcher
    fun launchYouTube(item: YouTubeCardItem) {
        val pm = context.packageManager
        val intent = pm.getLaunchIntentForPackage("com.google.android.youtube")
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Playing audio for: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    // Launch any installed app
    fun launchApp(packageName: String) {
        try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Cannot launch $packageName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Card customization
    fun toggleCardVisibility(index: Int) {
        val updated = _cardConfigs.value.toMutableList()
        val cur = updated[index]
        updated[index] = cur.copy(isVisible = !cur.isVisible)
        _cardConfigs.value = updated
    }

    fun moveCardUp(index: Int) {
        if (index > 0) {
            val updated = _cardConfigs.value.toMutableList()
            val temp = updated[index]
            updated[index] = updated[index - 1]
            updated[index - 1] = temp
            _cardConfigs.value = updated
        }
    }

    fun moveCardDown(index: Int) {
        if (index < _cardConfigs.value.size - 1) {
            val updated = _cardConfigs.value.toMutableList()
            val temp = updated[index]
            updated[index] = updated[index + 1]
            updated[index + 1] = temp
            _cardConfigs.value = updated
        }
    }

    fun resetCardDefaults() {
        _cardConfigs.value = defaultConfigs
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopListening()
        voiceAssistant.destroy()
    }
}

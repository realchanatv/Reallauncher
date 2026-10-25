package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.DashboardCardConfig
import com.example.data.model.DashboardCardType
import com.example.data.model.MusicState
import com.example.data.model.NavigationState
import com.example.data.model.RadioStation
import com.example.data.model.VehicleTelemetry
import com.example.data.model.WeatherData
import com.example.data.model.YouTubeCardItem
import com.example.ui.cards.MusicCard
import com.example.ui.cards.NavigationCard
import com.example.ui.cards.ObdTelemetryCard
import com.example.ui.cards.QuickTogglesCard
import com.example.ui.cards.RadioCard
import com.example.ui.cards.SpeedometerCard
import com.example.ui.cards.WeatherCard
import com.example.ui.cards.YouTubeCard

@Composable
fun DashboardScreen(
    cardConfigs: List<DashboardCardConfig>,
    telemetry: VehicleTelemetry,
    navigationState: NavigationState,
    musicState: MusicState,
    radioStation: RadioStation,
    radioPresets: List<RadioStation>,
    weather: WeatherData,
    youTubeItems: List<YouTubeCardItem>,
    isMph: Boolean,
    isNightMode: Boolean,
    showSpeedOverlay: Boolean,
    onToggleUnit: () -> Unit,
    onToggleNightMode: () -> Unit,
    onToggleSpeedOverlay: () -> Unit,
    onTriggerSimulatedFault: () -> Unit,
    onOpenFullMap: () -> Unit,
    onOpenDiagnostics: () -> Unit,
    onTogglePlayMusic: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onSelectRadioPreset: (RadioStation) -> Unit,
    onSeekRadioPrev: () -> Unit,
    onSeekRadioNext: () -> Unit,
    onToggleRadioBand: () -> Unit,
    onLaunchYouTube: (YouTubeCardItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleCards = cardConfigs.filter { it.isVisible }.sortedBy { it.order }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen_grid"),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(visibleCards, key = { it.type.name }) { config ->
            Box(modifier = Modifier.height(180.dp)) {
                when (config.type) {
                    DashboardCardType.SPEEDOMETER -> {
                        SpeedometerCard(
                            telemetry = telemetry,
                            isMph = isMph,
                            onToggleUnit = onToggleUnit
                        )
                    }
                    DashboardCardType.NAVIGATION -> {
                        NavigationCard(
                            navigationState = navigationState,
                            onOpenFullMap = onOpenFullMap
                        )
                    }
                    DashboardCardType.MUSIC -> {
                        MusicCard(
                            musicState = musicState,
                            onTogglePlay = onTogglePlayMusic,
                            onNextTrack = onNextTrack,
                            onPrevTrack = onPrevTrack
                        )
                    }
                    DashboardCardType.RADIO -> {
                        RadioCard(
                            currentStation = radioStation,
                            presets = radioPresets,
                            onSelectPreset = onSelectRadioPreset,
                            onSeekPrev = onSeekRadioPrev,
                            onSeekNext = onSeekRadioNext,
                            onToggleBand = onToggleRadioBand
                        )
                    }
                    DashboardCardType.WEATHER -> {
                        WeatherCard(
                            weather = weather,
                            isFahrenheit = isMph
                        )
                    }
                    DashboardCardType.OBD_DIAGNOSTICS -> {
                        ObdTelemetryCard(
                            telemetry = telemetry,
                            onOpenDiagnostics = onOpenDiagnostics
                        )
                    }
                    DashboardCardType.YOUTUBE -> {
                        YouTubeCard(
                            items = youTubeItems,
                            isMoving = telemetry.speedKmh > 2f,
                            onLaunchYouTube = onLaunchYouTube
                        )
                    }
                    DashboardCardType.QUICK_TOGGLES -> {
                        QuickTogglesCard(
                            isNightMode = isNightMode,
                            onToggleNightMode = onToggleNightMode,
                            showSpeedOverlay = showSpeedOverlay,
                            onToggleSpeedOverlay = onToggleSpeedOverlay,
                            onTriggerSimulatedFault = onTriggerSimulatedFault
                        )
                    }
                }
            }
        }
    }
}

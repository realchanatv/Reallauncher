package com.example.data.model

enum class DashboardCardType(
    val title: String,
    val iconName: String,
    val defaultOrder: Int
) {
    SPEEDOMETER("Speed Gauge", "speed", 0),
    NAVIGATION("Navigation & Traffic", "navigation", 1),
    MUSIC("Media Player", "music_note", 2),
    RADIO("FM / AM Radio", "radio", 3),
    WEATHER("Live Weather", "wb_sunny", 4),
    OBD_DIAGNOSTICS("OBD-II Telemetry", "directions_car", 5),
    YOUTUBE("YouTube Hub", "smart_display", 6),
    QUICK_TOGGLES("Vehicle Controls", "tune", 7)
}

data class DashboardCardConfig(
    val type: DashboardCardType,
    val isVisible: Boolean = true,
    val order: Int = type.defaultOrder
)

package com.example.data.model

data class DtcCode(
    val code: String,
    val title: String,
    val severity: DtcSeverity,
    val description: String,
    val suggestedFix: String
)

enum class DtcSeverity {
    INFO,
    WARNING,
    CRITICAL
}

data class VehicleTelemetry(
    val speedKmh: Float = 0f,
    val speedLimitKmh: Int = 100,
    val rpm: Int = 850,
    val coolantTempC: Int = 88,
    val oilTempC: Int = 92,
    val batteryVoltage: Float = 14.2f,
    val fuelPercent: Int = 74,
    val throttlePercent: Int = 0,
    val engineLoadPercent: Int = 18,
    val boostPsi: Float = 0.0f,
    val gear: String = "P",
    val isEngineRunning: Boolean = true,
    val isSpeedWarning: Boolean = false,
    val obdConnected: Boolean = true,
    val obdDeviceName: String = "ELM327 Bluetooth OBD-II",
    val dtcCodes: List<DtcCode> = emptyList(),
    val tripDistanceKm: Float = 42.6f,
    val tripDurationMinutes: Int = 38,
    val avgSpeedKmh: Float = 67.2f
) {
    val speedMph: Float
        get() = speedKmh * 0.621371f

    val speedLimitMph: Int
        get() = (speedLimitKmh * 0.621371f).toInt()
}

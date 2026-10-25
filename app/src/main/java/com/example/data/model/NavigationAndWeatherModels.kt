package com.example.data.model

data class TrafficAlert(
    val id: String,
    val title: String,
    val description: String,
    val distanceText: String,
    val severity: AlertSeverity,
    val delayMinutes: Int = 0
)

enum class AlertSeverity {
    INFO,
    WARNING,
    HAZARD
}

enum class NavigationManeuver {
    TURN_RIGHT,
    TURN_LEFT,
    KEEP_STRAIGHT,
    SLIGHT_RIGHT,
    SLIGHT_LEFT,
    U_TURN,
    ROUNDABOUT,
    DESTINATION
}

data class NavigationState(
    val isNavigating: Boolean = true,
    val destinationName: String = "Grand Central Hub",
    val destinationAddress: String = "450 Lexington Ave, Suite 100",
    val nextManeuver: NavigationManeuver = NavigationManeuver.TURN_RIGHT,
    val nextInstruction: String = "In 400m, Turn Right onto Grand Avenue",
    val currentRoad: String = "State Route 101 North",
    val distanceToManeuverMeters: Int = 400,
    val distanceRemainingKm: Float = 14.8f,
    val etaRemainingMinutes: Int = 18,
    val arrivalTime: String = "14:48",
    val currentSpeedLimit: Int = 65,
    val trafficAlerts: List<TrafficAlert> = listOf(
        TrafficAlert(
            id = "t1",
            title = "Heavy Traffic Congestion",
            description = "Slow moving vehicles ahead on I-101",
            distanceText = "1.8 km ahead",
            severity = AlertSeverity.WARNING,
            delayMinutes = 6
        ),
        TrafficAlert(
            id = "t2",
            title = "Highway Construction Ahead",
            description = "Right lane closed for roadwork",
            distanceText = "4.2 km ahead",
            severity = AlertSeverity.INFO,
            delayMinutes = 2
        ),
        TrafficAlert(
            id = "t3",
            title = "Fixed Speed Camera",
            description = "Speed limit strictly enforced 65 MPH",
            distanceText = "850 m ahead",
            severity = AlertSeverity.HAZARD,
            delayMinutes = 0
        )
    )
)

data class WeatherData(
    val tempCelsius: Int = 22,
    val condition: String = "Clear & Dry",
    val location: String = "Downtown Metro",
    val highC: Int = 26,
    val lowC: Int = 16,
    val precipitationChance: Int = 10,
    val windSpeedKmh: Int = 14,
    val roadCondition: String = "Optimal Dry Asphalt",
    val isRoadHazard: Boolean = false
) {
    val tempFahrenheit: Int
        get() = ((tempCelsius * 9f / 5f) + 32).toInt()
}

data class InstalledAppItem(
    val label: String,
    val packageName: String,
    val isAutomotiveRecommended: Boolean = false,
    val category: String = "General"
)

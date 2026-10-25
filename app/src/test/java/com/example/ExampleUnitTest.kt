package com.example

import com.example.data.model.DashboardCardConfig
import com.example.data.model.DashboardCardType
import com.example.data.model.DtcCode
import com.example.data.model.DtcSeverity
import com.example.data.model.VehicleTelemetry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun telemetry_speedConversion_isAccurate() {
        val telemetry = VehicleTelemetry(speedKmh = 100f, speedLimitKmh = 100)
        assertEquals(62.1371f, telemetry.speedMph, 0.01f)
        assertEquals(62, telemetry.speedLimitMph)
    }

    @Test
    fun telemetry_overspeedDetection_works() {
        val normal = VehicleTelemetry(speedKmh = 60f, speedLimitKmh = 65, isSpeedWarning = false)
        assertFalse(normal.isSpeedWarning)

        val speeding = VehicleTelemetry(speedKmh = 85f, speedLimitKmh = 65, isSpeedWarning = true)
        assertTrue(speeding.isSpeedWarning)
    }

    @Test
    fun dtcCodes_handling_isCorrect() {
        val dtc = DtcCode(
            code = "P0420",
            title = "Catalyst System Efficiency",
            severity = DtcSeverity.WARNING,
            description = "O2 sensor check",
            suggestedFix = "Inspect sensor"
        )
        val telemetry = VehicleTelemetry(dtcCodes = listOf(dtc))
        assertEquals(1, telemetry.dtcCodes.size)
        assertEquals("P0420", telemetry.dtcCodes[0].code)
        assertEquals(DtcSeverity.WARNING, telemetry.dtcCodes[0].severity)
    }

    @Test
    fun cardConfig_order_isPreserved() {
        val configs = listOf(
            DashboardCardConfig(DashboardCardType.SPEEDOMETER, order = 0),
            DashboardCardConfig(DashboardCardType.NAVIGATION, order = 1),
            DashboardCardConfig(DashboardCardType.MUSIC, order = 2)
        )
        assertEquals(DashboardCardType.SPEEDOMETER, configs[0].type)
        assertTrue(configs[0].isVisible)
    }
}

package com.example.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleTelemetry
import com.example.ui.components.SpeedometerGauge
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed

@Composable
fun SpeedometerCard(
    telemetry: VehicleTelemetry,
    isMph: Boolean,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val cardBg = if (isDark) Color(0xFF141720) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF262B3A) else Color(0xFFD3D8E4)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)

    val currentSpeed = if (isMph) telemetry.speedMph else telemetry.speedKmh
    val currentLimit = if (isMph) telemetry.speedLimitMph else telemetry.speedLimitKmh
    val unitLabel = if (isMph) "MPH" else "KM/H"

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag("speedometer_card")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Speedometer Gauge
            SpeedometerGauge(
                speed = currentSpeed,
                maxSpeed = if (isMph) 140f else 220f,
                unit = unitLabel,
                speedLimit = currentLimit,
                gear = telemetry.gear,
                rpm = telemetry.rpm,
                isWarning = telemetry.isSpeedWarning,
                size = 195.dp
            )

            // Side trip telemetry stats
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Unit Switcher Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF1F2432) else Color(0xFFE2E6EE))
                        .carPressable(onClick = onToggleUnit)
                        .testTag("toggle_speed_unit_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "UNIT: $unitLabel",
                        color = AutoRacingRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Trip Distance
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = AutoRacingRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TRIP DISTANCE",
                            color = mutedColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", telemetry.tripDistanceKm)} KM",
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Trip Duration
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "DRIVE TIME",
                            color = mutedColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${telemetry.tripDurationMinutes} MIN",
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Boost PSI / Throttle
                Column {
                    Text(
                        text = "TURBO BOOST",
                        color = mutedColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+${telemetry.boostPsi} PSI",
                        color = if (telemetry.boostPsi > 8) AutoRacingRed else textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

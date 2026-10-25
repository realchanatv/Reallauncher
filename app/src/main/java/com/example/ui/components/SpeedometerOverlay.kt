package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleTelemetry
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed
import com.example.ui.theme.AutoSpeedWarning

@Composable
fun SpeedometerOverlay(
    telemetry: VehicleTelemetry,
    isMph: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val bg = if (isDark) Color(0xFF10131B).copy(alpha = 0.95f) else Color(0xFFFFFFFF).copy(alpha = 0.95f)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val speedValue = if (isMph) telemetry.speedMph else telemetry.speedKmh
    val speedLimit = if (isMph) telemetry.speedLimitMph else telemetry.speedLimitKmh
    val unit = if (isMph) "MPH" else "KM/H"

    Box(
        modifier = modifier
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(
                1.5.dp,
                if (telemetry.isSpeedWarning) AutoRacingRed else Color(0xFF2C3244),
                RoundedCornerShape(16.dp)
            )
            .padding(10.dp)
            .testTag("speedometer_hud_overlay")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                if (telemetry.isSpeedWarning) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Overspeed",
                        tint = AutoRacingRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SPEED ALERT",
                        color = AutoRacingRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Text(
                        text = "LIVE HUD",
                        color = Color(0xFF8E95A5),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF222736) else Color(0xFFE2E6EE))
                        .carPressable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close HUD",
                        tint = textColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Big Speed and Unit
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${speedValue.toInt()}",
                    color = if (telemetry.isSpeedWarning) AutoRacingRed else textColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = Color(0xFF8E95A5),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Speed Limit & Gear Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, AutoRacingRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$speedLimit",
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GEAR ${telemetry.gear}",
                    color = AutoRacingRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

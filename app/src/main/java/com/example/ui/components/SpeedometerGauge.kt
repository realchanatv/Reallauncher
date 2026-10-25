package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed
import com.example.ui.theme.AutoSpeedWarning
import com.example.ui.theme.AutoTachRedline
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGauge(
    speed: Float,
    maxSpeed: Float = 160f,
    unit: String = "MPH",
    speedLimit: Int = 65,
    gear: String = "D",
    rpm: Int = 2400,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed.coerceIn(0f, maxSpeed),
        animationSpec = tween(durationMillis = 180),
        label = "speedometer_needle"
    )

    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val dialTrackColor = if (isDark) Color(0xFF1E222D) else Color(0xFFD6DBE5)
    val tickColor = if (isDark) AutoPureWhite else Color(0xFF1B1E26)
    val numberColor = if (isDark) AutoPureWhite else Color(0xFF0C0E14)

    Box(
        modifier = modifier
            .size(size)
            .testTag("speedometer_gauge"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val radius = (canvasWidth.coerceAtMost(canvasHeight) / 2f) - 16.dp.toPx()

            val startAngle = 140f
            val sweepAngle = 260f

            // Background gauge track
            drawArc(
                color = dialTrackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Redline indicator arc (last 20% of gauge)
            val redlineStart = startAngle + (sweepAngle * 0.8f)
            val redlineSweep = sweepAngle * 0.2f
            drawArc(
                color = AutoTachRedline,
                startAngle = redlineStart,
                sweepAngle = redlineSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )

            // Active speed gradient sweep
            val speedFraction = (animatedSpeed / maxSpeed).coerceIn(0f, 1f)
            if (speedFraction > 0.01f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.35f to AutoRacingRed,
                        0.75f to AutoTachRedline,
                        1.0f to Color.White
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * speedFraction,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Ticks around dial
            val totalTicks = 20
            for (i in 0..totalTicks) {
                val tickAngle = startAngle + (sweepAngle * (i.toFloat() / totalTicks))
                val rad = Math.toRadians(tickAngle.toDouble())
                val isMajor = i % 4 == 0
                val tickLength = if (isMajor) 14.dp.toPx() else 7.dp.toPx()
                val tickStroke = if (isMajor) 2.5.dp.toPx() else 1.2.dp.toPx()
                val isRedlineTick = i >= (totalTicks * 0.8f)

                val innerR = radius - 16.dp.toPx()
                val outerR = innerR + tickLength

                val startP = Offset(
                    (center.x + (innerR * cos(rad))).toFloat(),
                    (center.y + (innerR * sin(rad))).toFloat()
                )
                val endP = Offset(
                    (center.x + (outerR * cos(rad))).toFloat(),
                    (center.y + (outerR * sin(rad))).toFloat()
                )

                drawLine(
                    color = if (isRedlineTick) AutoTachRedline else tickColor.copy(alpha = if (isMajor) 0.9f else 0.4f),
                    start = startP,
                    end = endP,
                    strokeWidth = tickStroke,
                    cap = StrokeCap.Round
                )
            }

            // Needle
            val needleAngle = startAngle + (sweepAngle * speedFraction)
            rotate(degrees = needleAngle - 90f, pivot = center) {
                // Needle line
                drawLine(
                    brush = Brush.verticalGradient(
                        colors = listOf(AutoPureWhite, AutoRacingRed)
                    ),
                    start = Offset(center.x, center.y + 12.dp.toPx()),
                    end = Offset(center.x, center.y - radius + 8.dp.toPx()),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Center pivot cap
            drawCircle(
                color = AutoPureBlack,
                radius = 16.dp.toPx(),
                center = center
            )
            drawCircle(
                color = AutoRacingRed,
                radius = 7.dp.toPx(),
                center = center
            )
        }

        // Digital telemetry overlay inside gauge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 18.dp)
        ) {
            // Speed Warning alert
            if (isWarning) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(AutoRacingRed, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Overspeed",
                        tint = AutoPureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "SLOW DOWN",
                        color = AutoPureWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Big Digital Speed
            Text(
                text = "${animatedSpeed.toInt()}",
                color = if (isWarning) AutoRacingRed else numberColor,
                fontSize = if (size < 180.dp) 36.sp else 48.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 44.sp,
                letterSpacing = (-1).sp
            )

            // Speed Unit Label
            Text(
                text = unit,
                color = if (isDark) AutoPureWhite.copy(alpha = 0.7f) else Color(0xFF4A4E5C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Badges row: Gear + Speed limit
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gear Indicator
                Box(
                    modifier = Modifier
                        .background(
                            if (isDark) Color(0xFF232733) else Color(0xFFE2E6EE),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "GEAR $gear",
                        color = if (gear == "D" || gear == "S") AutoRacingRed else numberColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Speed limit sign
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                        .border(2.5.dp, AutoRacingRed, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$speedLimit",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$rpm RPM",
                color = if (isDark) Color(0xFF888E9E) else Color(0xFF5A5E6D),
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

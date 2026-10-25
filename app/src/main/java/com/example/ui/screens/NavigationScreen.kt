package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertSeverity
import com.example.data.model.NavigationState
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed
import com.example.ui.theme.AutoSpeedWarning
import com.example.ui.theme.AutoSuccessGreen

@Composable
fun NavigationScreen(
    navigationState: NavigationState,
    onSelectDestination: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    val mapBgColor = if (isDark) Color(0xFF0F1117) else Color(0xFFDEE3ED)
    val roadColor = if (isDark) Color(0xFF1F2330) else Color(0xFFFFFFFF)
    val highwayColor = if (isDark) Color(0xFF2C3246) else Color(0xFFCAD0E0)
    val waterColor = if (isDark) Color(0xFF111D2C) else Color(0xFFB9D4EC)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("offline_maps_screen")
    ) {
        // High-performance Offline Vector Map Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width * 0.42f, height * 0.52f)

            // Background land
            drawRect(color = mapBgColor)

            // Water feature (Lake/Bay)
            val waterPath = Path().apply {
                moveTo(width * 0.75f, 0f)
                cubicTo(width * 0.7f, height * 0.3f, width * 0.85f, height * 0.6f, width, height * 0.7f)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = waterPath, color = waterColor)

            // Local Street Grid
            val gridStep = 45.dp.toPx() * zoomLevel
            for (x in -2..18) {
                val posX = (x * gridStep) % width
                drawLine(
                    color = roadColor,
                    start = Offset(posX, 0f),
                    end = Offset(posX, height),
                    strokeWidth = 3.dp.toPx()
                )
            }
            for (y in -2..12) {
                val posY = (y * gridStep) % height
                drawLine(
                    color = roadColor,
                    start = Offset(0f, posY),
                    end = Offset(width, posY),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Arterial Highways
            drawLine(
                color = highwayColor,
                start = Offset(0f, height * 0.45f),
                end = Offset(width, height * 0.48f),
                strokeWidth = 8.dp.toPx()
            )
            drawLine(
                color = highwayColor,
                start = Offset(width * 0.35f, 0f),
                end = Offset(width * 0.52f, height),
                strokeWidth = 8.dp.toPx()
            )

            // Active Navigation Route (Bold Red Line with glow)
            val routePath = Path().apply {
                moveTo(center.x, center.y)
                lineTo(center.x + 80f * zoomLevel, center.y - 70f * zoomLevel)
                lineTo(center.x + 190f * zoomLevel, center.y - 75f * zoomLevel)
                lineTo(center.x + 240f * zoomLevel, center.y - 180f * zoomLevel)
                lineTo(center.x + 360f * zoomLevel, center.y - 190f * zoomLevel)
            }

            // Route outer glow
            drawPath(
                path = routePath,
                color = AutoRacingRed.copy(alpha = 0.35f),
                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Route core line
            drawPath(
                path = routePath,
                color = AutoRacingRed,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Destination Pin
            val destPoint = Offset(center.x + 360f * zoomLevel, center.y - 190f * zoomLevel)
            drawCircle(
                color = AutoPureBlack,
                radius = 12.dp.toPx(),
                center = destPoint
            )
            drawCircle(
                color = AutoRacingRed,
                radius = 8.dp.toPx(),
                center = destPoint
            )
            drawCircle(
                color = AutoPureWhite,
                radius = 3.dp.toPx(),
                center = destPoint
            )

            // Vehicle Location Arrow (Current Position)
            drawCircle(
                color = AutoRacingRed.copy(alpha = 0.25f),
                radius = 24.dp.toPx(),
                center = center
            )
            drawCircle(
                color = AutoPureWhite,
                radius = 14.dp.toPx(),
                center = center
            )
            rotate(degrees = 35f, pivot = center) {
                val arrowPath = Path().apply {
                    moveTo(center.x, center.y - 12.dp.toPx())
                    lineTo(center.x - 7.dp.toPx(), center.y + 8.dp.toPx())
                    lineTo(center.x, center.y + 4.dp.toPx())
                    lineTo(center.x + 7.dp.toPx(), center.y + 8.dp.toPx())
                    close()
                }
                drawPath(path = arrowPath, color = AutoRacingRed)
            }
        }

        // Top Turn-by-Turn Instruction Banner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF141722).copy(alpha = 0.95f) else Color(0xFFFFFFFF).copy(alpha = 0.95f))
                .border(1.dp, if (isDark) Color(0xFF2C3245) else Color(0xFFCED4E2), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AutoRacingRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Turn Right",
                        tint = AutoPureWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "IN ${navigationState.distanceToManeuverMeters}M",
                        color = AutoRacingRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = navigationState.nextInstruction,
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Heading towards ${navigationState.destinationName}",
                        color = mutedColor,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Right Floating Traffic Alerts Panel (user requested "real-time traffic alerts")
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .width(280.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF141722).copy(alpha = 0.95f) else Color(0xFFFFFFFF).copy(alpha = 0.95f))
                    .border(1.dp, if (isDark) Color(0xFF2C3245) else Color(0xFFCED4E2), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE TRAFFIC ALERTS",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AutoRacingRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${navigationState.trafficAlerts.size} ACTIVE",
                                color = AutoPureWhite,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    navigationState.trafficAlerts.forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (alert.severity) {
                                    AlertSeverity.HAZARD -> Icons.Default.ReportProblem
                                    AlertSeverity.WARNING -> Icons.Default.Traffic
                                    AlertSeverity.INFO -> Icons.Default.Construction
                                },
                                contentDescription = null,
                                tint = when (alert.severity) {
                                    AlertSeverity.HAZARD -> AutoRacingRed
                                    AlertSeverity.WARNING -> AutoSpeedWarning
                                    AlertSeverity.INFO -> Color(0xFF29B6F6)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alert.title,
                                    color = textColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${alert.distanceText} • ${alert.description}",
                                    color = mutedColor,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Quick Offline Destination Shortcuts
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF141722).copy(alpha = 0.95f) else Color(0xFFFFFFFF).copy(alpha = 0.95f))
                    .border(1.dp, if (isDark) Color(0xFF2C3245) else Color(0xFFCED4E2), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "SAVED OFFLINE ROUTES",
                        color = mutedColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DestShortcut(icon = Icons.Default.Home, label = "Home", onClick = { onSelectDestination("Home") })
                        DestShortcut(icon = Icons.Default.Work, label = "Work", onClick = { onSelectDestination("Work") })
                        DestShortcut(icon = Icons.Default.EvStation, label = "Charge", onClick = { onSelectDestination("Supercharger") })
                        DestShortcut(icon = Icons.Default.LocalGasStation, label = "Gas", onClick = { onSelectDestination("Fuel Station") })
                    }
                }
            }
        }

        // Bottom Left: Map Controls (Zoom +, Zoom -, Re-center)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF141722) else Color(0xFFFFFFFF))
                    .border(1.dp, if (isDark) Color(0xFF2C3245) else Color(0xFFCED4E2), RoundedCornerShape(10.dp))
                    .carPressable(onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(2.0f) }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF141722) else Color(0xFFFFFFFF))
                    .border(1.dp, if (isDark) Color(0xFF2C3245) else Color(0xFFCED4E2), RoundedCornerShape(10.dp))
                    .carPressable(onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.6f) }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AutoRacingRed)
                    .carPressable(onClick = { zoomLevel = 1.0f }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Re-center",
                    tint = AutoPureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DestShortcut(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    Box(
        modifier = Modifier
            .size(54.dp, 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isDark) Color(0xFF1E2230) else Color(0xFFE2E7F2))
            .carPressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = AutoRacingRed,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = if (isDark) AutoPureWhite else Color(0xFF121419),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

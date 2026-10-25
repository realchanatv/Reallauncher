package com.example.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Traffic
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
fun NavigationCard(
    navigationState: NavigationState,
    onOpenFullMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val cardBg = if (isDark) Color(0xFF141720) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF262B3A) else Color(0xFFD3D8E4)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)

    val activeAlert = navigationState.trafficAlerts.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("navigation_card")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Maneuver Direction and Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Maneuver Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AutoRacingRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Turn",
                        tint = AutoPureWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "IN ${navigationState.distanceToManeuverMeters} METERS",
                        color = AutoRacingRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = navigationState.nextInstruction,
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = navigationState.currentRoad,
                        color = mutedColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Full Map Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF222736) else Color(0xFFE2E6EE))
                        .carPressable(onClick = onOpenFullMap)
                        .testTag("nav_open_full_map_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Open Full Map",
                        tint = if (isDark) AutoPureWhite else Color(0xFF161922),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Real-Time Traffic Alert Banner (as requested by user)
            if (activeAlert != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (activeAlert.severity) {
                                AlertSeverity.HAZARD -> AutoRacingRed.copy(alpha = 0.2f)
                                AlertSeverity.WARNING -> AutoSpeedWarning.copy(alpha = 0.2f)
                                AlertSeverity.INFO -> Color(0xFF2196F3).copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when (activeAlert.severity) {
                                AlertSeverity.HAZARD -> AutoRacingRed
                                AlertSeverity.WARNING -> AutoSpeedWarning
                                AlertSeverity.INFO -> Color(0xFF2196F3)
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (activeAlert.severity) {
                            AlertSeverity.HAZARD -> Icons.Default.ReportProblem
                            AlertSeverity.WARNING -> Icons.Default.Traffic
                            AlertSeverity.INFO -> Icons.Default.Construction
                        },
                        contentDescription = "Traffic Alert",
                        tint = when (activeAlert.severity) {
                            AlertSeverity.HAZARD -> AutoRacingRed
                            AlertSeverity.WARNING -> AutoSpeedWarning
                            AlertSeverity.INFO -> Color(0xFF2196F3)
                        },
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${activeAlert.title} (${activeAlert.distanceText})",
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = activeAlert.description,
                            color = mutedColor,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (activeAlert.delayMinutes > 0) {
                        Text(
                            text = "+${activeAlert.delayMinutes} MIN",
                            color = AutoRacingRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // ETA and Trip Summary Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1B1E29) else Color(0xFFEFF2F8))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AutoSuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ETA: ${navigationState.etaRemainingMinutes} MIN",
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "${navigationState.distanceRemainingKm} KM REMAINING",
                    color = mutedColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "ARRIVE ${navigationState.arrivalTime}",
                    color = AutoRacingRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed

@Composable
fun QuickTogglesCard(
    isNightMode: Boolean,
    onToggleNightMode: () -> Unit,
    showSpeedOverlay: Boolean,
    onToggleSpeedOverlay: () -> Unit,
    onTriggerSimulatedFault: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val cardBg = if (isDark) Color(0xFF141720) else Color(0xFFFFFFFF)
    val borderColor = if (isDark) Color(0xFF262B3A) else Color(0xFFD3D8E4)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("quick_toggles_card")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF232838) else Color(0xFFE2E6F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Controls",
                        tint = AutoRacingRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VEHICLE CONTROLS",
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CarToggleItem(
                    title = "THEME",
                    subtitle = if (isNightMode) "NIGHT" else "DAY",
                    icon = Icons.Default.Brightness4,
                    isActive = isNightMode,
                    onClick = onToggleNightMode,
                    modifier = Modifier.weight(1f),
                    tag = "toggle_theme_quick"
                )

                CarToggleItem(
                    title = "SPEED HUD",
                    subtitle = if (showSpeedOverlay) "ACTIVE" else "OFF",
                    icon = Icons.Default.Speed,
                    isActive = showSpeedOverlay,
                    onClick = onToggleSpeedOverlay,
                    modifier = Modifier.weight(1f),
                    tag = "toggle_hud_quick"
                )

                CarToggleItem(
                    title = "TEST FAULT",
                    subtitle = "INJECT DTC",
                    icon = Icons.Default.BugReport,
                    isActive = false,
                    onClick = onTriggerSimulatedFault,
                    modifier = Modifier.weight(1f),
                    tag = "toggle_fault_quick"
                )
            }
        }
    }
}

@Composable
private fun CarToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)

    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isActive) AutoRacingRed else (if (isDark) Color(0xFF1C202C) else Color(0xFFE5E9F3))
            )
            .carPressable(onClick = onClick)
            .testTag(tag)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) AutoPureWhite else (if (isDark) AutoPureWhite else Color(0xFF1B1E28)),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = if (isActive) AutoPureWhite else textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

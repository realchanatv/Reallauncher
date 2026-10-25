package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Speed
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
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed

enum class CarScreen(val title: String) {
    DASHBOARD("Dashboard"),
    NAVIGATION("Navigation"),
    MEDIA("Media Hub"),
    OBD_TELEMETRY("OBD Diagnostics"),
    APP_DRAWER("All Apps")
}

@Composable
fun CarSideNavRail(
    currentScreen: CarScreen,
    onSelectScreen: (CarScreen) -> Unit,
    showSpeedOverlay: Boolean,
    onToggleSpeedOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val navBgColor = if (isDark) Color(0xFF0D0F14) else Color(0xFFE2E6EF)
    val borderColor = if (isDark) Color(0xFF232734) else Color(0xFFCBD1DE)

    Column(
        modifier = modifier
            .width(84.dp)
            .fillMaxHeight()
            .background(navBgColor)
            .border(width = 0.5.dp, color = borderColor)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Main Navigation Items
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavRailItem(
                icon = Icons.Default.Dashboard,
                label = "Dash",
                isSelected = currentScreen == CarScreen.DASHBOARD,
                onClick = { onSelectScreen(CarScreen.DASHBOARD) },
                tag = "nav_dashboard"
            )

            NavRailItem(
                icon = Icons.Default.Navigation,
                label = "Maps",
                isSelected = currentScreen == CarScreen.NAVIGATION,
                onClick = { onSelectScreen(CarScreen.NAVIGATION) },
                tag = "nav_navigation"
            )

            NavRailItem(
                icon = Icons.Default.LibraryMusic,
                label = "Media",
                isSelected = currentScreen == CarScreen.MEDIA,
                onClick = { onSelectScreen(CarScreen.MEDIA) },
                tag = "nav_media"
            )

            NavRailItem(
                icon = Icons.Default.DirectionsCar,
                label = "OBD-II",
                isSelected = currentScreen == CarScreen.OBD_TELEMETRY,
                onClick = { onSelectScreen(CarScreen.OBD_TELEMETRY) },
                tag = "nav_obd"
            )

            NavRailItem(
                icon = Icons.Default.Apps,
                label = "Apps",
                isSelected = currentScreen == CarScreen.APP_DRAWER,
                onClick = { onSelectScreen(CarScreen.APP_DRAWER) },
                tag = "nav_app_drawer"
            )
        }

        // Bottom speed gauge overlay toggle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (showSpeedOverlay) AutoRacingRed else (if (isDark) Color(0xFF1E222D) else Color(0xFFCCD2DF))
                    )
                    .carPressable(onClick = onToggleSpeedOverlay)
                    .testTag("toggle_speed_overlay_btn"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Toggle Speedometer Overlay",
                        tint = if (showSpeedOverlay) AutoPureWhite else (if (isDark) AutoPureWhite else Color(0xFF141720)),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (showSpeedOverlay) "HUD ON" else "HUD",
                        color = if (showSpeedOverlay) AutoPureWhite else (if (isDark) Color(0xFFA5ACB8) else Color(0xFF5A6070)),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun NavRailItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack

    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> AutoRacingRed
                    isDark -> Color(0xFF171922)
                    else -> Color(0xFFD6DBE7)
                }
            )
            .carPressable(onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) AutoPureWhite else (if (isDark) AutoPureWhite.copy(alpha = 0.85f) else Color(0xFF1B1E28)),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) AutoPureWhite else (if (isDark) Color(0xFF9BA2B2) else Color(0xFF4A4F5F)),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            )
        }
    }
}

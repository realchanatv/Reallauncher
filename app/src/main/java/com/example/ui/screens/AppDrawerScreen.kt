package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstalledAppItem
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed

@Composable
fun AppDrawerScreen(
    installedApps: List<InstalledAppItem>,
    onLaunchApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val surfaceColor = if (isDark) Color(0xFF13161F) else Color(0xFFFFFFFF)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)
    val borderColor = if (isDark) Color(0xFF262C3A) else Color(0xFFCED4E2)

    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = if (searchQuery.isBlank()) {
        installedApps
    } else {
        installedApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("app_drawer_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search & Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceColor)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AutoRacingRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = AutoPureWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VEHICLE APP DRAWER",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${installedApps.size} Automotive & System Apps Available",
                        color = mutedColor,
                        fontSize = 10.sp
                    )
                }
            }

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .width(260.dp)
                    .height(44.dp)
                    .testTag("app_search_field"),
                placeholder = {
                    Text(
                        text = "Search installed apps...",
                        fontSize = 11.sp,
                        color = mutedColor
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AutoRacingRed,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Apps Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { appItem ->
                    AppDrawerGridItem(
                        item = appItem,
                        onClick = { onLaunchApp(appItem.packageName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppDrawerGridItem(
    item: InstalledAppItem,
    onClick: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val itemBg = if (isDark) Color(0xFF1A1E29) else Color(0xFFF1F4FA)

    // Select category icon
    val iconVector: ImageVector = when {
        item.label.contains("Map", true) || item.label.contains("Waze", true) -> Icons.Default.Map
        item.label.contains("Music", true) || item.label.contains("Spotify", true) -> Icons.Default.MusicNote
        item.label.contains("YouTube", true) || item.label.contains("Video", true) -> Icons.Default.SmartDisplay
        item.label.contains("Phone", true) || item.label.contains("Call", true) -> Icons.Default.Phone
        item.label.contains("Setting", true) -> Icons.Default.Settings
        item.label.contains("Auto", true) || item.label.contains("Car", true) -> Icons.Default.DirectionsCar
        else -> Icons.Default.Apps
    }

    Box(
        modifier = Modifier
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(itemBg)
            .carPressable(onClick = onClick)
            .padding(10.dp)
            .testTag("app_item_${item.packageName}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isAutomotiveRecommended) AutoRacingRed
                        else (if (isDark) Color(0xFF282D3D) else Color(0xFFDEE3EE))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = item.label,
                    tint = if (item.isAutomotiveRecommended) AutoPureWhite else (if (isDark) AutoPureWhite else Color(0xFF1B1E28)),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

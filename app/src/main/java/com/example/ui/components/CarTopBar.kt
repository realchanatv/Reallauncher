package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed
import com.example.ui.theme.AutoSuccessGreen
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CarTopBar(
    isNightMode: Boolean,
    onToggleNightMode: () -> Unit,
    onOpenVoiceAssistant: () -> Unit,
    onOpenCustomizeCards: () -> Unit,
    obdConnected: Boolean,
    tempC: Int,
    condition: String,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val surfaceColor = if (isDark) Color(0xFF101217) else Color(0xFFE5E9F2)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF0F1116)
    val mutedColor = if (isDark) Color(0xFF8C93A6) else Color(0xFF5A6072)
    val borderColor = if (isDark) Color(0xFF242836) else Color(0xFFCCD2DF)

    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now)
            delay(1000)
        }
    }

    // Voice assistant button pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(surfaceColor)
            .border(width = 0.5.dp, color = borderColor)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left: Branding + Connectivity indicators
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Racing stripe badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(if (isDark) Color(0xFF1B1F2A) else Color(0xFFD6DBE6), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp, 16.dp)
                        .background(AutoRacingRed, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "APEX AUTO",
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }

            // OBD-II Connection Status Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF181B24) else Color(0xFFDCE1EC))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (obdConnected) AutoSuccessGreen else Color(0xFFFF9800))
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = "OBD-II",
                    tint = if (obdConnected) AutoSuccessGreen else mutedColor,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (obdConnected) "OBD-II LIVE" else "OBD SEARCH",
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // GPS Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "GPS Lock",
                    tint = AutoSuccessGreen,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "GPS 3D",
                    color = mutedColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Cellular signal
            Icon(
                imageVector = Icons.Default.SignalCellularAlt,
                contentDescription = "5G Signal",
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
        }

        // Center: Clock & Weather glance
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weather brief
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1A1D27) else Color(0xFFD6DCE8))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Weather",
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "$tempC°C $condition",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Big Clock
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentTimeString.ifEmpty { "14:30" },
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = currentDateString.ifEmpty { "Today" },
                    color = mutedColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Right: Voice Assistant & Quick Actions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Gemini Hands-Free Voice Assistant Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .carPressable(onClick = onOpenVoiceAssistant)
                    .testTag("gemini_voice_assistant_button"),
                contentAlignment = Alignment.Center
            ) {
                // Pulse halo
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(AutoRacingRed.copy(alpha = 0.25f))
                )
                // Core red button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(AutoRacingRed)
                        .border(1.5.dp, AutoPureWhite.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Gemini Voice Assistant",
                        tint = AutoPureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Customize Cards button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1E222E) else Color(0xFFD6DBE7))
                    .carPressable(onClick = onOpenCustomizeCards)
                    .testTag("customize_cards_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Customize Dashboard Cards",
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Night / Day Mode Toggle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDark) Color(0xFF1E222E) else Color(0xFFD6DBE7))
                    .carPressable(onClick = onToggleNightMode)
                    .testTag("toggle_night_mode_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isNightMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Night / Day Mode",
                    tint = if (isNightMode) Color(0xFFFFB300) else AutoRacingRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

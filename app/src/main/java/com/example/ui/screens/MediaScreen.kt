package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MusicState
import com.example.data.model.RadioStation
import com.example.data.model.YouTubeCardItem
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed

@Composable
fun MediaScreen(
    musicState: MusicState,
    radioStation: RadioStation,
    radioPresets: List<RadioStation>,
    youTubeItems: List<YouTubeCardItem>,
    isMoving: Boolean,
    onTogglePlayMusic: () -> Unit,
    onNextTrack: () -> Unit,
    onPrevTrack: () -> Unit,
    onSelectRadioPreset: (RadioStation) -> Unit,
    onSeekRadioPrev: () -> Unit,
    onSeekRadioNext: () -> Unit,
    onToggleRadioBand: () -> Unit,
    onLaunchYouTube: (YouTubeCardItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val surfaceColor = if (isDark) Color(0xFF13161F) else Color(0xFFFFFFFF)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)
    val borderColor = if (isDark) Color(0xFF262C3A) else Color(0xFFCED4E2)

    var selectedMediaTab by remember { mutableIntStateOf(0) } // 0: Music, 1: Radio, 2: YouTube

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("media_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isDark) Color(0xFF181C26) else Color(0xFFE2E7F2))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MediaTabItem(
                title = "MUSIC PLAYER",
                icon = Icons.Default.MusicNote,
                isSelected = selectedMediaTab == 0,
                onClick = { selectedMediaTab = 0 },
                modifier = Modifier.weight(1f)
            )
            MediaTabItem(
                title = "RADIO TUNER",
                icon = Icons.Default.Radio,
                isSelected = selectedMediaTab == 1,
                onClick = { selectedMediaTab = 1 },
                modifier = Modifier.weight(1f)
            )
            MediaTabItem(
                title = "YOUTUBE AUTO",
                icon = Icons.Default.SmartDisplay,
                isSelected = selectedMediaTab == 2,
                onClick = { selectedMediaTab = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceColor)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            when (selectedMediaTab) {
                0 -> {
                    // Full Music Player
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big Album Artwork
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(musicState.albumColorHex))
                                .border(2.dp, AutoPureWhite.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Album,
                                contentDescription = "Album",
                                tint = AutoPureWhite,
                                modifier = Modifier.size(90.dp)
                            )
                        }

                        // Track Details and Large Transport Controls
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AutoRacingRed)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = musicState.source.uppercase(),
                                        color = AutoPureWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = musicState.title,
                                    color = textColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${musicState.artist} • ${musicState.album}",
                                    color = mutedColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Progress Bar
                            val progress = (musicState.currentPositionSec.toFloat() / musicState.durationSec.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AutoRacingRed,
                                trackColor = if (isDark) Color(0xFF262C3A) else Color(0xFFDFE4ED)
                            )

                            // Playback Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF1E222D) else Color(0xFFE5E9F1))
                                        .carPressable(onClick = onPrevTrack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Previous",
                                        tint = textColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(AutoRacingRed)
                                        .border(2.dp, AutoPureWhite, CircleShape)
                                        .carPressable(onClick = onTogglePlayMusic),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (musicState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = AutoPureWhite,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(24.dp))

                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0xFF1E222D) else Color(0xFFE5E9F1))
                                        .carPressable(onClick = onNextTrack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Next",
                                        tint = textColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Full FM/AM Radio Tuner
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${radioStation.band} BROADCAST",
                                    color = AutoRacingRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = radioStation.frequency,
                                    color = textColor,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${radioStation.name} • ${radioStation.genre}",
                                    color = mutedColor,
                                    fontSize = 14.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AutoRacingRed)
                                    .carPressable(onClick = onToggleRadioBand)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SWITCH TO ${if (radioStation.band == "FM") "AM" else "FM"}",
                                    color = AutoPureWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Presets Grid
                        Text(
                            text = "STATION PRESETS",
                            color = mutedColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            radioPresets.forEachIndexed { idx, stn ->
                                val isCur = stn.frequency == radioStation.frequency
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isCur) AutoRacingRed else (if (isDark) Color(0xFF1E222F) else Color(0xFFE2E7F0)))
                                    .carPressable(onClick = { onSelectRadioPreset(stn) }),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "P${idx + 1}",
                                            color = if (isCur) AutoPureWhite else textColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = stn.frequency.split(" ").firstOrNull() ?: "",
                                            color = if (isCur) AutoPureWhite.copy(alpha = 0.9f) else mutedColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // YouTube Automotive Videos
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AUTOMOTIVE CHANNELS & PLAYLISTS",
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )

                            if (isMoving) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AutoRacingRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = AutoRacingRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AUDIO SAFE MODE (CAR MOVING)",
                                        color = AutoRacingRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(youTubeItems) { ytItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDark) Color(0xFF1A1E29) else Color(0xFFF1F4FA))
                                        .carPressable(onClick = { onLaunchYouTube(ytItem) })
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 68.dp, height = 44.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF282C38)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = AutoRacingRed,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = ytItem.title,
                                                color = textColor,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${ytItem.channel} • ${ytItem.duration} • ${ytItem.viewCount}",
                                                color = mutedColor,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AutoRacingRed)
                                            .padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "PLAY",
                                            color = AutoPureWhite,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaTabItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AutoRacingRed else Color.Transparent)
            .carPressable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) AutoPureWhite else (if (isDark) AutoPureWhite.copy(alpha = 0.7f) else Color(0xFF1B1F2C)),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = if (isSelected) AutoPureWhite else (if (isDark) AutoPureWhite.copy(alpha = 0.7f) else Color(0xFF1B1F2C)),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

package com.example.data.model

data class MusicState(
    val title: String = "Midnight City Highway",
    val artist: String = "Synthwave Roadsters",
    val album: String = "Turbo Nights 2026",
    val durationSec: Int = 248,
    val currentPositionSec: Int = 82,
    val isPlaying: Boolean = true,
    val volumeLevel: Float = 0.75f,
    val source: String = "Spotify Car Sync",
    val albumColorHex: Long = 0xFFE50914
)

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val band: String = "FM",
    val genre: String,
    val currentSong: String,
    val isFavorite: Boolean = false
)

data class YouTubeCardItem(
    val id: String,
    val title: String,
    val channel: String,
    val duration: String,
    val category: String,
    val viewCount: String
)

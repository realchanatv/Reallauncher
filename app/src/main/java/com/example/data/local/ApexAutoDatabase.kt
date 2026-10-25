package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "trip_logs")
data class TripLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val distanceKm: Float,
    val maxSpeedKmh: Float,
    val avgSpeedKmh: Float,
    val fuelConsumedLiters: Float,
    val troubleCodesDetected: String = ""
)

@Entity(tableName = "radio_presets")
data class RadioPresetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val frequency: String,
    val band: String = "FM",
    val genre: String,
    val presetSlot: Int
)

@Entity(tableName = "offline_destinations")
data class OfflineDestinationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val category: String, // Home, Work, Charging, Gas, Service
    val distanceKm: Float,
    val estimatedMinutes: Int
)

@Dao
interface ApexAutoDao {
    @Query("SELECT * FROM trip_logs ORDER BY timestamp DESC LIMIT 20")
    fun getAllTripLogs(): Flow<List<TripLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripLog(log: TripLogEntity)

    @Query("SELECT * FROM radio_presets ORDER BY presetSlot ASC")
    fun getRadioPresets(): Flow<List<RadioPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioPreset(preset: RadioPresetEntity)

    @Query("SELECT * FROM offline_destinations ORDER BY name ASC")
    fun getOfflineDestinations(): Flow<List<OfflineDestinationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestinations(destinations: List<OfflineDestinationEntity>)
}

@Database(
    entities = [
        TripLogEntity::class,
        RadioPresetEntity::class,
        OfflineDestinationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ApexAutoDatabase : RoomDatabase() {
    abstract fun apexDao(): ApexAutoDao
}

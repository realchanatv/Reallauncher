package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarLocationManager(private val context: Context) : LocationListener {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _gpsSpeedKmh = MutableStateFlow<Float?>(null)
    val gpsSpeedKmh: StateFlow<Float?> = _gpsSpeedKmh.asStateFlow()

    private val _hasGpsFix = MutableStateFlow(false)
    val hasGpsFix: StateFlow<Boolean> = _hasGpsFix.asStateFlow()

    private val _currentBearing = MutableStateFlow(0f)
    val currentBearing: StateFlow<Float> = _currentBearing.asStateFlow()

    @SuppressLint("MissingPermission")
    fun startListening() {
        try {
            locationManager?.let { lm ->
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        500L,
                        1f,
                        this
                    )
                }
                if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000L,
                        2f,
                        this
                    )
                }
            }
        } catch (_: Exception) {}
    }

    fun stopListening() {
        try {
            locationManager?.removeUpdates(this)
        } catch (_: Exception) {}
    }

    override fun onLocationChanged(location: Location) {
        _hasGpsFix.value = true
        if (location.hasSpeed()) {
            _gpsSpeedKmh.value = location.speed * 3.6f // m/s to km/h
        }
        if (location.hasBearing()) {
            _currentBearing.value = location.bearing
        }
    }

    override fun onProviderEnabled(provider: String) {
        _hasGpsFix.value = true
    }

    override fun onProviderDisabled(provider: String) {
        _hasGpsFix.value = false
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}

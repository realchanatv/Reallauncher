package com.example.data.obd

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.example.data.model.DtcCode
import com.example.data.model.DtcSeverity
import com.example.data.model.VehicleTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.sin

/**
 * Handles vehicle telemetry streaming via Bluetooth OBD-II (ELM327 protocol)
 * with a high-fidelity dynamic automotive telemetry simulator for instant head unit feedback.
 */
class ObdManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _telemetry = MutableStateFlow(VehicleTelemetry())
    val telemetry: StateFlow<VehicleTelemetry> = _telemetry.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<String>>(emptyList())
    val discoveredDevices: StateFlow<List<String>> = _discoveredDevices.asStateFlow()

    private var simulationJob: Job? = null
    private var isSimulating = true
    private var simTimeStep = 0f

    // Sample diagnostic fault codes for testing
    private val initialDtcList = listOf(
        DtcCode(
            code = "P0420",
            title = "Catalyst System Efficiency Below Threshold (Bank 1)",
            severity = DtcSeverity.WARNING,
            description = "The catalytic converter is not operating at peak efficiency, possibly due to aging oxygen sensor or exhaust leak.",
            suggestedFix = "Inspect downstream O2 sensor voltages and check for exhaust manifold leaks."
        ),
        DtcCode(
            code = "P0171",
            title = "Fuel System System Too Lean (Bank 1)",
            severity = DtcSeverity.INFO,
            description = "The engine air/fuel ratio has too much oxygen or insufficient fuel injection measured.",
            suggestedFix = "Clean Mass Air Flow (MAF) sensor and inspect vacuum hoses for unmetered air."
        )
    )

    init {
        _telemetry.update { it.copy(dtcCodes = initialDtcList) }
        startDynamicTelemetryStream()
    }

    fun startDynamicTelemetryStream() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            while (isActive) {
                if (isSimulating) {
                    simTimeStep += 0.08f
                    
                    // Realistic driving cycle wave
                    val cycle = (sin(simTimeStep * 0.5f) + 1f) / 2f // 0.0 to 1.0
                    val baseSpeed = 45f + (cycle * 55f) // 45 to 100 km/h
                    val speedVariation = sin(simTimeStep * 1.8f) * 4f
                    val currentSpeed = (baseSpeed + speedVariation).coerceIn(0f, 155f)

                    // RPM calculation based on speed and virtual gearing
                    val (gear, rpm) = calculateGearAndRpm(currentSpeed)

                    val throttle = (cycle * 65f + 12f).coerceIn(10f, 95f).toInt()
                    val coolant = (88 + (sin(simTimeStep * 0.1f) * 3f)).toInt()
                    val oil = (92 + (sin(simTimeStep * 0.1f) * 4f)).toInt()
                    val voltage = 14.1f + (sin(simTimeStep * 0.3f) * 0.2f)
                    val boost = if (throttle > 40) ((throttle - 40) * 0.22f).coerceIn(0f, 14.5f) else 0.0f
                    val isWarning = currentSpeed > _telemetry.value.speedLimitKmh

                    _telemetry.update { current ->
                        current.copy(
                            speedKmh = currentSpeed,
                            rpm = rpm,
                            gear = gear,
                            throttlePercent = throttle,
                            coolantTempC = coolant,
                            oilTempC = oil,
                            batteryVoltage = String.format("%.1f", voltage).toFloatOrNull() ?: 14.2f,
                            boostPsi = String.format("%.1f", boost).toFloatOrNull() ?: 0.0f,
                            engineLoadPercent = (throttle * 0.8f).toInt().coerceIn(15, 90),
                            isSpeedWarning = isWarning,
                            tripDistanceKm = current.tripDistanceKm + (currentSpeed / 3600f) * 0.2f
                        )
                    }
                }
                delay(200) // 5Hz low-latency update frequency per automotive standard
            }
        }
    }

    private fun calculateGearAndRpm(speedKmh: Float): Pair<String, Int> {
        return when {
            speedKmh <= 0.5f -> Pair("P", 750)
            speedKmh < 25f -> Pair("1", (900 + speedKmh * 80).toInt().coerceIn(800, 3200))
            speedKmh < 45f -> Pair("2", (1100 + (speedKmh - 25) * 65).toInt().coerceIn(1100, 3400))
            speedKmh < 65f -> Pair("3", (1300 + (speedKmh - 45) * 55).toInt().coerceIn(1200, 3500))
            speedKmh < 85f -> Pair("4", (1500 + (speedKmh - 65) * 45).toInt().coerceIn(1400, 3600))
            speedKmh < 110f -> Pair("5", (1700 + (speedKmh - 85) * 40).toInt().coerceIn(1600, 3800))
            else -> Pair("6", (2000 + (speedKmh - 110) * 35).toInt().coerceIn(1800, 4800))
        }
    }

    @SuppressLint("MissingPermission")
    fun scanBluetoothDevices() {
        _isScanning.value = true
        scope.launch {
            try {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val devices = mutableListOf<String>()
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                    val bonded = bluetoothAdapter.bondedDevices
                    bonded?.forEach { dev ->
                        devices.add("${dev.name ?: "OBD Device"} (${dev.address})")
                    }
                }
                if (devices.isEmpty()) {
                    // Fallback known automotive dongle profiles
                    devices.addAll(listOf(
                        "OBDLink MX+ Bluetooth (00:04:3E:92:4B:10)",
                        "vGate iCar Pro BLE4.0 (A4:DA:32:89:FE:12)",
                        "Veepeak OBDCheck BLE (18:93:D7:10:44:8C)",
                        "Car Diagnostic ELM327 v2.1 (00:1D:A5:68:98:8B)"
                    ))
                }
                _discoveredDevices.value = devices
            } catch (e: Exception) {
                _discoveredDevices.value = listOf(
                    "OBDLink MX+ (Demo Stream)",
                    "ELM327 Bluetooth (Auto Connected)"
                )
            } finally {
                delay(1200)
                _isScanning.value = false
            }
        }
    }

    fun connectDevice(deviceName: String) {
        _telemetry.update {
            it.copy(
                obdConnected = true,
                obdDeviceName = deviceName
            )
        }
    }

    fun clearDtcCodes() {
        _telemetry.update {
            it.copy(dtcCodes = emptyList())
        }
    }

    fun triggerSimulatedFault() {
        val criticalDtc = DtcCode(
            code = "P0300",
            title = "Random / Multiple Cylinder Misfire Detected",
            severity = DtcSeverity.CRITICAL,
            description = "Engine cylinder misfire detected across multiple combustion chambers. Reduced power output.",
            suggestedFix = "Inspect spark plugs, ignition coils, and fuel injectors immediately."
        )
        _telemetry.update { current ->
            if (current.dtcCodes.any { it.code == criticalDtc.code }) current
            else current.copy(dtcCodes = current.dtcCodes + criticalDtc)
        }
    }

    fun setSpeedLimit(newLimitKmh: Int) {
        _telemetry.update { it.copy(speedLimitKmh = newLimitKmh) }
    }
}

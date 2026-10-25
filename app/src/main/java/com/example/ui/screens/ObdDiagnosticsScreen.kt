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
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DtcSeverity
import com.example.data.model.VehicleTelemetry
import com.example.ui.components.carPressable
import com.example.ui.theme.AutoPureBlack
import com.example.ui.theme.AutoPureWhite
import com.example.ui.theme.AutoRacingRed
import com.example.ui.theme.AutoSpeedWarning
import com.example.ui.theme.AutoSuccessGreen

@Composable
fun ObdDiagnosticsScreen(
    telemetry: VehicleTelemetry,
    isScanningBluetooth: Boolean,
    discoveredDevices: List<String>,
    onScanBluetooth: () -> Unit,
    onConnectDevice: (String) -> Unit,
    onClearDtcCodes: () -> Unit,
    onTriggerTestFault: () -> Unit,
    onAskGeminiAboutDtc: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    val surfaceColor = if (isDark) Color(0xFF13161F) else Color(0xFFFFFFFF)
    val textColor = if (isDark) AutoPureWhite else Color(0xFF10131A)
    val mutedColor = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274)
    val borderColor = if (isDark) Color(0xFF262C3A) else Color(0xFFCED4E2)

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("obd_diagnostics_screen"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Live Telemetry Gauges
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(surfaceColor)
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AutoRacingRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = AutoPureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "OBD-II LIVE TELEMETRY",
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${telemetry.obdDeviceName} • PROTOCOL ISO-15765-4 CAN",
                                color = mutedColor,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Scan Bluetooth Button
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF202534) else Color(0xFFE2E7F2))
                            .carPressable(onClick = onScanBluetooth)
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isScanningBluetooth) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = AutoRacingRed
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.BluetoothSearching,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isScanningBluetooth) "SCANNING..." else "SCAN BLUETOOTH",
                                color = textColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Live PIDs Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Column 1
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelemetryItemBox(
                        title = "ENGINE SPEED",
                        value = "${telemetry.rpm} RPM",
                        subtitle = "Gear: ${telemetry.gear}",
                        color = AutoRacingRed,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItemBox(
                        title = "COOLANT TEMP",
                        value = "${telemetry.coolantTempC}°C",
                        subtitle = "Target: 85-95°C",
                        color = Color(0xFF29B6F6),
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItemBox(
                        title = "BATTERY / ALT",
                        value = "${telemetry.batteryVoltage} V",
                        subtitle = "Charging nominal",
                        color = AutoSuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Column 2
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TelemetryItemBox(
                        title = "THROTTLE",
                        value = "${telemetry.throttlePercent}%",
                        subtitle = "TPS Signal Valid",
                        color = AutoSpeedWarning,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItemBox(
                        title = "TURBO BOOST",
                        value = "+${telemetry.boostPsi} PSI",
                        subtitle = "MAP Sensor live",
                        color = AutoRacingRed,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItemBox(
                        title = "ENGINE LOAD",
                        value = "${telemetry.engineLoadPercent}%",
                        subtitle = "Calculated Load",
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Right Column: Diagnostic Trouble Codes (DTC)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .background(surfaceColor)
                .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ECU DIAGNOSTIC CODES",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (telemetry.dtcCodes.isEmpty()) "No fault codes stored in ECU" else "${telemetry.dtcCodes.size} trouble code(s) detected",
                        color = if (telemetry.dtcCodes.isEmpty()) AutoSuccessGreen else AutoRacingRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Trigger Test Fault button
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0xFF222634) else Color(0xFFE2E7F0))
                        .carPressable(onClick = onTriggerTestFault)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ ADD FAULT",
                        color = AutoRacingRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Codes List
            if (telemetry.dtcCodes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "All Clear",
                            tint = AutoSuccessGreen,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "POWERTRAIN SYSTEMS HEALTHY",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Zero active OBD-II Diagnostic Trouble Codes",
                            color = mutedColor,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(telemetry.dtcCodes) { dtc ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDark) Color(0xFF1B1E29) else Color(0xFFF1F4FA))
                                .border(
                                    1.dp,
                                    if (dtc.severity == DtcSeverity.CRITICAL) AutoRacingRed else AutoSpeedWarning,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = dtc.code,
                                            color = AutoRacingRed,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = dtc.title,
                                            color = textColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dtc.description,
                                    color = mutedColor,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Ask Gemini AI Button for this code
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AutoRacingRed)
                                        .carPressable(onClick = { onAskGeminiAboutDtc(dtc.code) }),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = AutoPureWhite,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "ASK GEMINI AI TO DIAGNOSE",
                                            color = AutoPureWhite,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Clear DTC Codes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (telemetry.dtcCodes.isNotEmpty()) Color(0xFFB71C1C) else (if (isDark) Color(0xFF1E2230) else Color(0xFFE2E7F0)))
                    .carPressable(
                        onClick = onClearDtcCodes,
                        enabled = telemetry.dtcCodes.isNotEmpty()
                    )
                    .testTag("clear_dtc_codes_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = AutoPureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CLEAR ALL CHECK ENGINE CODES",
                        color = AutoPureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TelemetryItemBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == AutoPureBlack
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDark) Color(0xFF181C26) else Color(0xFFF1F4FA))
            .padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                color = if (isDark) Color(0xFF8F96A8) else Color(0xFF5B6274),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                color = if (isDark) Color(0xFF6C7385) else Color(0xFF808799),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

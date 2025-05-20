package com.cramium.example.ui.component

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun RequestBluetoothPermissions(onResult: (granted: Boolean) -> Unit) {
    // get an ActivityResultLauncher scoped to Compose
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            onResult(perms.filter { it.key != Manifest.permission.BLUETOOTH }.values.all { it })
        }
    )

    // kick off once when this Composable enters the composition
    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }
}

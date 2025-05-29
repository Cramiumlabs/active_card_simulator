package com.cramium.example.ui.simulator

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cramium.activecard.utils.QRCodeUtil
import com.cramium.example.ui.component.RegisterUserDialog
import com.cramium.example.ui.component.RequestBluetoothPermissions
import com.cramium.example.ui.demo.getDeviceUuid

@Composable
fun SimulatorScreen(
    modifier: Modifier = Modifier,
    vm: SimulatorViewModel = hiltViewModel(),
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> vm.getUser()
                else -> {}
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
    RequestBluetoothPermissions { granted ->
        if (granted) {
            vm.start()
        } else {
            Toast.makeText(context, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello ${uiState.userName}!")
        if (!uiState.isRegister) {
            RegisterUserDialog(onDone = { pair ->
                val (userName, deviceName) = pair
                vm.saveUserName(userName, deviceName)
            })
        } else {
            Box(
                modifier = modifier
                    .size(300.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            ) {
                Image(
                    bitmap = QRCodeUtil.generateQRCode(
                        """
                        {
                          "identityPublicKey": ${uiState.identityPubKey},
                          "deviceId": ${context.getDeviceUuid()},
                          "deviceName": ${uiState.deviceName},
                          "ownerUser": ${uiState.userName},
                          "firmwareVersion": "1.0.0",
                          "timestamp": ${System.currentTimeMillis()}
                        }
                    """.trimIndent(),
                        200,200
                    ).asImageBitmap(),
                    contentDescription = "Dialog Image",
                    modifier = Modifier
                        .size(300.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                )
            }

            Button(
                onClick = { vm.startKeyGen() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start keygen")
            }
        }
    }
}
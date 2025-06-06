package com.cramium.example.ui.simulator

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val logs by vm.logs.collectAsState()
    val listState = rememberLazyListState()
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
        Spacer(Modifier.height(32.dp))
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
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Allow LazyColumn to take available space
                .padding(top = 8.dp)
        ) {
            items(logs) { text ->
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp, horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)
                )
            }
        }

        LaunchedEffect(logs.size) {
            if (logs.isNotEmpty()){
                listState.animateScrollToItem(index = logs.lastIndex)
            }
        }

    }
}
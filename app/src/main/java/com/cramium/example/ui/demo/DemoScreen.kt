package com.cramium.example.ui.demo

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun DemoScreen(
    vm: DemoViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    vm.initClient(context)
    var username by remember { mutableStateOf("") }
    val qrScanLauncher =
        rememberLauncherForActivityResult(contract = ScanContract(), onResult = { result ->
            if (result.contents != null) {
                vm.startScan(result.contents)
            }
        })

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome!")

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { vm.register(username) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { vm.authenticate(username) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Sign In")
                }
            }

            Button(onClick = {
                qrScanLauncher.launch(ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setOrientationLocked(false)
                    setBeepEnabled(false)
                })
            }, modifier = Modifier.padding(16.dp)) {
                Text("Start scan QR code")
            }
            Button(
                onClick = {
                    vm.connectToDevice {
                        Toast.makeText(context, "Device authenticated", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Connect to device")
            }
            Button(
                onClick = { vm.registerPaillierGroup() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Create paillier group")
            }

            Button(
                onClick = { vm.registerGroup() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Create keygen group")
            }
        }
    }
}
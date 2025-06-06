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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val uiState by vm.uiState.collectAsState()
    val textStyle = TextStyle(fontSize = 12.sp)
    val qrScanLauncher =
        rememberLauncherForActivityResult(contract = ScanContract(), onResult = { result ->
            if (result.contents != null) {
                vm.startScan(result.contents)
            }
        })
    val listState = rememberLazyListState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { vm.register(username) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Sign Up", style = textStyle)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { vm.authenticate(username) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text("Sign In", style = textStyle)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )  {
                Button(onClick = {
                    qrScanLauncher.launch(ScanOptions().apply {
                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        setOrientationLocked(false)
                        setBeepEnabled(false)
                    })
                }, modifier = Modifier.padding(4.dp)) {
                    Text("Start scan QR code", style = textStyle)
                }
                Button(
                    onClick = {
                        vm.connectToDevice {
                            Toast.makeText(context, "Device authenticated", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Connect to device", style = textStyle)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { vm.registerPaillierGroup() },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Create paillier group", style = textStyle)
                }

                Button(
                    onClick = { vm.registerGroup() },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Create keygen group", style = textStyle)
                }
            }

            Button(
                onClick = { vm.signing() },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Send eth transaction", style = textStyle)
            }


            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Allow LazyColumn to take available space
                    .padding(top = 8.dp)
            ) {
                items(uiState.logs) { text ->
                    Text(
                        text = text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp)
                    )
                }
            }

            LaunchedEffect(uiState) {
                if (uiState.logs.isNotEmpty()){
                    listState.animateScrollToItem(index = uiState.logs.lastIndex)
                }
            }

        }
    }
}
package com.cramium.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramium.example.ui.component.RequestBluetoothPermissions
import com.cramium.example.ui.demo.DemoScreen
import com.cramium.example.ui.simulator.SimulatorScreen
import com.cramium.example.ui.theme.ExampleTheme
import com.polidea.rxandroidble3.exceptions.BleException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

// Define your screen routes (example)
object AppDestinations {
    const val MAIN = "main"
    const val DEMO = "demo"
    const val SIMULATOR = "simulator"
    // Add other destinations
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException && throwable.cause is BleException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable)
        }
        enableEdgeToEdge()

        setContent {
            ExampleTheme {
                RequestBluetoothPermissions { granted ->
                    if (!granted) Toast.makeText(
                        this,
                        "Bluetooth permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AppDestinations.MAIN
                ) {
                    composable(route = AppDestinations.MAIN) {
                        MainScreen(
                            onNavigateDemo = { navController.navigate(AppDestinations.DEMO) },
                            onNavigateSimulator = { navController.navigate(AppDestinations.SIMULATOR) }
                        )
                    }
                    composable(route = AppDestinations.DEMO) {
                        DemoScreen(context = this@MainActivity)
                    }
                    composable(route = AppDestinations.SIMULATOR) {
                        SimulatorScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    onNavigateDemo: () -> Unit = {},
    onNavigateSimulator: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNavigateDemo,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Demo application")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateSimulator,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Active Card Simulator")
        }
    }
}
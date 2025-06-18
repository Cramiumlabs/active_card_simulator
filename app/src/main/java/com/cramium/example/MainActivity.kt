package com.cramium.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cramium.example.ui.component.RequestBluetoothPermissions
import com.cramium.example.ui.demo.DemoScreen
import com.cramium.example.ui.screen.HomeScreen
import com.cramium.example.ui.screen.LocalPinScreen
import com.cramium.example.ui.screen.MpcGroupScreen
import com.cramium.example.ui.screen.SecuritySettingScreen
import com.cramium.example.ui.screen.TransactionApprovalScreen
import com.cramium.example.ui.screen.TransactionApprovedScreen
import com.cramium.example.ui.screen.TransactionRejectedScreen
import com.cramium.example.ui.screen.TransactionScreen
import com.cramium.example.ui.screen.WalletScreen
import com.cramium.example.ui.simulator.SimulatorScreen
import com.cramium.example.ui.theme.ExampleTheme
import com.polidea.rxandroidble3.exceptions.BleException
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins

// Define your screen routes (example)
object AppDestinations {
    const val MAIN = "main"
    const val DEMO = "demo"
    const val SIMULATOR = "simulator"
    const val HOME = "home"
    const val MPC_GROUP = "mpc_group"
    const val WALLET_LIST = "wallet_list"
    const val TRANSACTION_INFO = "transaction_info"
    const val SECURITY_SETTING = "security_setting"
    const val LOCAL_PIN = "local_pin"
    const val TRANSACTION_APPROVAL = "transaction_approval"
    const val TRANSACTION_APPROVED = "transaction_approved"
    const val TRANSACTION_REJECTED = "transaction_rejected"
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
                val navController = rememberNavController()
                CompositionLocalProvider(
                    LocalNavigation provides navController
                ) {
                    RequestBluetoothPermissions { granted ->
                        if (!granted) Toast.makeText(
                            this, "Bluetooth permission denied", Toast.LENGTH_SHORT
                        ).show()
                    }

                    NavHost(
                        navController = navController, startDestination = AppDestinations.MAIN
                    ) {
                        composable(route = AppDestinations.MAIN) {
                            MainScreen(
                                onNavigateDemo = { navController.navigate(AppDestinations.DEMO) },
                                onNavigateSimulator = { navController.navigate(AppDestinations.SIMULATOR) },
                                onNavigateHome = { navController.navigate(AppDestinations.HOME) },
                            )
                        }
                        composable(route = AppDestinations.DEMO) {
                            DemoScreen(context = this@MainActivity)
                        }
                        composable(route = AppDestinations.SIMULATOR) {
                            SimulatorScreen()
                        }
                        composable(route = AppDestinations.HOME) {
                            HomeScreen()
                        }

                        composable(route = AppDestinations.MPC_GROUP) {
                            MpcGroupScreen()
                        }

                        composable(route = AppDestinations.WALLET_LIST) {
                            WalletScreen()
                        }

                        composable(route = AppDestinations.TRANSACTION_INFO) {
                            TransactionScreen()
                        }

                        composable(route = AppDestinations.SECURITY_SETTING) {
                            SecuritySettingScreen()
                        }

                        composable(route = AppDestinations.LOCAL_PIN) {
                            LocalPinScreen()
                        }

                        composable(route = AppDestinations.TRANSACTION_APPROVAL) {
                            TransactionApprovalScreen()
                        }

                        composable(route = AppDestinations.TRANSACTION_APPROVED) {
                            TransactionApprovedScreen()
                        }

                        composable(route = AppDestinations.TRANSACTION_REJECTED) {
                            TransactionRejectedScreen()
                        }
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
    onNavigateHome: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNavigateDemo, modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Demo application")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateSimulator, modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Active Card Simulator")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateHome, modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Home menu")
        }
    }
}


@SuppressLint("ComposeCompositionLocalUsage")
val LocalNavigation =
    compositionLocalOf<NavHostController> { error("NavHostController Context Not Found!") }
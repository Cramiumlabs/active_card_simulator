package com.cramium.example.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun BiometricPrompt(
    onAuthenticated: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalContext.current as FragmentActivity
    // Executor on the main thread:
    val executor = remember { ContextCompat.getMainExecutor(context) }
    // Build the prompt info once:
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock with biometrics")
            .setSubtitle("Use your fingerprint or device PIN/password")
//            .setNegativeButtonText("Cancel")
            // allow strong biometrics OR device credential fallback
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
    }
    // Hold onto the BiometricPrompt instance:
    val biometricPrompt = remember {
        BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onAuthenticated()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // You can choose to notify the user here if you like
                }
            }
        )
    }

    biometricPrompt.authenticate(promptInfo)
}

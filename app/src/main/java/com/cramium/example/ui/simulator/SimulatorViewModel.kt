package com.cramium.example.ui.simulator

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramium.activecard.ActiveCardServer
import com.cramium.activecard.ActiveCardServerCallback
import com.cramium.activecard.ActiveCardServerImpl
import com.cramium.sdk.client.MpcClient
import com.cramium.sdk.client.MpcClientImpl
import com.cramium.sdk.service.GoogleServiceImpl
import com.cramium.sdk.utils.stringToByteArray
import com.cramium.sdk.utils.toHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimulatorViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {
    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USERNAME = "key_username"
        private const val KEY_DEVICE_NAME = "key_device_name"
        private const val KEY_AC_PUBLIC_KEY = "key_ac_public_key"
        private const val KEY_AC_PRIVATE_KEY = "key_ac_private_key"
        private const val KEY_MOBILE_PUBLIC_KEY = "key_mobile_public_key"
    }
    private val mpcClient: MpcClient
    private val acSimulator: ActiveCardServer
    private var keygenJob: Job? = null

    init {
        val acServerCallback: ActiveCardServerCallback = ActiveCardServerCallback()
        mpcClient = MpcClientImpl(
            context = applicationContext,
            apiKey = "YOUR_API_KEY",
            serverAddress = "YOUR_SERVER_ADDRESS",
            mode = "development",
            authServerAddress = "https://example.com",
            cloudService = GoogleServiceImpl(applicationContext, "YOUR_PROJECT_ID"),
            localPartyCallback = acServerCallback,
            isLocalParty = true
        )
        acSimulator = ActiveCardServerImpl(applicationContext, acServerCallback, mpcClient)
        keygenJob =  acSimulator.keygen()
    }
    private val _uiState = MutableStateFlow(ACSimulatorState())
    val uiState: StateFlow<ACSimulatorState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ACSimulatorState()
        )

    fun saveUserName(userName: String, deviceName: String) {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USERNAME, userName)
            .putString(KEY_DEVICE_NAME, deviceName)
            .apply()
        _uiState.value =
            _uiState.value.copy(isRegister = true, userName = userName, deviceName = deviceName)
        Log.d("AC_Simulator", "Save username: $userName and device name: $deviceName")
        stop()
        start()
    }

    fun getUser(): String? {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userName = prefs.getString(KEY_USERNAME, null)
        val deviceName = prefs.getString(KEY_DEVICE_NAME, null)
        val (publicKey, _) = getIdentityKeyPair()
        _uiState.value = _uiState.value.copy(
            isRegister = userName != null,
            userName = userName ?: _uiState.value.userName,
            deviceName = deviceName ?: _uiState.value.deviceName,
            identityPubKey = publicKey.toHexString()
        )
        return userName
    }

    private fun getIdentityKeyPair(): Pair<ByteArray, ByteArray> {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val newPairKey = mpcClient.generateIdentityKeyPair()
        val publicKey =
            prefs.getString(KEY_AC_PUBLIC_KEY, null)?.stringToByteArray() ?: newPairKey.publicKey
        val privateKey =
            prefs.getString(KEY_AC_PRIVATE_KEY, null)?.stringToByteArray() ?: newPairKey.privateKey
        prefs.edit()
            .putString(KEY_AC_PUBLIC_KEY, publicKey.toHexString())
            .putString(KEY_AC_PRIVATE_KEY, privateKey.toHexString())
            .apply()
        return Pair(publicKey, privateKey)
    }

    private fun stop() {
        acSimulator.stopAdvertising()
    }

    fun start() {
        if (!uiState.value.isRegister) return
        acSimulator.startAdvertising(_uiState.value.deviceName)
        val (publicKey, privateKey) = getIdentityKeyPair()

        viewModelScope.launch {
            Log.d("AC_Simulator", "Start authentication flow")
            acSimulator.observeAuthenticationFlow(
                publicKey,
                privateKey,
                saveMobilePublicKey = {
                    Log.d("AC_Simulator", "Authentication successful mobile key: ${it.toHexString()}")
                    val prefs =
                        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putString(KEY_MOBILE_PUBLIC_KEY, it.toHexString()).apply()
                }
            ).collect {}
        }
    }

}

data class ACSimulatorState(
    val isRegister: Boolean = true,
    val deviceName: String = "AC_Simulator",
    val userName: String = "Android",
    val identityPubKey: String = "BASE64_ENCODED_PUBKEY"
)

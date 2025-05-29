package com.cramium.example.ui.demo

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramium.activecard.ActiveCardClient
import com.cramium.activecard.ActiveCardClientCallback
import com.cramium.activecard.ActiveCardClientImpl
import com.cramium.activecard.ble.ConnectionUpdateError
import com.cramium.activecard.ble.ConnectionUpdateSuccess
import com.cramium.activecard.ble.ScanInfo
import com.cramium.activecard.ble.model.ConnectionState
import com.cramium.activecard.simulator.ActiveCardQr
import com.cramium.sdk.client.MpcClient
import com.cramium.sdk.client.MpcClientImpl
import com.cramium.sdk.client.Passkey
import com.cramium.sdk.client.PasskeyImpl
import com.cramium.sdk.model.mpc.MpcGroup
import com.cramium.sdk.repositories.UDMRepository
import com.cramium.sdk.repositories.UDMRepositoryImpl
import com.cramium.sdk.service.GoogleServiceImpl
import com.cramium.sdk.utils.Constants
import com.cramium.sdk.utils.stringToByteArray
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class DemoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private var activeCardClient: ActiveCardClient? = null
    private var activeCardDevice: ScanInfo? = null
    private var client: MpcClient? = null
    private var scanJob: Job? = null
    private var keygenJob: Job? = null
    private var passkey: Passkey? = null
    private var udmRepository: UDMRepository? = null
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .serializeNulls()
        .create()
    private var activeCardQr: ActiveCardQr? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    companion object {
        // TODO: Replace your access token here
        const val MODE = "production"
        const val AUTH_SERVER_ADDRESS = "https://trust-auth.mpc-dev.cramiumtech.com"
        const val SERVER_ADDRESS = "trust-api-edge.mpc-dev.cramiumlabs.com"
        const val SERVER_ID = "230303543292-reoj1d8ffas6hrjs2t7nfpakgl2dn6cn.apps.googleusercontent.com"
        const val API_KEY = "gXjV2AdnVdQ86wHpxu7JxCPIoYvhbKds"
    }

    fun initClient(activityContext: Context) {
        val ggService = GoogleServiceImpl(context, SERVER_ID)
        passkey = PasskeyImpl(activityContext, API_KEY, AUTH_SERVER_ADDRESS)
        val activeCardCallback = ActiveCardClientCallback()
        val mpcClient = MpcClientImpl(
            context = activityContext,
            mode = MODE,
            authServerAddress = AUTH_SERVER_ADDRESS,
            serverAddress = SERVER_ADDRESS,
            apiKey = API_KEY,
            cloudService = ggService,
            localPartyCallback = activeCardCallback
        )
        client = mpcClient
        activeCardClient = ActiveCardClientImpl(context, activeCardCallback, mpcClient)
    }

    fun startScan(qrCode: String) {
        val qrResult = gson.fromJson(qrCode, ActiveCardQr::class.java)
        Log.d("AC_Simulator", "QR code result: $qrResult")
        activeCardQr = qrResult
        scanJob = viewModelScope.launch {
            Log.d("AC_Simulator", "Start scan for device: ${qrResult.deviceName}")
            activeCardClient
                ?.scanForDevices()
                ?.filter { it.name == qrResult.deviceName }
                ?.collect {
                    activeCardDevice = it
                }
        }
    }

    fun connectToDevice(onDone: () -> Unit) {
        activeCardDevice?.let {
            Log.d("AC_Simulator", "Start connect to device: ${it.name} - ${it.deviceId}")
            activeCardClient?.connectToDevice(it.deviceId)
        }
        scope.launch {
            activeCardClient
                ?.connectionUpdate
                ?.collect { connection ->
                    Log.d("AC_Simulator", "Connection status: $connection")
                    when (connection) {
                        is ConnectionUpdateSuccess -> {
                            if (connection.connectionState == ConnectionState.DISCONNECTED) {
                                activeCardClient?.disconnect(connection.deviceId)
                                keygenJob?.cancel()
                                keygenJob = null
                            }
                            else if (connection.connectionState == ConnectionState.CONNECTED) {
                                Log.d("AC_Simulator", "Connected to device: ${connection.deviceId}")
//                                startAuthenticationFlow(onDone)
                                activeCardClient?.negotiateMtuSize(connection.deviceId, 250)?.collect {}
                            }
                        }

                        is ConnectionUpdateError -> Log.d(
                            "MainActivity",
                            "Connection error: ${connection.errorMessage}"
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startAuthenticationFlow(onDone: () -> Unit) {
        Log.d("AC_Simulator", "Start authentication flow")
        val id = activeCardDevice?.deviceId ?: return
        val ac = activeCardQr ?: return
        scope.launch IoScope@{
            val identityKey = client?.generateIdentityKeyPair()
            Log.d("AC_Simulator", "Start authentication flow")
            identityKey?.let { mobileKey ->
                activeCardClient
                    ?.authenticateFlow(id, ac.identityPublicKey.stringToByteArray(), mobileKey.publicKey, mobileKey.privateKey) {
                        CoroutineScope(Dispatchers.Main).launch {
                            udmRepository?.registerAcDevice(id, ac.deviceName, ac.firmwareVersion)
                                ?.flatMapLatest {
                                    udmRepository?.getLinkDevices() ?: throw Exception()
                                }
                                ?.map {
                                    Log.d("AC_Simulator", "Link devices: $it")
                                    onDone()
                                }
                                ?.launchIn(this)

                        }

                    }

            }
        }
    }

    fun register(username: String) {
        viewModelScope.launch {
            passkey?.register(username)
                ?.catch {
                    Log.d("MainActivityViewModel", "Register passkey error: $it")
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                ?.collect { response ->
                    Log.d("MainActivityViewModel", "Register passkey response: $response")
                }
        }
    }

    fun authenticate(username: String) {
        CoroutineScope(Dispatchers.Default).launch {
            passkey?.authenticate(username)
                ?.catch {
                    Log.d("MainActivityViewModel", "Authenticate passkey error: $it")
                }
                ?.collect { response ->
                    Log.d("MainActivityViewModel", "Authenticate passkey response: $response")
                    client?.setAccessToken(response.accessToken)
                    udmRepository = UDMRepositoryImpl(
                        "https://trust-fabric.mpc-dev.cramiumtech.com",
                        response.accessToken
                    )
                }
        }
    }


    var keygenGroup: MpcGroup? = null

    fun startKeygenFlow() {
        val id = activeCardDevice?.deviceId ?: return
        if (keygenJob != null) return
        keygenJob = activeCardClient?.keygen(id)
    }

    fun registerPaillierGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                startKeygenFlow()
                val paillierGroup = client!!.createPaillierGroup(3)
                client?.registerNewLocalPartyToMpcGroup(paillierGroup.id)
                delay(2000)
                client?.newPaillier(paillierGroup.id)
            } catch (e: Exception) {
                Log.d("AC_Simulator", "Paillier error: $e - ${e.cause?.message}")
            }
        }
    }

    fun registerGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                startKeygenFlow()
                val paillierGroup = client!!.getMpcGroups(pageSize = 5, pageToken = "").first().groups.first()
                keygenGroup  = client!!.createMpcKeygenGroup(3, "AC", paillierGroup.id)
                Log.d("AC_Simulator", "Keygen group id: ${keygenGroup!!.id}")
                client?.registerNewLocalPartyToMpcGroup(keygenGroup!!.id)
                client?.newMnemonicKeyGen(keygenGroup!!, Constants.mnemonic, Constants.mnemonicWallets)
            } catch (e: Exception) {
                Log.d("AC_Simulator", "Keygen error: $e - ${e.cause?.message}")
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
    }
}

fun Context.getDeviceUuid(): String {
    // Returns a 64-bit hex string, e.g. "9774d56d682e549c"
    return Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

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
import com.cramium.sdk.utils.toHexString
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import org.sol4k.Connection
@HiltViewModel

class DemoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState: MutableStateFlow<DemoState> = MutableStateFlow(DemoState())
    val uiState
        get() = _uiState.stateIn(viewModelScope, SharingStarted.Eagerly, DemoState())
    private var activeCardClient: ActiveCardClient? = null
    private var activeCardDevice: ScanInfo? = null
    private var client: MpcClient? = null
    private var scanJob: Job? = null
    private var keygenJob: Job? = null
    private var signingJob: Job? = null
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

    private val okHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(10L, TimeUnit.SECONDS)
        writeTimeout(10L, TimeUnit.SECONDS)
        readTimeout(10L, TimeUnit.SECONDS)
    }
        .build()

    private val web3j: Web3j = Web3j.build(
        HttpService(
            "https://ethereum-sepolia.core.chainstack.com/45687b9558255137a0c9c1627d28f644",
            okHttpClient
        )
    )
    private val connection = Connection("https://solana-devnet.core.chainstack.com/11158aef7eaf0a9c01fe6e31ddd07d42")

    private suspend fun getCurrentGasPrice(): BigInteger? {
        return try {
            val ethGasPrice = web3j.ethGasPrice().sendAsync().await() // Using .await() for coroutines
            ethGasPrice.gasPrice
        } catch (e: Exception) {
            // Handle error (e.g., network issue, node unavailable)
            println("Error fetching gas price: ${e.message}")
            null
        }
    }

    private suspend fun getAccountNonce(accountAddress: String): BigInteger? {
        return try {
            val ethGetTransactionCount = web3j.ethGetTransactionCount(
                accountAddress,
                DefaultBlockParameterName.LATEST
            ).sendAsync().await() // Using .await() for coroutines
            ethGetTransactionCount.transactionCount
        } catch (e: Exception) {
            println("Error fetching nonce for $accountAddress: ${e.message}")
            null
        }
    }


    private suspend fun estimateGasLimit(fromAddress: String, toAddress: String, data: String? = null, value: BigInteger? = null): BigInteger? {
        return try {
            val transaction = Transaction.createEthCallTransaction(fromAddress, toAddress, data, value)
            val ethEstimateGas = web3j.ethEstimateGas(transaction).sendAsync().await()
            if (ethEstimateGas.hasError()) {
                addLog("Error estimating gas: ${ethEstimateGas.error.message}")
                null
            } else {
                ethEstimateGas.amountUsed
            }
        } catch (e: Exception) {
            // Handle error
            addLog("Error estimating gas limit: ${e.message}")
            null
        }
    }

    private suspend fun sendSignedTransaction(signedTransactionData: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val ethSendTx: EthSendTransaction = web3j.ethSendRawTransaction(signedTransactionData)
                    .sendAsync()
                    .await()
                if (ethSendTx.hasError()) {
                    addLog("Error sending transaction: ${ethSendTx.error.message}")
                    null
                } else {
                    val txHash = ethSendTx.transactionHash
                    addLog("Transaction sent successfully, txHash: $txHash")
                    txHash
                }
            } catch (e: Exception) {
                addLog("Exception sending transaction: ${e.message}")
                null
            }
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

    private fun addLog(log:String) = _uiState.update { it.copy(logs = it.logs.toMutableList().plus(log)) }
    fun startScan(qrCode: String) {
        val qrResult = gson.fromJson(qrCode, ActiveCardQr::class.java)
        activeCardQr = qrResult
        scanJob = viewModelScope.launch {
            addLog("Start scan for device: ${qrResult.deviceName}")
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
            addLog("Start connect to device: ${it.name} - ${it.deviceId}")
            Log.d("AC_Simulator", "Start connect to device: ${it.name} - ${it.deviceId}")
            activeCardClient?.connectToDevice(it.deviceId)
        }
        scope.launch {
            activeCardClient
                ?.connectionUpdate
                ?.collect { connection ->
                    addLog("Connection status: $connection")
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
                                startAuthenticationFlow(onDone)
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

    private var authenticationJob: Job? = null
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun startAuthenticationFlow(onDone: () -> Unit) {
        addLog("Start authentication flow")
        Log.d("AC_Simulator", "Start authentication flow")
        val id = activeCardDevice?.deviceId ?: return
        val ac = activeCardQr ?: return
        val identityKey = client?.generateIdentityKeyPair()
        Log.d("AC_Simulator", "Start authentication flow")
        identityKey?.let { mobileKey ->
            authenticationJob = activeCardClient
                ?.authenticateFlow(id, ac.identityPublicKey.stringToByteArray(), mobileKey.publicKey, mobileKey.privateKey, onLog = { addLog(it) }) {
                    CoroutineScope(Dispatchers.Main).launch { onDone() }
                    authenticationJob?.cancel()
                    authenticationJob = null
//                    CoroutineScope(Dispatchers.Main).launch {
//                        udmRepository?.registerAcDevice(id, ac.deviceName, ac.firmwareVersion)
//                            ?.flatMapLatest {
//                                udmRepository?.getLinkDevices() ?: throw Exception()
//                            }
//                            ?.map {
//                                Log.d("AC_Simulator", "Link devices: $it")
//                                onDone()
//                                authenticationJob?.cancel()
//                                authenticationJob = null
//                            }
//                            ?.launchIn(this)
//                    }
                }
        }
    }

    fun register(username: String) {
        viewModelScope.launch {
            passkey?.register(username)
                ?.catch {
                    addLog("Register passkey error: $it")
                    Log.d("MainActivityViewModel", "Register passkey error: $it")
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                ?.collect { response ->
                    addLog("Register passkey response: ${response.verified}")
                    Log.d("MainActivityViewModel", "Register passkey response: $response")
                }
        }
    }

    fun authenticate(username: String) {
        CoroutineScope(Dispatchers.Default).launch {
            passkey?.authenticate(username)
                ?.catch {
                    addLog("Authenticate passkey error: $it")
                    Log.d("MainActivityViewModel", "Authenticate passkey error: $it")
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                ?.collect { response ->
                    addLog("Authenticate passkey response: ${response.verified}")
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

    private fun startFlow() {
        val id = activeCardDevice?.deviceId ?: return
        if (keygenJob == null) keygenJob = activeCardClient?.activeCardFlow(id) { addLog(it) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun signingSolana() {
        viewModelScope.launch {
            startFlow()
            client?.getBackup()
                ?.flatMapLatest { backup ->
                    val paillierGroup = backup.paillierGroup ?: throw Exception()
                    val masterWallet = backup.masterWallets.last()
                    val toAddress = "7fk4ZUAfd1rjq5RXi7Uvjr9goA5jR1Kvz3kAwvzp8xbY"
                    val ethWallet = masterWallet.wallets.first { it.chainId == "900" }
                    client!!.buildSolTransaction(
                        paillierGroupId = paillierGroup.id,
                        keygenGroupId = masterWallet.groupId,
                        wallet = ethWallet,
                        amount = "100000",
                        chainId = "solana-devnet",
                        fromAddress = ethWallet.address,
                        toAddress = toAddress,
                        amountInUsd = 0,
                    )
                }
                ?.catch {
                    addLog("Transaction error: $it")
                }
                ?.flatMapLatest { data ->
                    addLog("Transaction done: ${data.toHexString()}")
                    val signature = connection.sendTransaction(data)
                    flowOf(signature)
                }
                ?.flowOn(Dispatchers.IO)
                ?.collect { signature ->
                    Log.d("DemoViewModel", "Transaction done: $signature")
                    addLog("Transaction sent successfully, txHash: $signature")
                }

        }
    }

    fun signingEth() {
        viewModelScope.launch {
            startFlow()
            client?.getBackup()
                ?.flatMapLatest { backup ->
                    val paillierGroup = backup.paillierGroup ?: throw Exception()
                    val masterWallet = backup.masterWallets.last()
                    val toAddress = "0x682A0B80a4f6966c0950513a8A6D4C6074ff077c"
                    val ethWallet = masterWallet.wallets.first { it.chainId == "1" }
                    val nonce = getAccountNonce(ethWallet.address) ?: throw Exception()
                    val gasPrice = getCurrentGasPrice() ?: throw Exception()
                    val getEstGas = estimateGasLimit(ethWallet.address, toAddress, null, BigInteger.valueOf(0.0001.toLong()))?: throw Exception()
                    client!!.buildETHTransaction(
                        paillierGroupId = paillierGroup.id,
                        keygenGroupId = masterWallet.groupId,
                        walletAddress = ethWallet.address,
                        wallet = ethWallet,
                        amount = "100000000000000",
                        gasLimit = getEstGas.toString(),
                        gasPrice = gasPrice.toString(),
                        nonce = nonce.toString(),
                        chainId = "11155111",
                        toAddress = toAddress,
                        ignoringPayloadVerify = false,
                        data = byteArrayOf(),
                        amountInUsd = 0,
                    )
                }
                ?.flowOn(Dispatchers.IO)
                ?.catch {
                    addLog("Transaction error: $it")
                }
                ?.collect { data ->
                    addLog("Transaction done: ${data.toHexString()}")
                    sendSignedTransaction(data.toHexString())
                }

        }
    }

    fun registerPaillierGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                startFlow()
                val paillierGroup = client!!.createPaillierGroup(3)
                client?.registerNewLocalPartyToMpcGroup(paillierGroup.id)
                delay(2000)
                client?.newPaillier(paillierGroup.id)
                addLog("Register paillier group success id: ${paillierGroup.id}")
            } catch (e: Exception) {
                addLog("Paillier error: $e - ${e.cause?.message}")
                Log.d("AC_Simulator", "Paillier error: $e - ${e.cause?.message}")
            }
        }
    }

    fun registerGroup() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                startFlow()
                val paillierGroup = client!!.getMpcGroups(pageSize = 5, pageToken = "").first().groups.first()
                keygenGroup  = client!!.createMpcKeygenGroup(3, "AC", paillierGroup.id)
                Log.d("AC_Simulator", "Keygen group id: ${keygenGroup!!.id}")
                client?.registerNewLocalPartyToMpcGroup(keygenGroup!!.id)
                client?.newMnemonicKeyGen(keygenGroup!!, Constants.mnemonic, Constants.mnemonicWallets)
                addLog("Register keygen group success id: ${keygenGroup!!.id}")
            } catch (e: Exception) {
                addLog("Keygen error: $e - ${e.cause?.message}")
                Log.d("AC_Simulator", "Keygen error: $e - ${e.cause?.message}")
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
    }
}

data class DemoState(
    val logs: List<String> = emptyList()
)

fun Context.getDeviceUuid(): String {
    // Returns a 64-bit hex string, e.g. "9774d56d682e549c"
    return Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

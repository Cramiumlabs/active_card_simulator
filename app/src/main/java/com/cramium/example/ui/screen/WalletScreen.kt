package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramium.example.ui.component.MPCGroupItem
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme
import com.cramium.sdk.model.mpc.MpcGroup

@Composable
fun WalletScreen() {
    WalletUIScreen {}
}


@Composable
internal fun WalletUIScreen(
    event: (AppEvent) -> Unit,
) {

    Scaffold(topBar = {
        TopHeader(title = "Wallet List")
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            val menuItems = remember {
                IntArray(5) { it }.map {
                    MpcGroup(
                        id = "mpc_group_$it",
                        numParties = 3L,
                        threshold = 2L,
                        protocol = "gg20",
                        name = "Personal Wallet Group",
                        shard = ByteArray(32) { it.toByte() },
                        serverShard = ByteArray(32) { (it + 5).toByte() },
                        type = 1L // Could represent "savings", "express", etc.
                    )
                }
            }

            menuItems.forEach { group ->
                MPCGroupItem(group = group, onSetting = {})
            }

        }
    })

}


@Preview(showBackground = true)
@Composable
fun WalletUIScreenPreview() {
    ExampleTheme {
        WalletUIScreen {}
    }
}
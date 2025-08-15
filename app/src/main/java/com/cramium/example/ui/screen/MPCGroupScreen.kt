package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramium.example.AppDestinations
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.MenuItemButton
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun MpcGroupScreen() {
    val nav = LocalNavigation.current
    MpcGroupUIScreen { event ->
        when (event) {
            is AppEvent.WalletList -> {
                nav.navigate(AppDestinations.WALLET_LIST)
            }

            is AppEvent.TransactionInfo -> {
                nav.navigate(AppDestinations.TRANSACTION_INFO)
            }

            else -> Unit
        }
    }
}


@Composable
internal fun MpcGroupUIScreen(
    event: (AppEvent) -> Unit,
) {
    Scaffold(topBar = {
        TopHeader(title = "MPC Groups")
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            val menuItems = remember {
                listOf(
                    "List of Joined (MPC Groups)" to AppEvent.MpcGroupListJoined,
                    "Modification and refresh " to AppEvent.MpcGroupModification,
                    "Wallet List" to AppEvent.WalletList,
                    "Transaction Info " to AppEvent.TransactionInfo,
                )
            }

            menuItems.forEach { (title, action) ->
                MenuItemButton(title = title, onClick = { event(action) })
            }

        }
    })

}


@Preview(showBackground = true)
@Composable
fun MpcGroupScreenPreview() {
    ExampleTheme {
        MpcGroupUIScreen {}
    }
}
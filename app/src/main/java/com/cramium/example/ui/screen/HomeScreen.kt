package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun HomeScreen() {
    val nav = LocalNavigation.current
    HomeUIScreen { event ->
        when (event) {
            is AppEvent.MpcGroup -> {
                nav.navigate(AppDestinations.MPC_GROUP)
            }

            is AppEvent.SecuritySetting -> {
                nav.navigate(AppDestinations.SECURITY_SETTING)
            }

            is AppEvent.TransactionApproval -> {
                nav.navigate(AppDestinations.TRANSACTION_APPROVAL)
            }

            else -> Unit
        }
    }
}


@Composable
internal fun HomeUIScreen(
    event: (AppEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopHeader(title = "Homepage")

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            val menuItems = remember {
                listOf(
                    "Menu" to AppEvent.Menu,
                    "MPC Groups" to AppEvent.MpcGroup,
                    "Security Settings" to AppEvent.SecuritySetting,
                    "Transaction Approval" to AppEvent.TransactionApproval,
                )
            }

            menuItems.forEach { (title, action) ->
                MenuItemButton(title = title, onClick = { event(action) })
            }

        }
    }

}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ExampleTheme {
        HomeUIScreen {}
    }
}

sealed interface AppEvent {
    data object Menu : AppEvent
    data object MpcGroup : AppEvent
    data object SecuritySetting : AppEvent
    data object TransactionApproval : AppEvent

    data object MpcGroupListJoined : AppEvent
    data object MpcGroupModification : AppEvent

    data object WalletList : AppEvent
    data object TransactionInfo : AppEvent

    data object LocalPin : AppEvent
    data object TransactionApproved : AppEvent
    data object TransactionRejected : AppEvent

    data object Nothing : AppEvent
}
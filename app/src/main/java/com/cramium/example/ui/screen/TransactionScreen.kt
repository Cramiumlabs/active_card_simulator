package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramium.example.AppDestinations
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun TransactionScreen() {
    val nav = LocalNavigation.current
    TransactionUIScreen { event ->
        when (event) {
            is AppEvent.WalletList -> {
                nav.navigate(AppDestinations.WALLET_LIST)
            }

            else -> Unit
        }
    }
}


@Composable
internal fun TransactionUIScreen(
    event: (AppEvent) -> Unit,
) {
    Scaffold(topBar = {
        TopHeader(title = "Transaction Summary")
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Name", fontWeight = FontWeight.Normal, color = Color.Gray
            )
            Text(
                text = "Personal Group",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "MPC Configuration", fontWeight = FontWeight.Normal, color = Color.Gray
            )
            Text(
                text = "2-of-4",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Last Backup", fontWeight = FontWeight.Normal, color = Color.Gray
            )
            Text(
                text = "29 May 2025", fontWeight = FontWeight.Bold, color = Color.Black
            )
        }
    })

}


@Preview(showBackground = true)
@Composable
fun TransactionScreenPreview() {
    ExampleTheme {
        TransactionUIScreen {}
    }
}
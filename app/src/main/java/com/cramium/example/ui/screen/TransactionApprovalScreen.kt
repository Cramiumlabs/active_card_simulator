package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramium.example.AppDestinations
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.PrimaryActionButton
import com.cramium.example.ui.component.SecondaryActionButton
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun TransactionApprovalScreen() {
    val nav = LocalNavigation.current
    TransactionApprovalUIScreen { event ->
        when (event) {
            AppEvent.TransactionApproved -> {
                nav.navigate(AppDestinations.TRANSACTION_APPROVED)
            }

            AppEvent.TransactionRejected -> {
                nav.navigate(AppDestinations.TRANSACTION_REJECTED)
            }

            else -> Unit
        }
    }
}


@Composable
internal fun TransactionApprovalUIScreen(
    event: (AppEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopHeader(title = "Transaction Approval")

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Destination", fontWeight = FontWeight.Normal, color = Color.Gray
            )
            Text(
                text = "0x1234567890abcdef1234567890abcdef12345678",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Amount", fontWeight = FontWeight.Normal, color = Color.Gray
            )
            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "$1000",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "0.38 ETH", fontWeight = FontWeight.Bold, color = Color.Black
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryActionButton(
                text = "Approval",
            ) {
                event(AppEvent.TransactionApproved)
            }
            Spacer(modifier = Modifier.height(16.dp))
            SecondaryActionButton(
                text = "Reject",
            ) {
                event(AppEvent.TransactionRejected)
            }
            Spacer(modifier = Modifier.height(64.dp))

        }
    }

}


@Preview(showBackground = true)
@Composable
fun TransactionStatusScreenPreview() {
    ExampleTheme {
        TransactionApprovalUIScreen() {}
    }
}
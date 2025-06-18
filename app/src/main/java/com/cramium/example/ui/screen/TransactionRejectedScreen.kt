package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.PrimaryActionButton
import com.cramium.example.ui.component.SecondaryActionButton
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun TransactionRejectedScreen() {
    val nav = LocalNavigation.current
    TransactionRejectedUIScreen()
}


@Composable
internal fun TransactionRejectedUIScreen(
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopHeader(title = "Transaction Approved")

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    "Transaction Rejected",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The transaction wasn’t approved.\nPlease confirm the details and try again if needed.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryActionButton(
                    text = "Review Tx",
                ) {
                    // Handle event
                }
                Spacer(modifier = Modifier.height(16.dp))
                SecondaryActionButton(
                    text = "Close",
                ) {
                    // Handle event
                }
                Spacer(modifier = Modifier.height(64.dp))


            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun TransactionRejectedScreenPreview() {
    ExampleTheme {
        TransactionRejectedUIScreen()
    }
}
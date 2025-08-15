package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun TransactionApprovedScreen() {
    val nav = LocalNavigation.current
    TransactionApprovedUIScreen(
        biometricCode = "556754556754",
        passkeyCode = "556754",
        ethAmount = "0.38 ETH",
    )
}


@Composable
internal fun TransactionApprovedUIScreen(
    biometricCode: String,
    passkeyCode: String,
    ethAmount: String,
) {

    Scaffold(topBar = {
        TopHeader(title = "Transaction Approved")
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp), verticalArrangement = Arrangement.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Approve", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Biometric", color = Color.Gray)
            Text(biometricCode, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Passkey Auth", color = Color.Gray)
                    Text(passkeyCode, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text(ethAmount, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

    })

}


@Preview(showBackground = true)
@Composable
fun TransactionApprovedScreenPreview() {
    ExampleTheme {
        TransactionApprovedUIScreen(
            biometricCode = "",
            passkeyCode = "",
            ethAmount = "",
        )
    }
}
package com.cramium.example.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cramium.example.LocalNavigation
import com.cramium.example.ui.component.PinCodeInput
import com.cramium.example.ui.component.PrimaryActionButton
import com.cramium.example.ui.component.TopHeader
import com.cramium.example.ui.theme.ExampleTheme

@Composable
fun LocalPinScreen() {
    val nav = LocalNavigation.current
    LocalPinUIScreen { event ->
        when (event) {
            else -> Unit
        }
    }
}


@Composable
internal fun LocalPinUIScreen(
    event: (AppEvent) -> Unit,
) {
    Scaffold(topBar = {
        TopHeader(title = "Setup Pin")
    }, content = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            PinCodeInput(modifier = Modifier.fillMaxWidth()) {
                // TODO: Do it later
            }
            Spacer(modifier = Modifier.weight(1f))

            PrimaryActionButton(
                text = "Confirm",
            ) {
                // TODO: Do it later
            }
            Spacer(modifier = Modifier.height(64.dp))

        }
    })

}


@Preview(showBackground = true)
@Composable
fun LocalPinScreenPreview() {
    ExampleTheme {
        LocalPinUIScreen {}
    }
}
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
fun SecuritySettingScreen() {
    val nav = LocalNavigation.current
    SecuritySettingUIScreen { event ->
        when (event) {
            is AppEvent.LocalPin -> {
                nav.navigate(AppDestinations.LOCAL_PIN)
            }

            else -> Unit
        }
    }
}


@Composable
internal fun SecuritySettingUIScreen(
    event: (AppEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopHeader(title = "MPC Groups")

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            val menuItems = remember {
                listOf(
                    "Accessible only when connected" to AppEvent.Nothing,
                    "Setup Local Pin" to AppEvent.LocalPin,
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
fun SecuritySettingScreenPreview() {
    ExampleTheme {
        SecuritySettingUIScreen {}
    }
}
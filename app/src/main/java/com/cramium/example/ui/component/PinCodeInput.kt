package com.cramium.example.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinCodeInput(
    modifier: Modifier = Modifier,
    pinLength: Int = 4,
    onPinEntered: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val focusRequesters = List(pinLength) { FocusRequester() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0 until pinLength) {
            OutlinedTextField(
                value = pin.getOrNull(i)?.toString() ?: "",
                onValueChange = { value ->
                    if (value.length <= 1 && value.all { it.isDigit() }) {
                        val newPin = StringBuilder(pin)
                        if (pin.length > i) {
                            newPin.setCharAt(i, value.firstOrNull() ?: ' ')
                        } else {
                            newPin.append(value)
                        }
                        pin = newPin.toString().take(pinLength)

                        if (value.isNotEmpty() && i < pinLength - 1) {
                            focusRequesters[i + 1].requestFocus()
                        }

                        if (pin.length == pinLength && !pin.contains(' ')) {
                            onPinEntered(pin)
                        }
                    }
                },
                modifier = Modifier
                    .width(56.dp)
                    .focusRequester(focusRequesters[i])
                    .focusProperties {
                        if (i < pinLength - 1) next = focusRequesters[i + 1]
                        if (i > 0) previous = focusRequesters[i - 1]
                    },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }

    // Auto-focus first field
    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}

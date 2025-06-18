package com.cramium.example.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SecondaryActionButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentColor: Color = Color.DarkGray,
    onClick: () -> Unit,

    ) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text)
    }


}

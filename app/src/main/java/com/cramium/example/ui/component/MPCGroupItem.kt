package com.cramium.example.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cramium.example.R
import com.cramium.example.ui.theme.ExampleTheme
import com.cramium.example.ui.theme.PrimaryGreen
import com.cramium.sdk.model.mpc.MpcGroup

@Composable
fun MPCGroupItem(
    modifier: Modifier = Modifier, group: MpcGroup, onSetting: () -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.Center, // Center the Image within the Box
                modifier = modifier
                    .size(48.dp) // The size of the circular background
                    .clip(CircleShape) // Clip the Box to a circle shape
                    .background(Color(0x0F87E946)) // Set the background color to black
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_mpc_group),
                    contentDescription = "Wallet Icon",
                    modifier = modifier.size(32.dp), // Adjust size of the icon itself if needed, to fit nicely within the circle
                    colorFilter = ColorFilter.tint(PrimaryGreen)
                )
            }
            Spacer(modifier = modifier.width(12.dp))

            // Wallet Name
            Column(modifier = modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = group.name,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    InfoTag(
                        text = "${group.threshold + 1L} of ${group.numParties}"
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_cloud),
                        contentDescription = "Cramium Logo",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_smartphone),
                        contentDescription = "Cramium Logo",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                    if (group.numParties == 3L) Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_hardware),
                        contentDescription = "Cramium Logo",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
            }


            // Settings Icon
            IconButton(onClick = onSetting) {
                Icon(
                    imageVector = Icons.Default.Settings, contentDescription = "Settings"
                )
            }
        }

        // Divider
        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
    }


}

@Composable
fun InfoTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    cornerRadiusPercent: Int = 50 // For a fully rounded "pill" shape
) {
    Box(
        contentAlignment = Alignment.Center, // Center the text inside the Box
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadiusPercent)) // Creates the pill shape
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp) // Adjust padding for desired size
    ) {
        Text(
            text = text, color = textColor, fontSize = 13.sp, // Adjust font size
            fontWeight = FontWeight.Medium // Adjust font weight
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MPCScreenPreview() {
    ExampleTheme {
        MPCGroupItem(group = MpcGroup(
            id = "mpc_group_001",
            numParties = 3L,
            threshold = 2L,
            protocol = "gg20",
            name = "Personal Wallet Group",
            shard = ByteArray(32) { it.toByte() },
            serverShard = ByteArray(32) { (it + 5).toByte() },
            type = 1L // Could represent "savings", "express", etc.
        ), onSetting = {

        })
    }
}
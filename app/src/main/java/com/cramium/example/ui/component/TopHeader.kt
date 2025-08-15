package com.cramium.example.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.cramium.example.LocalNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader(
    modifier: Modifier = Modifier,
    title: String,
    enableBack: Boolean = true,
    onBack: (() -> Unit)? = null
) {
    val nav = LocalNavigation.current
    TopAppBar(modifier = modifier, title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }, navigationIcon = {
        if (enableBack) {
            IconButton(onClick = {
                if (onBack == null) {
                    nav.popBackStack()
                } else onBack()
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    }, colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White,
        titleContentColor = Color.Black,
        navigationIconContentColor = Color.Black,
        actionIconContentColor = Color.Black
    )
    )

//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(Color(0xFF00E0FF), Color(0xFFB9FF66))
//                ),
//                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
//            )
//            .padding(top = 40.dp, bottom = 24.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Box(modifier = Modifier.fillMaxWidth()) {
//            val nav = LocalNavigation.current
//
//            if (enableBack) {
//                IconButton(
//                    onClick = {
//                        if (onBack == null) {
//                            nav.popBackStack()
//                        } else onBack()
//                    },
//                    modifier = Modifier
//                        .align(Alignment.TopStart)
//                        .padding(start = 16.dp)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "Back",
//                        tint = Color.White
//                    )
//                }
//            }
//
//            Column(
//                modifier = Modifier.align(Alignment.Center),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = title,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }
}
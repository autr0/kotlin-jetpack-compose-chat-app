package com.devautro.firebasechatapp.chatScreen.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DateHeader(
    date: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = date,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = 4.dp, bottom = 4.dp),
        textAlign = TextAlign.Center,
        color = Color.White
    )
}
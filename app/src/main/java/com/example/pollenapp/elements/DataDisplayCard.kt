package com.example.pollenapp.elements

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pollenapp.RetroFitInstance

@Composable
fun DataDisplayCard() {
    var responseText by remember { mutableStateOf("Loading...") }
/*
    LaunchedEffect(Unit) {
        try {
            val response = RetroFitInstance().apiInterface.getHourlyPollen(51.619450, -3.945775)
            if (response.isSuccessful) {
                responseText = response.body().toString()
            } else {
                responseText = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            responseText = "Exception: ${e.message}"
        }
    }

    Card(modifier = Modifier.padding(16.dp)) {
        Text(
            text = responseText,
            modifier = Modifier.padding(16.dp)
        )
    } */
}
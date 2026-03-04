package com.example.pollenapp.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun Header(
    location: String,
    aqi: Int
) {



    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = 28.dp,
                    bottomEnd = 28.dp
                )
            )
            .background(gradient)
            .padding(20.dp)
    ) {

        Column {
            Text(
                text = location,
                color = MaterialTheme.colorScheme.surfaceVariant,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Air Quality",
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = aqi.toString(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            RatingBadge(aqi)
        }
    }
}

@Composable
fun RatingBadge(aqi: Int) {
    val text = when {
        aqi < 21 -> "Good"
        aqi < 41 -> "Fair"
        aqi < 61 -> "Moderate"
        aqi < 81 -> "Poor"
        else -> "Very Poor"
    }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    _root_ide_package_.com.example.pollenapp.AppTheme(dynamicColor = false) {
        Header("Swansea, UK", 55)
    }
}
package com.example.pollenapp.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Forecast(
    val dayStr: String,
    val month: String,
    val dayInt: Int,
    val score: Float,
    val rating: String,
    val icon: ImageVector
)

@Composable
fun ForecastCard(
    forecasts: List<Forecast>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Forecast",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (forecasts.isNotEmpty()) {
                forecasts.forEach { forecast ->
                    DayForecastCard(item = forecast)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                Text(
                    text = "Error: Unable to fetch forecast.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DayForecastCard(
    item: Forecast,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.dayStr,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "${item.score}/10",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.month + " ${item.dayInt}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Light
                        )

                        Text(
                            text = item.rating,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForecastPreview() {

    val days = listOf(
        Forecast(
            "Fri",
            "June",
            18,
            6.8f,
            "High",
            Icons.Outlined.Warning
        ),
        Forecast(
            "Sat",
            "June",
            19,
            6.6f,
            "High",
            Icons.Outlined.Warning
        ),
        Forecast(
            "Sun",
            "June",
            20,
            7.8f,
            "High",
            Icons.Outlined.Warning
        ),
        Forecast(
            "Mon",
            "June",
            21,
            5.6f,
            "Medium",
            Icons.Outlined.Warning
        ),
        Forecast(
            "Tue",
            "June",
            22,
            2.0f,
            "Low",
            Icons.Outlined.Warning
        ),
        Forecast(
            "Wed",
            "June",
            23,
            3.4f,
            "Low",
            Icons.Outlined.Warning
        )
    )

    _root_ide_package_.com.example.pollenapp.AppTheme(dynamicColor = false) {
        ForecastCard(
            forecasts = days,
            modifier = Modifier.padding(16.dp)
        )
    }
}
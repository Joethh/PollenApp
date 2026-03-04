package com.example.pollenapp.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun UserSensitivityInputCard(
    modifier: Modifier = Modifier,
    onNavigateToFeedback: (Int) -> Unit = {}
) {
    var sliderPosition by remember { mutableFloatStateOf(5f) }

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
                text = "How are you feeling?",
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = "Rate your current sensitivity level",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${sliderPosition.roundToInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..10f,
                    steps = 9,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Low", style = MaterialTheme.typography.labelSmall)
                Text("High", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToFeedback(sliderPosition.roundToInt()) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Submit Rating")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserSensitivityInputCardPreview() {
    _root_ide_package_.com.example.pollenapp.AppTheme(dynamicColor = false) {
        UserSensitivityInputCard(
            modifier = Modifier.padding(16.dp)
        )
    }
}

package com.example.pollenapp.elements

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class AllergenItem(
    val name: String,
    val score: Float,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun AllergenBreakdownCard(
    allergens: List<AllergenItem>,
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
                text = "Allergen Breakdown",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (allergens.isNotEmpty()) {
                allergens.forEach { allergen ->
                    AllergenRow(item = allergen)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            } else {
                Text(
                    text = "Error: Unable to fetch current allergen levels.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AllergenRow(item: AllergenItem) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(item.color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.name,
                    tint = item.color,
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
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    Text(
                        text = "${item.score}/10",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { item.score / 10f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = item.color,
                    trackColor = Color.LightGray.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllergenPreview() {

    val allergens = listOf(
        AllergenItem(
            "Grass",
            8.2f,
            Color(0xFF4CAF50),
            Icons.Outlined.Warning
        ),
        AllergenItem(
            "Tree",
            6.5f,
            Color(0xFF2E7D32),
            Icons.Outlined.Warning
        ),
        AllergenItem(
            "Weed",
            4.3f,
            Color(0xFF8BC34A),
            Icons.Outlined.Warning
        ),
        AllergenItem(
            "Ragweed",
            3.1f,
            Color(0xFFFBC02D),
            Icons.Outlined.Warning
        )
    )

    _root_ide_package_.com.example.pollenapp.AppTheme(dynamicColor = false) {
        AllergenBreakdownCard(
            allergens = allergens,
            modifier = Modifier.padding(16.dp)
        )
    }
}
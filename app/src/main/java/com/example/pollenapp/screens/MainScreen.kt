package com.example.pollenapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pollenapp.DiscomfortResponse
import com.example.pollenapp.elements.AllergenBreakdownCard
import com.example.pollenapp.elements.AllergenItem
import com.example.pollenapp.elements.DataDisplayCard
import com.example.pollenapp.elements.DiscomfortTestCard
import com.example.pollenapp.elements.Forecast
import com.example.pollenapp.elements.ForecastCard
import com.example.pollenapp.elements.Header
import com.example.pollenapp.elements.SensitivityAlertCard
import com.example.pollenapp.elements.UserSensitivityInputCard

@Composable
fun MainScreen(
    forecasts: List<Forecast>,
    allergens: List<AllergenItem>,
    location: String,
    aqi: Int,
    discomfort: DiscomfortResponse?,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        Box {
            Header(location, aqi)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = padding
            ) {
                item { Spacer(Modifier.height(220.dp)) }

                item { 
                    DiscomfortTestCard(
                        discomfort = discomfort,
                        modifier = modifier
                    )
                }

                item {
                    SensitivityAlertCard(
                        rating = "High",
                        description = "Sensitivity levels are high today. Take precautions if you're spending time outdoors.",
                        modifier = modifier
                    )
                }

                item { AllergenBreakdownCard(allergens, modifier) }

                item {
                    UserSensitivityInputCard(
                        modifier = modifier,
                        onNavigateToFeedback = { /* Handle navigation */ }
                    ) 
                }

                item { ForecastCard(forecasts, modifier) }

                item { DataDisplayCard() }
            }
        }
    }
}

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
import com.example.pollenapp.elements.AllergenBreakdownCard
import com.example.pollenapp.elements.AllergenItem
import com.example.pollenapp.elements.DataDisplayCard
import com.example.pollenapp.elements.Forecast
import com.example.pollenapp.elements.ForecastCard
import com.example.pollenapp.elements.Header
import com.example.pollenapp.elements.SensitivityAlert
import com.example.pollenapp.elements.SensitivityAlertCard
import com.example.pollenapp.elements.UserSensitivityInputCard

@Composable
fun MainScreen(
    forecasts: List<Forecast>,
    allergens: List<AllergenItem>,
    location: String,
    aqi: Int,
    discomfort: SensitivityAlert?,
    onRatingSubmit: (Int) -> Unit,
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

                if (discomfort != null) {
                    item {
                        SensitivityAlertCard(
                            discomfort,
                            modifier = modifier
                        )
                    }
                }

                item { AllergenBreakdownCard(allergens, modifier) }

                item {
                    UserSensitivityInputCard(
                        modifier = modifier,
                        onRatingSubmitted = onRatingSubmit
                    ) 
                }

                item { ForecastCard(forecasts, modifier) }

                item { DataDisplayCard() }
            }
        }
    }
}

package com.example.pollenapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pollenapp.elements.AllergenItem
import com.example.pollenapp.elements.Forecast
import com.example.pollenapp.elements.SensitivityAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {  // Change 1

    private val pollenRepository = PollenRepository(application)

    private val _allergens = MutableStateFlow<List<AllergenItem>>(emptyList())
    val allergens: StateFlow<List<AllergenItem>> = _allergens.asStateFlow()
    
    private val _forecast = MutableStateFlow<List<Forecast>>(emptyList())
    val forecast: StateFlow<List<Forecast>> = _forecast.asStateFlow()

    private val _aqi = MutableStateFlow<Int>(0)
    val aqi: StateFlow<Int> = _aqi.asStateFlow()

    private val _discomfortScore = MutableStateFlow<SensitivityAlert?>(null)
    val discomfortScore: StateFlow<SensitivityAlert?> = _discomfortScore.asStateFlow()

    fun fetchDataForLocation(lat: Double, lon: Double) {
        getCurrentPollenLevels(lat, lon)
        getFourDayPollenForecast(lat, lon)
        getCurrentAqi(lat, lon)
    }

    fun submitUserSensitivity(rating: Int) {
        viewModelScope.launch {
            val currentAllergens = _allergens.value
            if (currentAllergens.isNotEmpty()) {
                val prediction = pollenRepository.updateAndGetDiscomfortPrediction(currentAllergens, rating.toFloat())
                _discomfortScore.value = prediction
            }
        }
    }

    private fun getCurrentPollenLevels(lat: Double, lon: Double) {
        viewModelScope.launch {
            val pollenData = pollenRepository.getCurrentPollenLevels(lat, lon)
            _allergens.value = pollenData
            
            if (pollenData.isNotEmpty() && _discomfortScore.value == null) {
                val prediction = pollenRepository.getDiscomfortPrediction(pollenData)
                _discomfortScore.value = prediction
            }
        }
    }

    private fun getFourDayPollenForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            val forecastData = pollenRepository.getFourDayPollenForecast(lat, lon)
            _forecast.value = forecastData
        }
    }

    private fun getCurrentAqi(lat: Double, lon: Double) {
        viewModelScope.launch {
            val aqiData = pollenRepository.getCurrentAqi(lat, lon)
            _aqi.value = aqiData
        }
    }
}

package com.example.pollenapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pollenapp.screens.MainScreen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val locationManager = LocationManager(this)

        setContent {
            val allergens by viewModel.allergens.collectAsState()
            val forecast by viewModel.forecast.collectAsState()
            val aqi by viewModel.aqi.collectAsState()
            val discomfort by viewModel.discomfortScore.collectAsState()
            
            var locationName by remember { mutableStateOf("Loading...") }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions.values.all { it }
                if (granted) {
                    locationName = "Refreshing..."
                }
            }

            LaunchedEffect(Unit) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            LaunchedEffect(locationName) {
                if (locationName == "Loading..." || locationName == "Refreshing...") {
                    val latLon = locationManager.getLatLon()
                    if (latLon != null) {
                        locationName = locationManager.getLocationString(latLon)
                        viewModel.fetchDataForLocation(latLon[0], latLon[1])
                    } else {
                        locationName = "Permission Denied / Unknown"
                    }
                }
            }

            AppTheme(dynamicColor = false) {
                MainScreen(
                    forecasts = forecast,
                    allergens = allergens,
                    location = locationName,
                    aqi = aqi,
                    discomfort = discomfort,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

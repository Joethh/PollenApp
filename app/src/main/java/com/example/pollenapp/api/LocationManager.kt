package com.example.pollenapp.api

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.example.pollenapp.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume

class LocationManager(private val context: Context) {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLatLon(): List<Double>? = suspendCancellableCoroutine { cont ->
        val cts = CancellationTokenSource()
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(listOf(location.latitude, location.longitude))
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { cont.resume(null) }

        cont.invokeOnCancellation { cts.cancel() }
    }

    suspend fun getLocationString(latLon: List<Double>): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)

                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLon[0], latLon[1], 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    "${address.subAdminArea ?: address.locality ?: context.getString(R.string.unknown)}, ${address.countryCode}"
                } else context.getString(R.string.unknown)
            } catch (e: IOException) {
                context.getString(R.string.error)
            }
        }
    }
}
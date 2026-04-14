package com.example.pollenapp.data

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    val hourly: HourlyData,
    val current: CurrentData?
)

data class CurrentData(
    val time: String,
    val interval: Int,
    @SerializedName("european_aqi") val europeanAqi: Int?
)

data class HourlyData(
    val time: List<String>,
    @SerializedName("alder_pollen") val alderPollen: List<Float?>,
    @SerializedName("birch_pollen") val birchPollen: List<Float?>,
    @SerializedName("grass_pollen") val grassPollen: List<Float?>,
    @SerializedName("mugwort_pollen") val mugwortPollen: List<Float?>,
    @SerializedName("olive_pollen") val olivePollen: List<Float?>,
    @SerializedName("ragweed_pollen") val ragweedPollen: List<Float?>,
    @SerializedName("pm2_5") val pm2_5: List<Float?>
)

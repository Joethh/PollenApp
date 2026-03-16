package com.example.pollenapp

data class PollenLevelRequest(
    val name: String,
    val value: Float
)

data class DiscomfortResponse(
    val prediction: Float,
    val level: String
)

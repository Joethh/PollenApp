package com.example.pollenapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroFitInstance {
    private val openMeteoRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openMeteoApi: ApiInterface by lazy {
        openMeteoRetrofit.create(ApiInterface::class.java)
    }

    private val customRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val customApi: ApiInterface by lazy {
        customRetrofit.create(ApiInterface::class.java)
    }
}

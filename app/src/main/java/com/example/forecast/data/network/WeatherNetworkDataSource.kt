package com.example.forecast.data.network

import androidx.lifecycle.LiveData
import com.example.forecast.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>

    suspend fun fetchCurrentData(location: String)
}
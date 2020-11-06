package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.db.entity.WeatherLocation
import com.example.forecast.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
    suspend fun getWeatherLocation(): LiveData<WeatherLocation>
}
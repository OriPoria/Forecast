package com.example.forecast.data.network.response

import com.example.forecast.data.db.entity.CurrentWeatherEntry
import com.example.forecast.data.db.entity.Request
import com.example.forecast.data.db.entity.WeatherLocation
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation,
    val request: Request
)
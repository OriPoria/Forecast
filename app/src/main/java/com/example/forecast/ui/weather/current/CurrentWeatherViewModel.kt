package com.example.forecast.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.internal.UnitSystem
import com.example.forecast.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    private val unitSystem = UnitSystem.METRIC//get from settings later
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC


    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }
    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}
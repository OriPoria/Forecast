package com.example.forecast.data.db.unitlocalized

interface UnitSpecificCurrentWeatherEntry {
    val temperature: Int
    val weatherIcons: List<String>
    val feelslike: Int

}
package com.example.forecast.data.db.unitlocalized

import androidx.room.ColumnInfo

data class MetricCurrentWeatherEntry(
    @ColumnInfo(name = "temperature")
    override val temperature: Int,
    @ColumnInfo(name = "weatherIcons")
    override val weatherIcons: List<String>,
    override val feelslike: Int)
    : UnitSpecificCurrentWeatherEntry

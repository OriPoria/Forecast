package com.example.forecast.data.db.entity

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): List<String>? = Gson().fromJson(value, Array<String>::class.java).asList()
}
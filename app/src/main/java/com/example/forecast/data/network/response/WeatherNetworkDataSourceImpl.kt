package com.example.forecast.data.network.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.forecast.data.network.ApixuWeatherApiService

class WeatherNetworkDataSourceImpl(
    private val apixuWeatherApiService : ApixuWeatherApiService
) : WeatherNetworkDataSource {
    private val _downloadedCurrentWeather =  MutableLiveData<CurrentWeatherResponse>()

    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeather

    override suspend fun fetchCurrentWeather(location: String) {
        try {
            val fetchedCurrentWeather = apixuWeatherApiService
                .getCurrentWeather(location)
                .await()
            _downloadedCurrentWeather.postValue(fetchedCurrentWeather)
        } catch (e : Exception) {

        }
    }
}
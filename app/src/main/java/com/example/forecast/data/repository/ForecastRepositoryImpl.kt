package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.db.CurrentWeatherDao
import com.example.forecast.data.db.WeatherLocationDao
import com.example.forecast.data.db.entity.WeatherLocation
import com.example.forecast.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry
import com.example.forecast.data.network.WeatherNetworkDataSource
import com.example.forecast.data.network.response.CurrentWeatherResponse
import com.example.forecast.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
): ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever {newCurrnetWeather ->
            persistFetchedCurrentWeather(newCurrnetWeather)

        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getWeatherMetric()

        }
    }
    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }
    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocation().value

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            return
        }
        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
            fetchCurrentWeather()
        }

    }
    private suspend fun fetchCurrentWeather() {
        //going to update the live data downloadedCurrentWeather and observe it
        //and immediately, by persistFetchedCurrentWeather, it updates the DB
        weatherNetworkDataSource.fetchCurrentData(
            locationProvider.getPreferredLocationString()
        )
    }


    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}
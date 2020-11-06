package com.example.forecast

import android.app.Application
import android.content.Context
import com.example.forecast.data.db.ForecastDatabase
import com.example.forecast.data.network.*
import com.example.forecast.data.provider.LocationProvider
import com.example.forecast.data.provider.LocationProviderImpl
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.data.repository.ForecastRepositoryImpl
import com.example.forecast.ui.weather.current.CurrentWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ForecastApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))
        AndroidThreeTen.init(this@ForecastApplication)
        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind< ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind <WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind <LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind <ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(), instance()) }


        //our viewModel factory doesn't have a factory so we don't pass a < > parameter
        //we bind from provider, doesn't need to be singleton because we can create
        //a new instance of our factory
        bind() from provider { CurrentWeatherViewModelFactory(instance()) }

    }

    override fun onCreate() {
        super.onCreate()
    }
}
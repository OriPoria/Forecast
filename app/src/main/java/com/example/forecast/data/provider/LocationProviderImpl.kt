package com.example.forecast.data.provider

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.forecast.data.db.entity.WeatherLocation
import com.example.forecast.internal.LocationPermissionNotGrantedException
import com.example.forecast.internal.asDeferred
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Deferred

const val USE_DEVICE_LOCATION= "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION = "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context)
    : PreferenceProvider(context), LocationProvider {

    private val appContext = context.applicationContext


    //return true if the device location changed or the custom location from the preferences changed
    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException) {
            false
        }

        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val customLocationName = getCustomLocationName()
        return customLocationName != lastWeatherLocation.name
    }

    private fun getCustomLocationName(): String? {
        return preferences.getString(CUSTOM_LOCATION, null)
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        //if we are not using device location we return false
        if (!isUsingDeviceLocation())
            return false
        //the await will end when our deferred is completed
        val deviceLocation = getLastDeviceLocation().await()
            ?: return false

        println("device location longitude: " + deviceLocation.longitude + " and latitude: " + deviceLocation.latitude )
        val comparisonThreshold = 0.03
        return Math.abs(deviceLocation.latitude - lastWeatherLocation.lat.toDouble()) > comparisonThreshold &&
                Math.abs(deviceLocation.longitude - lastWeatherLocation.lon.toDouble()) > comparisonThreshold
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocation(): Deferred<Location?> {
        return if (hasLocationPermission())
            //may not have last location
            fusedLocationProviderClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()

    }
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(appContext,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isUsingDeviceLocation(): Boolean {
        //preferences from the PreferenceProvider Upper class
        return preferences.getBoolean(USE_DEVICE_LOCATION, true)
    }

    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()) {
            try {
                val deviceLocation = getLastDeviceLocation().await()
                    ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            } catch (e: LocationPermissionNotGrantedException) {
                return "${getCustomLocationName()}"
            }
        } else {
            return "${getCustomLocationName()}"
        }
    }
}
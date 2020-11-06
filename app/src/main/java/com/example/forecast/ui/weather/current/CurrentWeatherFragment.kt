package com.example.forecast.ui.weather.current
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.forecast.R
import com.example.forecast.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()
    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CurrentWeatherViewModel::class.java)
        bindUI()
//        val apiService = ApixuWeatherApiService(ConnectivityInterceptorImpl(this.requireContext()))
//        val weatherNetworkDataSource = WeatherNetworkDataSourceImpl(apiService)
//
//        weatherNetworkDataSource.downloadedCurrentWeather.observe(viewLifecycleOwner, Observer {
//            textview.text = it.toString()
//        })
//
//        GlobalScope.launch(Dispatchers.Main) {
//            weatherNetworkDataSource.fetchCurrentData("london")
//
//        }
    }
    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()

        val currentLocation = viewModel.weatherLocation.await()

        currentLocation.observe(viewLifecycleOwner, Observer { location ->
            if (location == null) return@Observer
            updateLocation(location.name)
        })

        currentWeather.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            group_loading.visibility = View.GONE
            updateLocation("Los Angeles")
            updateDateToToday()
            updateTemerature(it.temperature, it.feelslike)

            Glide.with(this@CurrentWeatherFragment)
                .load(it.weatherIcons[0]).into(imageView_condition_icon)

        })
    }
    private fun updateLocation(location: String) {
        (activity as AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDateToToday() {
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }
    @SuppressLint("SetTextI18n")
    private fun updateTemerature(temperature:Int, feelsLike: Int) {
        textView_temperature.text = "$temperature" + "\u2103"
        textView_feels_like_temperature.text = "feels like " + "$feelsLike" + "\u2103"
    }
}
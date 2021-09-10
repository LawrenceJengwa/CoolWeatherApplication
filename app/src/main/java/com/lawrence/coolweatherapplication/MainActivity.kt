package com.lawrence.coolweatherapplication

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lawrence.coolweatherapplication.databinding.ActivityMainBinding
import com.lawrence.coolweatherapplication.model.WeatherModel
import com.lawrence.coolweatherapplication.utils.WeatherUtil.*
import com.lawrence.coolweatherapplication.viewModel.MainViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mCityName: String
    private var weatherList: ArrayList<WeatherModel> = ArrayList()
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE = 100
        const val DAY_INT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val temp: LiveData<String> = viewModel.getTemperature()
        val condition: LiveData<String> = viewModel.getCondition()
        val townName: LiveData<String> = viewModel.getCityName()
        setUpObservers(condition, temp, townName)

        weatherAdapter =
            WeatherAdapter(this, weatherList)
        binding.idRvWeather.adapter = weatherAdapter

        viewModel.getWeatherList().observe(this, {
            weatherAdapter.addWeatherList(it as java.util.ArrayList<WeatherModel>?)
        })

        checkPermissions()
        setCityName(mCityName)

        viewModel.getWeatherData(this, mCityName)
        loadUI()

        binding.idIVSearch.setOnClickListener {
            val city: String = binding.idEdtCity.text.toString()
            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
            } else {
                binding.idTVCityName.text = city
                viewModel.getWeatherData(this, mCityName)
            }
        }
    }

    private fun setUpObservers(
        condition: LiveData<String>,
        temp: LiveData<String>,
        cityName: LiveData<String>
    ) {
        condition.observe(this, {
            binding.idTVCondition.text = it
        })

        temp.observe(this, {
            binding.idTVTemperature.text = it
        })

        cityName.observe(this, {
            binding.idTVCityName.text = it
        })
    }

    private fun checkPermissions() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE
            )
        }
        val location: Location? =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        setLocation(location)
    }

    private fun setLocation(location: Location?) {
        mCityName = location?.let { viewModel.getCityName(baseContext, it.longitude, it.latitude) }
            .toString()
    }

    private fun setCityName(cityName: String) {
        if (cityName.isEmpty()) {
            binding.idTVCityName.text = getString(R.string.default_city)
        }
        binding.idTVCityName.text = cityName
    }

    private fun loadUI() {
        val urlSuffix = "http:"

        binding.apply {
            idPBLoading.visibility = View.GONE
            idRLHome.visibility = View.VISIBLE
        }
        val joinedUrl = "$urlSuffix${viewModel.getCondition().value}"
        loadImage(joinedUrl, binding.idIVIcon)
        if (viewModel.getIsDay().value == DAY_INT) {
            loadImage(dayImageUrl, binding.idIVBack)
        } else {
            loadImage(nightImageUrl, binding.idIVBack)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show()

                    }
                    Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }

}
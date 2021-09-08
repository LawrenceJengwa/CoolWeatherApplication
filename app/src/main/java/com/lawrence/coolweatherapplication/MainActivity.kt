package com.lawrence.coolweatherapplication

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.lawrence.coolweatherapplication.databinding.ActivityMainBinding
import com.lawrence.coolweatherapplication.model.WeatherRVModel
import com.lawrence.coolweatherapplication.utils.WeatherUtil.dayImageUrl
import com.lawrence.coolweatherapplication.utils.WeatherUtil.nightImageUrl
import com.lawrence.coolweatherapplication.viewModel.MainViewModel
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mCityName: String

    private var weatherList: ArrayList<WeatherRVModel> = ArrayList()
    private lateinit var weatherAdapter: WeatherRVAdapter
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        weatherAdapter = WeatherRVAdapter(this, weatherList)
        binding.idRvWeather.adapter = weatherAdapter
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
        mCityName = location?.let { getCityName(it.longitude, it.latitude) }.toString()
        getWeatherInfo(mCityName)

        binding.idIVSearch.setOnClickListener {
            val city: String = binding.idEdtCity.text.toString()
            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
            } else {
                binding.idTVCityName.text = city
                getWeatherInfo(city)
            }
        }
    }

    private fun getWeatherInfo(cityName: String) {
        val url: String =
            "http://api.weatherapi.com/v1/forecast.json?key=89c6e74682074bc3a8182444210809&q=" + cityName + "&days=1&aqi=no&alerts=no"

        binding.idTVCityName.text = mCityName
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            binding.idPBLoading.visibility = View.GONE
            binding.idRLHome.visibility = View.VISIBLE

            weatherList.clear()
            try {
                var baseUrl: String = "http:"
                var temperature: String = it.getJSONObject("current").getString("temp_c")
                var isDay: Int = it.getJSONObject("current").getInt("is_day")
                var condition: String = it.getJSONObject("current").getJSONObject("condition").getString("text")
                var conditionIcon: String = it.getJSONObject("current").getJSONObject("condition").getString("icon")
                var joined: String = "$baseUrl$conditionIcon"

                binding.idTVTemperature.text = temperature+"°C"
                binding.idTVCondition.text = condition
                Picasso.get().load(joined).into(binding.idIVIcon)
                if (isDay == 1){
                    Picasso.get().load(dayImageUrl).into(binding.idIVBack)
                }else {
                  Picasso.get().load(nightImageUrl).into(binding.idIVBack)
                }

                val forecast: JSONObject= it.getJSONObject("forecast")
                val forecastDay: JSONObject = forecast.getJSONArray("forecastday").getJSONObject(0)
                val hourArray: JSONArray = forecastDay.getJSONArray("hour")

                for (i in 0 until hourArray.length()){
                    val hourObject = hourArray.getJSONObject(i)
                    val time: String = hourObject.getString("time")
                    val temp: String = hourObject.getString("temp_c")
                    val image: String = hourObject.getJSONObject("condition").getString("icon")
                    val wind: String = hourObject.getString("wind_kph")

                    weatherList.add(WeatherRVModel(time, temp, image, wind))
                }
                weatherAdapter.notifyDataSetChanged()

            }catch (e: JSONException){
               e.printStackTrace()
            }

            }) {
            Toast.makeText(this@MainActivity, "Please enter valid city name", Toast.LENGTH_SHORT)
                .show()
        }
        requestQueue.add(request)
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

    private fun getCityName(longitude: Double, latitude: Double): String {
        var cityName = "Not found"
        val geocoder: Geocoder = Geocoder(baseContext, Locale.getDefault())
        try {
            var addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 10)
            for (address in addressList) {
                if (address != null) {
                    var city: String = address.locality
                    if (city != null && city.isNotEmpty()) {
                        cityName = city
                    } else {
                        Log.d("TAG", "CITY NOT FOUND")
                        Toast.makeText(this, "Entered city not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        } catch (e: IOException) {

        }
        return cityName

    }

}
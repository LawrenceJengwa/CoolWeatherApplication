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
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lawrence.coolweatherapplication.databinding.ActivityMainBinding
import com.lawrence.coolweatherapplication.model.WeatherModel
import com.lawrence.coolweatherapplication.utils.WeatherUtil.*
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

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        weatherAdapter =
            WeatherAdapter(this, weatherList)
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

    //volley network call, should be moved to network package
    private fun getWeatherInfo(cityName: String) {
        val url = "$BASE_URL$API_KEY$cityName$DAYS_SUFFIX"

        binding.idTVCityName.text = mCityName
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            binding.idPBLoading.visibility = View.GONE
            binding.idRLHome.visibility = View.VISIBLE

            weatherList.clear()
            try {
                val urlSuffix = "http:"
                val temperature: String = it.getJSONObject("current").getString("temp_c")
                val isDay: Int = it.getJSONObject("current").getInt("is_day")
                val condition: String = it.getJSONObject("current").getJSONObject("condition").getString("text")
                val conditionIcon: String = it.getJSONObject("current").getJSONObject("condition").getString("icon")
                val joinedUrl = "$urlSuffix$conditionIcon"

                binding.idTVTemperature.text = temperature+"°C"
                binding.idTVCondition.text = condition
                loadImage(joinedUrl, binding.idIVIcon)
                if (isDay == DAY_INT){
                    loadImage(dayImageUrl, binding.idIVBack)
                }else {
                    loadImage(nightImageUrl, binding.idIVBack)
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

                    weatherList.add(
                        WeatherModel(
                            time,
                            temp,
                            image,
                            wind
                        )
                    )
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
        val geocoder = Geocoder(baseContext, Locale.getDefault())
        try {
            var addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 10)
            for (address in addressList) {
                val city: String = address.locality
                if (city.isNotEmpty()) {
                    cityName = city
                } else {
                    Log.d("TAG", "CITY NOT FOUND")
                    Toast.makeText(this, "Entered city not found", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: IOException) {

        }
        return cityName

    }

}
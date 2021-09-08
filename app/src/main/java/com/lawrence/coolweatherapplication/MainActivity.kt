package com.lawrence.coolweatherapplication

import android.app.DownloadManager
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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import com.lawrence.coolweatherapplication.model.WeatherRVModel
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var homeRL: RelativeLayout
    private lateinit var loadingPB: ProgressBar
    private lateinit var cityNameTv: TextView
    private lateinit var temperatureTv: TextView
    private lateinit var conditionTv: TextView
    private lateinit var cityEdt: TextInputEditText
    private lateinit var iconIV: ImageView
    private lateinit var backIV: ImageView
    private lateinit var searchIV: ImageView
    private lateinit var weatherRv: RecyclerView

    private lateinit var mCityName: String

    private var weatherList: ArrayList<WeatherRVModel> = ArrayList()
    private lateinit var weatherAdapter: WeatherRVAdapter
    private lateinit var locationManager: LocationManager

    companion object {
        const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)

        homeRL = findViewById(R.id.idRLHome)
        loadingPB = findViewById(R.id.idPBLoading)
        cityNameTv = findViewById(R.id.idTVCityName)
        temperatureTv = findViewById(R.id.idTVTemperature)
        conditionTv = findViewById(R.id.idTVCondition)
        cityEdt = findViewById(R.id.idEdtCity)
        iconIV = findViewById(R.id.idIVIcon)
        backIV = findViewById(R.id.idIVBack)
        searchIV = findViewById(R.id.idIVSearch)
        weatherRv = findViewById(R.id.idRvWeather)

        weatherAdapter = WeatherRVAdapter(this, weatherList)
        weatherRv.adapter = weatherAdapter
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

        searchIV.setOnClickListener {
            val city: String = cityEdt.text.toString()
            if (city.isEmpty()) {
                Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
            } else {
                cityNameTv.text = city
                getWeatherInfo(city)
            }
        }
    }

    private fun getWeatherInfo(cityName: String) {
        val url: String =
            "http://api.weatherapi.com/v1/forecast.json?key=89c6e74682074bc3a8182444210809&q=" + cityName + "&days=1&aqi=no&alerts=no"

        cityNameTv.text = mCityName
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            loadingPB.visibility = View.GONE
            homeRL.visibility = View.VISIBLE

            weatherList.clear()
            try {
                var baseUrl: String = "http:"
                var temperature: String = it.getJSONObject("current").getString("temp_c")
                var isDay: Int = it.getJSONObject("current").getInt("is_day")
                var condition: String = it.getJSONObject("current").getJSONObject("condition").getString("text")
                var conditionIcon: String = it.getJSONObject("current").getJSONObject("condition").getString("icon")
                var joined: String = "$baseUrl$conditionIcon"

                temperatureTv.text = temperature+"°C"
                conditionTv.text = condition
                Picasso.get().load(joined).into(iconIV)
                if (isDay == 1){
                    val day: String = "https://images.unsplash.com/photo-1542709111240-e9df0dd813b4?ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8ZGF5fGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
                    Picasso.get().load(day).into(backIV)
                }else {
                    val night: String =
                        "https://images.unsplash.com/photo-1475274047050-1d0c0975c63e?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8bmlnaHQlMjBza3l8ZW58MHx8MHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
                    Picasso.get().load(night).into(backIV)
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
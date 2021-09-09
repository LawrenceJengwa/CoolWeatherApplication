package com.lawrence.coolweatherapplication.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lawrence.coolweatherapplication.MainActivity
import com.lawrence.coolweatherapplication.WeatherAdapter
import com.lawrence.coolweatherapplication.model.WeatherModel
import com.lawrence.coolweatherapplication.utils.WeatherUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel : ViewModel() {

    private val weatherList = MutableLiveData<MutableList<WeatherModel>>()
    private val isDay = MutableLiveData<Int>()
    private val conditionIcon = MutableLiveData<String>()
    private val condition = MutableLiveData<String>()
    private val mCityName = MutableLiveData<String>()
    private val temperature = MutableLiveData<String>()
    private lateinit var weatherAdapter: WeatherAdapter

    fun getWeatherList(): LiveData<MutableList<WeatherModel>> = weatherList
    fun getTemperature(): LiveData<String> = temperature
    fun getConditionIcon(): LiveData<String> = conditionIcon
    fun getCondition(): LiveData<String> = condition
    fun getCityName(): LiveData<String> = mCityName
    fun getIsDay(): LiveData<Int> = isDay

    init {
        weatherList.value = ArrayList()
    }

    fun getCityName(context: Context, longitude: Double, latitude: Double): String {
        var cityName = "Not found"
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 10)
            for (address in addressList) {
                val city: String = address.locality
                if (city.isNotEmpty()) {
                    cityName = city
                    mCityName.value = cityName
                    Log.d("TAG", "CITY is: $city")
                } else {
                    Log.d("TAG", "CITY NOT FOUND")
                }
            }

        } catch (e: IOException) {

        }
        return cityName
    }

    fun getWeatherInfo(context: Context, cityName: String) {
        val url = "${WeatherUtil.BASE_URL}${WeatherUtil.API_KEY}$cityName${WeatherUtil.DAYS_SUFFIX}"

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            weatherList.value?.clear()

            try {
                temperature.value = it.getJSONObject("current").getString("temp_c")
                isDay.value = it.getJSONObject("current").getInt("is_day")
                condition.value = it.getJSONObject("current").getJSONObject("condition").getString("text")
                conditionIcon.value = it.getJSONObject("current").getJSONObject("condition").getString("icon")

                val forecast: JSONObject = it.getJSONObject("forecast")
                val forecastDay: JSONObject = forecast.getJSONArray("forecastday").getJSONObject(0)
                val hourArray: JSONArray = forecastDay.getJSONArray("hour")

                for (i in 0 until hourArray.length()){
                    val hourObject = hourArray.getJSONObject(i)
                    val time: String = hourObject.getString("time")
                    val temp: String = hourObject.getString("temp_c")
                    val image: String = hourObject.getJSONObject("condition").getString("icon")
                    val wind: String = hourObject.getString("wind_kph")

                    weatherList.value?.add(
                        WeatherModel(
                            time,
                            temp,
                            image,
                            wind
                        )
                    )
                }

            }catch (e: JSONException){
                e.printStackTrace()
            }

        }) {
            Log.d("MainViewModel", "Please enter valid city name:")
        }
        requestQueue.add(request)
    }

}
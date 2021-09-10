package com.lawrence.coolweatherapplication.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.lawrence.coolweatherapplication.WeatherAdapter
import com.lawrence.coolweatherapplication.model.WeatherModel
import com.lawrence.coolweatherapplication.utils.NetworkUtil
import com.lawrence.coolweatherapplication.utils.WeatherUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun getWeatherData(context: Context, cityName: String){
        viewModelScope.launch(Dispatchers.IO) {
            val data = NetworkUtil(context).getWeatherData(cityName)
            withContext(Dispatchers.IO){
                getWeatherInfo(data)
            }
        }
    }

    private fun getWeatherInfo(request: JSONObject) {
            weatherList.value?.clear()
                temperature.postValue(request.getJSONObject("current").getString("temp_c"))
                isDay.postValue(request.getJSONObject("current").getInt("is_day"))
                condition.postValue(request.getJSONObject("current").getJSONObject("condition").getString("text"))
                conditionIcon.postValue(request.getJSONObject("current").getJSONObject("condition").getString("icon"))
                val forecast: JSONObject = request.getJSONObject("forecast")
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
    }

}
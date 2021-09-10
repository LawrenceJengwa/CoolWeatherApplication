package com.lawrence.coolweatherapplication.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.lawrence.coolweatherapplication.TestUtils.Companion.testCityName
import com.lawrence.coolweatherapplication.TestUtils.Companion.testLatitude
import com.lawrence.coolweatherapplication.TestUtils.Companion.testLongitude
import com.lawrence.coolweatherapplication.model.WeatherModel

import junit.framework.TestCase
import org.junit.Assert

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : TestCase() {

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var geocoder: Geocoder

    private lateinit var addressList: List<Address>
    private lateinit var weatherList: List<WeatherModel>

    @Before
    public override fun setUp() {
        viewModel = MainViewModel()
        geocoder = Geocoder(context, Locale.getDefault())
        weatherList = listOf(WeatherModel("23",
                "20","icon","20km/hr" ))
    }

    @Test
    fun `getCityName should return correct city name when correct location is given`() {
        addressList = geocoder.getFromLocation(testLatitude, testLongitude, 10)
        viewModel.getCityName(context, testLatitude, testLongitude)

        assertEquals(testCityName, addressList[0].locality)
    }

    @Test
    fun `getTemperature should return the temperature value that has been set`() {
        val temp = "20"
        viewModel.getWeatherData(context, "Pretoria")
       // Assert.assertNull(viewModel.getTemperature().value)
        //viewModel.temperature.value = temp

        Assert.assertEquals(temp, viewModel.getTemperature().value)
    }
}
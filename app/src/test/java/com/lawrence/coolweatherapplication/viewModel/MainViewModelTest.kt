package com.lawrence.coolweatherapplication.viewModel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.lawrence.coolweatherapplication.TestUtils.Companion.testCityName
import com.lawrence.coolweatherapplication.TestUtils.Companion.testLatitude
import com.lawrence.coolweatherapplication.TestUtils.Companion.testLongitude

import junit.framework.TestCase

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest : TestCase() {

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var geocoder: Geocoder

    private lateinit var addressList: List<Address>

    @Before
    public override fun setUp() {
        viewModel = MainViewModel()
        geocoder = Geocoder(context, Locale.getDefault())
    }

    @Test
    fun `getCityName should return correct city name when correct location is given`() {
        addressList = geocoder.getFromLocation(testLatitude, testLongitude, 10)
        viewModel.getCityName(context, testLatitude, testLongitude)

        assertEquals(testCityName, addressList[0].locality)
    }
}
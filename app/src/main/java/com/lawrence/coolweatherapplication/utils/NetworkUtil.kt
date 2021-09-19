package com.lawrence.coolweatherapplication.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Module
class NetworkUtil(private val context: Context) {

    @Provides
    suspend fun getWeatherData(cityName: String): JSONObject {
        val url = "${WeatherUtil.BASE_URL}${WeatherUtil.API_KEY}$cityName${WeatherUtil.DAYS_SUFFIX}"

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        return suspendCancellableCoroutine { continuation ->
            try {
                val success = Response.Listener<JSONObject> { response ->
                    if (continuation.isActive) {
                        continuation.resume(response)
                    }

                }
                val error = Response.ErrorListener { _ ->
                    if (continuation.isActive) {
                        continuation.resume(JSONObject())
                    }
                }

                val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null, success, error)
                requestQueue.add(jsonRequest)
            } catch (e: Exception) {
                e.printStackTrace()
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}
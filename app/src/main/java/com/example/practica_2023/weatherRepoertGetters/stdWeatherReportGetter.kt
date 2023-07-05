package com.example.practica_2023.weatherRepoertGetters

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

fun stdGetWeatherReport(url: String, f: (JSONObject) -> Unit, context: Context?){
    val queue = Volley.newRequestQueue(context)
    val weatherReportGetRequest = StringRequest(
        Request.Method.GET,
        url,
        {
                response->
            f(JSONObject(response))
            Log.d("RequestLog", "Weather request get success: $response")
        },
        { Log.d("RequestLog", "Weather request get error: $it")})
    queue.add(weatherReportGetRequest)
}
package com.example.practica_2023

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica_2023.fragments.MainFragment
import com.example.practica_2023.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.PlaceHolder, MainFragment.newInstance()).commit()
        getWeatherReport("London")
    }
    private fun getWeatherReport(place: String){
        val url = "https://api.openweathermap.org/data/2.5/" +
                "weather?lat=44.34&lon=10.99&" +
                "appid=${getString(R.string.api_key)}"
        val queue = Volley.newRequestQueue(this)
        val weatherReportGetRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response->
                val weatherReport = JSONObject(response)
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                Log.d("RequestLog", "Weather request get success: $fusedLocationClient")
                Log.d("RequestLog", "Weather request get success: $response")
            },
            {Log.d("RequestLog", "Weather request get error: $it")})
        queue.add(weatherReportGetRequest)
    }
}
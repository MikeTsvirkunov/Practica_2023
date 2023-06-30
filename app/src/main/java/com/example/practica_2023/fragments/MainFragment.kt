package com.example.practica_2023.fragments

//import android.app.DownloadManager.Request
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica_2023.R
//import com.example.practica_2023.databinding.ActivityMainBinding
//import com.example.practica_2023.Manifest
import com.example.practica_2023.databinding.FragmentMainBinding
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.util.Dictionary


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

//        getWeatherReport(getCurrentApiLink("London"))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionCheck()

        val x = resources.getStringArray(R.array.places)
        val arrayAdapter = ArrayAdapter(requireContext(),
            R.layout.city_check_layout,
            x)
        binding.PlaceChecker.adapter = arrayAdapter
        getWeatherReport(getCurrentApiLink(binding.PlaceChecker.selectedItem.toString()))
        binding.swiperefresh.setOnRefreshListener{
            getWeatherReport(getCurrentApiLink(binding.PlaceChecker.selectedItem.toString()))
            binding.swiperefresh.isRefreshing = false
        }
    }

    private fun permissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            Toast.makeText(activity, "Permission, $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun permissionCheck() {
        if (!isPermissionAvailable(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    private fun getCurrentApiLink(place: String): String{
        return "https://api.openweathermap.org/data/2.5/" +
                "weather?q=${place}&" +
                "&units=${getString(R.string.api_units)}" +
                "&appid=${getString(R.string.api_key)}"
    }
    private fun getWeatherReport(url: String){
        val queue = Volley.newRequestQueue(context)
        val weatherReportGetRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response->
                val weatherReport = JSONObject(response)
                updateMainInfo(weatherReport)
                Log.d("RequestLog", "Weather request get success: $response")
            },
            {Log.d("RequestLog", "Weather request get error: $it")})
        queue.add(weatherReportGetRequest)
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    private fun updateMainInfo(weatherInfo: JSONObject){
        binding.WindSpeedNumLabel.text = "${weatherInfo.getJSONObject("wind").getString("speed")} m/s"
        binding.WeatherConditionLabel.text = JSONObject(weatherInfo.getJSONArray("weather")[0].toString()).getString("main")
        binding.TempInfoLabel.text = "${weatherInfo.getJSONObject("main").getString("temp")} ºC"
        binding.HumidityInfo.text = "${weatherInfo.getJSONObject("main").getString("humidity")} %"
        Log.d("JSONLog", "Weather : ${weatherInfo.getJSONArray("weather")[0]}")
        binding.WeatherImg.setImageResource(resources.getIdentifier(JSONObject(weatherInfo.getJSONArray("weather")[0].toString()).getString("main").lowercase(), "drawable",
            context?.packageName ?: ""
        ))
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
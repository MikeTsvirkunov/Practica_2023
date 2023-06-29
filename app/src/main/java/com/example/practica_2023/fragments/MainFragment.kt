package com.example.practica_2023.fragments

//import android.app.DownloadManager.Request
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica_2023.R
import com.example.practica_2023.databinding.ActivityMainBinding
//import com.example.practica_2023.Manifest
import com.example.practica_2023.databinding.FragmentMainBinding
import com.google.android.gms.location.LocationServices
import org.json.JSONObject


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionCheck()
        getWeatherReport("London")
        binding.GetWeatherReportBtn.setOnClickListener{
            getWeatherReport("London")
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
    private fun getWeatherReport(place: String){
        val url = "https://api.openweathermap.org/data/2.5/" +
                "weather?lat=44.34&lon=10.99&" +
                "appid=${getString(R.string.api_key)}"
        val queue = Volley.newRequestQueue(context)
        val weatherReportGetRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response->
                val weatherReport = JSONObject(response)
                UpdateMainInfo(weatherReport)
                Log.d("RequestLog", "Weather request get success: $response")
            },
            {Log.d("RequestLog", "Weather request get error: $it")})
        queue.add(weatherReportGetRequest)
    }

    @SuppressLint("SetTextI18n")
    private fun UpdateMainInfo(weatherInfo: JSONObject){
        binding.WindSpeedNumLabel.text = "${weatherInfo.getJSONObject("wind").getString("speed")} m/s"
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
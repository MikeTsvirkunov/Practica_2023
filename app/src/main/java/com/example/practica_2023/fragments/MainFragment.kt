package com.example.practica_2023.fragments

//import android.app.DownloadManager.Request
//import com.example.practica_2023.databinding.ActivityMainBinding
//import com.example.practica_2023.Manifest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica_2023.R
import com.example.practica_2023.databinding.FragmentMainBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.json.JSONObject


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding

    // bar data
    private  lateinit var barEntriesArrayList: ArrayList<BarEntry>
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

        val x = resources.getStringArray(R.array.places)
        val arrayAdapter = ArrayAdapter(requireContext(),
            R.layout.city_check_layout,
            x)
        binding.PlaceChecker.adapter = arrayAdapter
        CreateBarChart()
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

    private fun CreateBarChart(){
        getBarEntries()
        val barDataSet = BarDataSet(barEntriesArrayList, "Geeks for Geeks")
        val barData = BarData(BarDataSet(barEntriesArrayList, "Geeks for Geeks"))
        binding.HourChart.data = BarData(barDataSet)
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS)
//        barDataSet.valueTextColor = Color.BLACK
        binding.HourChart.setDrawBarShadow(false)
        binding.HourChart.setDrawGridBackground(false)
        binding.HourChart.setDrawBorders(false)
        barDataSet.valueTextSize = 16f
        binding.HourChart.description.isEnabled = false
    }
    private fun getBarEntries() {
        barEntriesArrayList = ArrayList<BarEntry>()
        barEntriesArrayList.add(BarEntry(1f, 4f))
        barEntriesArrayList.add(BarEntry(2f, 6f))
        barEntriesArrayList.add(BarEntry(3f, 8f))
        barEntriesArrayList.add(BarEntry(4f, 2f))
        barEntriesArrayList.add(BarEntry(5f, 4f))
        barEntriesArrayList.add(BarEntry(6f, 1f))
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
package com.example.practica_2023.fragments

//import android.app.DownloadManager.Request
//import com.example.practica_2023.databinding.ActivityMainBinding
//import com.example.practica_2023.Manifest

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.practica_2023.R
import com.example.practica_2023.databinding.FragmentMainBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.json.JSONObject


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
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
        getWeatherReport(getCurrentWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::updateMainInfo)
        getWeatherReport(getTimedWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::getHourTempBarChart)
        getWeatherReport(getTimedWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::getHourWSBarChart)
        binding.swiperefresh.setOnRefreshListener{
            getWeatherReport(getCurrentWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::updateMainInfo)
            getWeatherReport(getTimedWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::getHourTempBarChart)
            getWeatherReport(getTimedWeatherApiLink(binding.PlaceChecker.selectedItem.toString()), ::getHourWSBarChart)
            binding.swiperefresh.isRefreshing = false
        }
    }

    private fun getCurrentWeatherApiLink(place: String): String{
        return "https://api.openweathermap.org/data/2.5/weather" +
                "?q=${place}&" +
                "&units=${getString(R.string.api_units)}" +
                "&appid=${getString(R.string.api_key)}"
    }
    private fun getTimedWeatherApiLink(place: String): String{
        return "http://api.openweathermap.org/data/2.5/forecast?" +
                "q=${place}" +
                "&units=${getString(R.string.api_units)}" +
                "&appid=${getString(R.string.api_key)}"
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
    private fun getWeatherReport(url: String, f: (JSONObject) -> Unit){
        val queue = Volley.newRequestQueue(context)
        val weatherReportGetRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response->
                f(JSONObject(response))
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
    private fun getHourWSBarChart(weatherInfo: JSONObject){
        setBarChartParams(binding.HourWSChart)
        setBarChartData(binding.HourWSChart, "Temperature", getHourWSWeatherInfo(weatherInfo))
    }

    private fun getHourTempBarChart(weatherInfo: JSONObject){
        setBarChartParams(binding.HourTempChart)
        setBarChartData(binding.HourTempChart, "Temperature", getHourTempWeatherInfo(weatherInfo))
    }
    private fun setBarChartParams(barChart: BarChart){
        barChart.setDrawValueAboveBar(false)
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)
        barChart.setDrawBorders(false)
        barChart.setBorderColor(2)
        barChart.setDrawMarkers(false)
        barChart.setMaxVisibleValueCount(0)
        barChart.axisLeft.setDrawGridLines(false);
        barChart.axisRight.setDrawGridLines(false);
        barChart.axisRight.setDrawZeroLine(false);
        barChart.axisLeft.setDrawZeroLine(false);
        barChart.xAxis.setDrawGridLines(false);
        barChart.axisRight.setDrawLabels(false);
        barChart.axisLeft.setDrawLabels(false);
        barChart.xAxis.setDrawLabels(false)
        barChart.xAxis.setDrawAxisLine(false)
        barChart.axisLeft.setDrawAxisLine(false);
        barChart.axisRight.setDrawAxisLine(false);
        barChart.description.isEnabled = false
        barChart.description.typeface = ResourcesCompat.getFont(requireContext(), R.font.fredoka_one);
        barChart.invalidate()
    }
    private fun setBarChartData(barChart: BarChart, label: String, Data: ArrayList<BarEntry>){
        barChart.data = BarData(BarDataSet(Data, label))
        barChart.invalidate()
    }
    private fun getHourTempWeatherInfo(weatherInfo: JSONObject): ArrayList<BarEntry>{
        Log.d("HOURS", weatherInfo.toString())
        val data = weatherInfo.getJSONArray("list")
        val barEntriesArrayList = ArrayList<BarEntry>()
        val times = ArrayList<String>()
        Log.d("HOURS", data.length().toString())
        for (i in 0 until data.length() / 3){
            Log.d("HOURS", "res:${JSONObject(data[i].toString()).getString("dt")}")
            Log.d("HOURS", "${JSONObject(JSONObject(data[3*i].toString()).getJSONObject("main").toString()).getDouble("temp").toFloat()}")
            times.add(JSONObject(data[3*i].toString()).getString("dt"))
            barEntriesArrayList.add(BarEntry(i.toFloat(), JSONObject(JSONObject(data[i].toString()).getJSONObject("main").toString()).getDouble("temp").toFloat()))
        }
        return barEntriesArrayList
    }

    private fun getHourWSWeatherInfo(weatherInfo: JSONObject): ArrayList<BarEntry>{
        Log.d("HOURS", weatherInfo.toString())
        val data = weatherInfo.getJSONArray("list")
        val barEntriesArrayList = ArrayList<BarEntry>()
        val times = ArrayList<String>()
        Log.d("HOURS", data.length().toString())
        for (i in 0 until data.length() / 3){
            Log.d("HOURS", "res:${JSONObject(data[i].toString()).getString("dt")}")
            Log.d("HOURS", "${JSONObject(JSONObject(data[3*i].toString()).getJSONObject("main").toString()).getDouble("temp").toFloat()}")
            times.add(JSONObject(data[3*i].toString()).getString("dt"))
            barEntriesArrayList.add(BarEntry(i.toFloat(), JSONObject(JSONObject(data[i].toString()).getJSONObject("main").toString()).getDouble("humidity").toFloat()))
        }
        return barEntriesArrayList
    }


    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
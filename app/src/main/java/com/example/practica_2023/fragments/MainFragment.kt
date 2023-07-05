package com.example.practica_2023.fragments

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
import com.example.practica_2023.R
import com.example.practica_2023.databinding.FragmentMainBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import org.json.JSONObject
import com.example.practica_2023.barChartParamsSetters.setStanderBarChartParams
import com.example.practica_2023.getLink
import com.example.practica_2023.weatherRepoertGetters.stdGetWeatherReport
import org.json.JSONArray
import java.net.Proxy.Type


class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private lateinit var linkParams: MutableMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.PlaceChecker.adapter = ArrayAdapter(
            requireContext(),
            R.layout.city_check_layout,
            resources.getStringArray(R.array.places)
        )
        setStanderBarChartParams(binding.HourTempChart)
        setStanderBarChartParams(binding.HourWSChart)
        linkParams = mutableMapOf(
            "q" to binding.PlaceChecker.selectedItem.toString(),
            "units" to getString(R.string.api_units),
            "appid" to getString(R.string.api_key)
        )
        updateInfo()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionCheck()
        binding.swiperefresh.setOnRefreshListener{
            linkParams["q"] = binding.PlaceChecker.selectedItem.toString()
            updateInfo()
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

    // Updates. Just for less code.
    private fun updateInfo(){
        stdGetWeatherReport(
            url = getLink(getString(R.string.current_link), linkParams.toMap()),
            context = context,
            f = ::updateMainInfo
        )
        stdGetWeatherReport(
            url = getLink(getString(R.string.timed_link), linkParams.toMap()),
            context = context,
            f = {
                val data = it.getJSONArray("list")
                val timesWeatherReportList = mutableListOf<String>()
                val tempWeatherReportList = mutableListOf<Float>()
                val wsWeatherReportList = mutableListOf<Float>()

                val valueTakers = listOf<(JSONObject) -> Unit>(
                    {obj -> timesWeatherReportList.add(obj.getString("dt")) },
                    {obj ->  tempWeatherReportList.add(
                        JSONObject(obj.getJSONObject("main").toString())
                            .getDouble("temp")
                            .toFloat()) },
                    {obj ->  wsWeatherReportList.add(
                        JSONObject(obj.getJSONObject("main").toString())
                            .getDouble("humidity")
                            .toFloat())},
                )
                getWeatherObjectInfo(data, (0..data.length() step 3).iterator(), valueTakers)
                val tt = arrayListOf<BarEntry>()
                val tws = arrayListOf<BarEntry>()
                (0 until tempWeatherReportList.size).forEach {
                    pair ->
                    tt.add(BarEntry(pair.toFloat(), tempWeatherReportList[pair]))
                    tws.add(BarEntry(pair.toFloat(), wsWeatherReportList[pair]))
                }
                setBarChartData(binding.HourTempChart, "Temperature", tt)
                setBarChartData(binding.HourWSChart, "WindSpeed", tws)
            }
        )
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

    private fun setBarChartData(barChart: BarChart, label: String, Data: ArrayList<BarEntry>){
        barChart.data = BarData(BarDataSet(Data, label))
        barChart.invalidate()
    }

    private fun getWeatherObjectInfo(data: JSONArray, dataIterator: IntIterator, valueTakers: List<(JSONObject) -> Unit>){
        dataIterator.forEach { index -> valueTakers.forEach{ taker -> taker(JSONObject(data[index].toString())) } }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}


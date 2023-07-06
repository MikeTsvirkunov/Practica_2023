package com.example.practica_2023.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.practica_2023.R
import com.example.practica_2023.barChartParamsSetters.setBarChartData
import com.example.practica_2023.barChartParamsSetters.setStanderBarChartParams
import com.example.practica_2023.databinding.FragmentMainBinding
import com.example.practica_2023.getLink
import com.example.practica_2023.jsonObjectsGetters.getWeatherObjectInfo
import com.example.practica_2023.weatherRepoertGetters.stdGetWeatherReport
import com.github.mikephil.charting.data.BarEntry
import org.json.JSONObject
import com.example.practica_2023.coordGetters.getCoord
import kotlin.math.roundToInt


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
        var x = resources.getStringArray(R.array.places)
        if (isPermissionAvailable(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            x += "Current"
        }

        binding.PlaceChecker.adapter = ArrayAdapter(
            requireContext(),
            R.layout.city_check_layout,
            x
        )
        setStanderBarChartParams(binding.HourTempChart)
        setStanderBarChartParams(binding.HourWSChart)
        linkParams = mutableMapOf(
            "units" to getString(R.string.api_units),
            "appid" to getString(R.string.api_key)
        )
        updateLinkParams()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionCheck()
        binding.swiperefresh.setOnRefreshListener {
            updateLinkParams()
            binding.swiperefresh.isRefreshing = false
        }

        binding.PlaceChecker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateLinkParams()
            }

        }
    }

    private fun permissionCheck() {
        if (!isPermissionAvailable(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                Toast.makeText(activity, "Permission, $it", Toast.LENGTH_LONG).show()
            }
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Updates. Just for less code.
    private fun updateInfo(linkParams: Map<String, String>) {
        stdGetWeatherReport(
            url = getLink(getString(R.string.current_link), linkParams),
            context = context,
            f = ::updateMainInfo
        )
        stdGetWeatherReport(
            url = getLink(getString(R.string.timed_link), linkParams),
            context = context,
            f = {
                val data = it.getJSONArray("list")
                val timesWeatherReportList = mutableListOf<String>()
                val tempWeatherReportList = mutableListOf<Float>()
                val wsWeatherReportList = mutableListOf<Float>()
                val valueTakers = listOf<(JSONObject) -> Unit>(
                    { obj -> timesWeatherReportList.add(obj.getString("dt")) },
                    { obj ->
                        tempWeatherReportList.add(
                            JSONObject(obj.getJSONObject("main").toString())
                                .getDouble("temp")
                                .toFloat()
                        )
                    },
                    { obj ->
                        wsWeatherReportList.add(
                            JSONObject(obj.getJSONObject("wind").toString())
                                .getDouble("speed")
                                .toFloat()
                        )
                    },
                )
                val tt = arrayListOf<BarEntry>()
                val tws = arrayListOf<BarEntry>()

                getWeatherObjectInfo(data, (0..data.length() step 3).iterator(), valueTakers)
                (0 until tempWeatherReportList.size).forEach { pair ->
                    tt.add(BarEntry(pair.toFloat(), tempWeatherReportList[pair]))
                    tws.add(BarEntry(pair.toFloat(), wsWeatherReportList[pair]))
                }
                setBarChartData(binding.HourTempChart, "Temperature", tt)
                setBarChartData(binding.HourWSChart, "WindSpeed", tws)
            }
        )
    }

    private fun updateLinkParams() {
        val params = linkParams.toMutableMap()
        if (binding.PlaceChecker.selectedItem.toString() == "Current") {
            getCoord(
                activity,
                context,
                { s, s2 ->
                    params += mapOf(
                        "lat" to ((s.toFloat() * 100).roundToInt() / 100).toString(),
                        "lon" to ((s2.toFloat() * 100).roundToInt() / 100).toString()
                    )
                    Log.d("Params1", "Params $params")
                    updateInfo(params.toMap())
                },
                {
                    params["q"] = "Omsk"
                    Log.d("Params2", "Params $params")
                    updateInfo(params.toMap())
                }
            )
        } else {
            params["q"] = binding.PlaceChecker.selectedItem.toString()
            updateInfo(params.toMap())
        }
    }

    // Updates. Just for less code.
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    private fun updateMainInfo(weatherInfo: JSONObject) {
        binding.WindSpeedNumLabel.text =
            "${weatherInfo.getJSONObject("wind").getString("speed")} m/s"
        binding.WeatherConditionLabel.text =
            JSONObject(weatherInfo.getJSONArray("weather")[0].toString()).getString("main")
        binding.TempInfoLabel.text = "${weatherInfo.getJSONObject("main").getString("temp")} ºC"
        binding.HumidityInfo.text = "${weatherInfo.getJSONObject("main").getString("humidity")} %"
        binding.WeatherImg.setImageResource(
            resources.getIdentifier(
                JSONObject(weatherInfo.getJSONArray("weather")[0].toString()).getString("main")
                    .lowercase(), "drawable",
                context?.packageName ?: ""
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}

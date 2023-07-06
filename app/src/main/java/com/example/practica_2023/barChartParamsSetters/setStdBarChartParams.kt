package com.example.practica_2023.barChartParamsSetters

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.practica_2023.R
import com.github.mikephil.charting.charts.BarChart


fun setStanderBarChartParams(barChart: BarChart, darkThem: Boolean) {
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
    barChart.axisLeft.setDrawLabels(true);
    barChart.legend.isEnabled = false
    barChart.xAxis.labelRotationAngle = -45f
    barChart.xAxis.setDrawLabels(true)
    if (darkThem){
        barChart.axisLeft.textColor = Color.WHITE
        barChart.xAxis.textColor = Color.WHITE
    }
    barChart.xAxis.setDrawAxisLine(false)
    barChart.axisLeft.setDrawAxisLine(false);
    barChart.axisRight.setDrawAxisLine(false);
    barChart.description.isEnabled = false
}

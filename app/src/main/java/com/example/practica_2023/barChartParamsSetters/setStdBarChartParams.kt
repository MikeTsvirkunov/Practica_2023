package com.example.practica_2023.barChartParamsSetters

import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.practica_2023.R
import com.github.mikephil.charting.charts.BarChart


fun setStanderBarChartParams(barChart: BarChart) {
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
    barChart.xAxis.setDrawLabels(false)
    barChart.xAxis.setDrawAxisLine(false)
    barChart.axisLeft.setDrawAxisLine(false);
    barChart.axisRight.setDrawAxisLine(false);
    barChart.description.isEnabled = false
}

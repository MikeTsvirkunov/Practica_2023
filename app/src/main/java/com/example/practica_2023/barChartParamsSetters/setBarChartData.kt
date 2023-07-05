package com.example.practica_2023.barChartParamsSetters

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

fun setBarChartData(barChart: BarChart, label: String, Data: ArrayList<BarEntry>){
    barChart.data = BarData(BarDataSet(Data, label))
    barChart.invalidate()
}
@file:Suppress("DEPRECATION")

package com.example.practica_2023.barChartParamsSetters

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter


fun setBarChartData(barChart: BarChart, label: String, Data: ArrayList<BarEntry>){
    barChart.data = BarData(BarDataSet(Data, label))
    barChart.invalidate()
}

fun setLabel(barChart: BarChart, xLabel: List<String>){
    val xAxis: XAxis = barChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    with (stdDataXAxisFormatter()){
        this.setData(xLabel)
        val xAxisFormatter: ValueFormatter = this
        xAxis.valueFormatter = xAxisFormatter
    }
}

class stdDataXAxisFormatter : ValueFormatter() {
    private var data = listOf<String>()
    fun setData(d:List<String>){
        data = d
    }
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return data.getOrNull(value.toInt()) ?: value.toString()
    }
}
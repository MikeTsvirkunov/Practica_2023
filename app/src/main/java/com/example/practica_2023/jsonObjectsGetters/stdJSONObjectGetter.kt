package com.example.practica_2023.jsonObjectsGetters

import org.json.JSONArray
import org.json.JSONObject

fun getWeatherObjectInfo(
    data: JSONArray,
    dataIterator: IntIterator,
    valueTakers: List<(JSONObject) -> Unit>
) {
    dataIterator.forEach { index -> valueTakers.forEach { taker -> taker(JSONObject(data[index].toString())) } }
}

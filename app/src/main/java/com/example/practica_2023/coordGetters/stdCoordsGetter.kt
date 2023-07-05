package com.example.practica_2023.coordGetters

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater
import kotlin.math.roundToInt

fun getCoord(
    activity: FragmentActivity?,
    context: Context?,
    valueUpdater: (String, String) -> Unit,
    permissionDeniedProcessor: () -> Unit
) {
    var lat = 0f
    var lon = 0f
    val ret = mutableMapOf<String, String>()
    val ls = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    if (context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } != PackageManager.PERMISSION_GRANTED && context?.let {
            ActivityCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } != PackageManager.PERMISSION_GRANTED
    ) {
        permissionDeniedProcessor()
    }
    if (ls != null) {
        val x = ls.lastLocation
        x.addOnSuccessListener { location: Location? ->
            lat = location?.latitude?.toFloat() ?: 0f
            lon = location?.longitude?.toFloat() ?: 0f
            Log.d("Fast check", "res : $lat, $lon")
            valueUpdater(lat.toString(), lon.toString())
//            ret += mapOf("lat" to ((lat * 100).roundToInt() / 100).toString(), "lon" to ((lon * 100).roundToInt() / 100).toString())
        }
    }
}

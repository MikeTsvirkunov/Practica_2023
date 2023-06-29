package com.example.practica_2023.fragments

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


// check is permission granted
fun Fragment.isPermissionAvailable(permission: String): Boolean{
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, permission) ==
            PackageManager.PERMISSION_GRANTED
}
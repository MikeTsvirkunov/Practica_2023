package com.example.practica_2023

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val dataCurrent = MutableLiveData<String>()
    val dataList = MutableLiveData<List<String>>()
}
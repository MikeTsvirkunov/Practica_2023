package com.example.practica_2023

fun getLink(link: String, vars: Map<String, String>): String {
    var x = link
    vars.forEach { (s, s2) -> x += "$s=$s2&" }
    return x
}

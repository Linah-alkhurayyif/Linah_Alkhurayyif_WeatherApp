package com.example.linah_alkhurayyif_weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    val apiId = "8a316bae40ca552c86771c6d73150592"
    var city = "Seoul"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
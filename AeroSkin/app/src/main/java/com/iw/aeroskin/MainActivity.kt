package com.iw.aeroskin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line connects the Kotlin code to your XML layout file
        setContentView(R.layout.activity_main)
    }
}
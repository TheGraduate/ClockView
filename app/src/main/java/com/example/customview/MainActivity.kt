package com.example.customview

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var clockView: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clockView = findViewById(R.id.clock_view)
        val buttonChangeColor = findViewById<Button>(R.id.button_change_color)
        val buttonChangeBackground = findViewById<Button>(R.id.button_change_background)

        buttonChangeColor.setOnClickListener {
            val newColor = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
            clockView.setSecondHandColor(newColor)
        }

        buttonChangeBackground.setOnClickListener {
            val newColor = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
            clockView.setBackgroundColor(newColor)
        }

        clockView.startSecondHandAnimation()
        clockView.startMinuteHandAnimation()
        clockView.startHourHandAnimation()

    }

}
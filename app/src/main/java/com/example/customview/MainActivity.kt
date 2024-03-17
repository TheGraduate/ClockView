package com.example.customview

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var clockView: ClockCustomView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         clockView = findViewById(R.id.clock_view)

        val clockFaceButtonChangeColor = findViewById<View>(R.id.clock_face_button_change_color)
        val secondHandButtonChangeColor = findViewById<View>(R.id.second_hand_button_change_color)
        val minuteHandButtonChangeColor = findViewById<View>(R.id.minute_hand_button_change_color)
        val hourHandButtonChangeColor = findViewById<View>(R.id.hour_hand_button_change_color)
        val stopStartButton = findViewById<View>(R.id.stopStartButton)
        val changeNumericalButton: SwitchCompat = findViewById(R.id.displayModeSwitch)


        clockFaceButtonChangeColor.setOnClickListener {
            val randomColor = generateRandomColor()
            clockView.setClockFaceColor(randomColor)
        }

        secondHandButtonChangeColor.setOnClickListener {
            val randomColor = generateRandomColor()
            clockView.setSecondHandColor(randomColor)
        }

        minuteHandButtonChangeColor.setOnClickListener {
            val randomColor = generateRandomColor()
            clockView.setMinuteHandColor(randomColor)
        }

        hourHandButtonChangeColor.setOnClickListener {
            val randomColor = generateRandomColor()
            clockView.setHourHandColor(randomColor)
        }

        stopStartButton.setOnClickListener {
            clockView.stopStartClock()
        }

        changeNumericalButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                clockView.toggleDisplayMode()
            } else {
                clockView.toggleDisplayMode()
            }
        }


    }

    private fun generateRandomColor(): Int {
        val random = Random.Default
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.rgb(red, green, blue)
    }

}
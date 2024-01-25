package com.example.tummocassignment

/*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
*/

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var precisionSpinner: Spinner
    private lateinit var stopwatchText: TextView
    private lateinit var startPauseButton: Button
    private lateinit var resetButton: Button

    private var isRunning = false
    private var elapsedTime = 0L
    private lateinit var precision: String
    private lateinit var stopwatchJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        precisionSpinner = findViewById(R.id.precisionSpinner)
        stopwatchText = findViewById(R.id.stopwatchText)
        startPauseButton = findViewById(R.id.startPauseButton)
        resetButton = findViewById(R.id.resetButton)

        val precisionOptions = arrayOf("seconds", "milliseconds")
        val precisionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, precisionOptions)
        precisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        precisionSpinner.adapter = precisionAdapter

        precisionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                precision = precisionOptions[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }

        startPauseButton.setOnClickListener {
            if (isRunning) {
                pauseStopwatch()
            } else {
                startStopwatch()
            }
        }

        resetButton.setOnClickListener {
            resetStopwatch()
        }
    }

    private fun startStopwatch() {
        startPauseButton.text = "Pause"
        isRunning = true

        stopwatchJob = CoroutineScope(Dispatchers.Main).launch {
            while (isRunning) {
                updateStopwatchText()
                delay(if (precision == "seconds") 1000 else 1)
                elapsedTime += if (precision == "seconds") 1000 else 1
            }
        }
    }

    private fun pauseStopwatch() {
        startPauseButton.text = "Start"
        isRunning = false
        stopwatchJob.cancel()
    }

    private fun resetStopwatch() {
        isRunning = false
        elapsedTime = 0
        startPauseButton.text = "Start"
        stopwatchText.text = "00:00:00" + if (precision == "milliseconds") ":000" else ""
    }

    private fun updateStopwatchText() {
        val hours = elapsedTime / 3600000
        val minutes = (elapsedTime % 3600000) / 60000
        val seconds = (elapsedTime % 60000) / 1000
        val milliseconds = elapsedTime % 1000

        val formattedTime = "%02d:%02d:%02d".format(hours, minutes, seconds) +
                if (precision == "milliseconds") ":%03d".format(milliseconds) else ""

        stopwatchText.text = formattedTime
    }
}

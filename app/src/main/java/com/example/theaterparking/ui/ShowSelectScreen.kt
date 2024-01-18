package com.example.theaterparking.ui

import CustomSpinnerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import com.example.theaterparking.R

class ShowSelectScreen : AppCompatActivity() {
    private lateinit var showsSpinner: Spinner
    private lateinit var continueBtn: Button

    private val showTimings = listOf("9AM-12PM", "12PM-3PM", "3PM-6PM", "6PM-9PM", "9PM-12AM")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_screen)
        showsSpinner = findViewById(R.id.spinner)
        val adapter = CustomSpinnerAdapter(this, showTimings)
        showsSpinner.adapter = adapter
        continueBtn = findViewById(R.id.continueBtn)
        handleChange() // call this function to handle the spinner selection
        handleClick()
    }

    private val handleChange = {
        showsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = showTimings[position]
                // Handle the selected item
                // todo : save this in shared preferences
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    private val handleClick = {
        // todo : save the selected show timing in shared preferences
        continueBtn.setOnClickListener {
            startActivity(Intent(this, ParkingEntryScreen::class.java))
        }
    }
}


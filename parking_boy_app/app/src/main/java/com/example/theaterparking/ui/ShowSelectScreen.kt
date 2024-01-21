 package com.example.theaterparking.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.camera.core.ExperimentalGetImage
import com.example.theaterparking.R
import com.example.theaterparking.ui.spinners.CustomSpinnerAdapter
import com.example.theaterparking.utils.StorageUtils

 @ExperimentalGetImage
class ShowSelectScreen : AppCompatActivity() {
    private lateinit var showsSpinner: Spinner
    private lateinit var continueBtn: Button

    private var showTimings = listOf("9AM-12PM", "12PM-3PM", "3PM-6PM", "6PM-9PM", "9PM-12AM")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_screen)
        showsSpinner = findViewById(R.id.spinner)
        // get the selected shows from the previous screen
        val selectedShows = intent.getStringArrayExtra("shows")
        if (!selectedShows.isNullOrEmpty()) {
            showTimings = selectedShows.toList()
        }
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
                // Do nothing
            }
        }
    }

    private val handleClick = {
        val selectedShow = showsSpinner.selectedItem.toString()
        StorageUtils.putData(
            "selectedShow",
            selectedShow,
            this
        ) // saving the selected show in shared preferences
        continueBtn.setOnClickListener {
            val intent = Intent(this, ParkingEntryScreen::class.java)
            startActivity(intent)
        }
    }
}


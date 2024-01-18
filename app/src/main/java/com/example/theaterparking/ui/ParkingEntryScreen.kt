package com.example.theaterparking.ui

import CustomParkingAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.theaterparking.R

class ParkingEntryScreen : AppCompatActivity() {

    private lateinit var parkingList: ListView
    private var parkings = listOf(
        "Parking 1",
        "Parking 2",
        "Parking 3",
        "Parking 4",
        "Parking 5",
        "Parking 6",
        "Parking 7",
        "Parking 8",
        "Parking 9",
        "Parking 10",
        "Parking 11"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parking_entry)

        parkingList = findViewById(R.id.parkingList)
        val adapter = CustomParkingAdapter(this, parkings)
        parkingList.adapter = adapter
    }
}
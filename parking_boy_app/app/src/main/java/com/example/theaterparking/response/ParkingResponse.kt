package com.example.theaterparking.response

import com.google.gson.annotations.SerializedName

data class ParkingResponse(
    val id: Int,
    @SerializedName("entry_time") val entryTime: String,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    val amount: String,
)
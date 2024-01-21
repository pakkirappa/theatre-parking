package com.example.theaterparking.dto

import com.google.gson.annotations.SerializedName

data class Parking(
    @SerializedName("id") val id: Int,
    @SerializedName("entry_time") val time: String,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("amount") val amount: String,
)

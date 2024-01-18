package com.example.theaterparking.models

import com.google.gson.annotations.SerializedName

data class Parking(
    @SerializedName("id") val id: Int,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("amount") val amount: String,
)

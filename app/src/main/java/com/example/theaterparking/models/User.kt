package com.example.theaterparking.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("amount") val amount: String,
)

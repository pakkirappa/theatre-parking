package com.example.theaterparking.dto

import com.google.gson.annotations.SerializedName

data class ParkingEntryDto(
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("show_id") val showId: String,
    @SerializedName("client_id") val clientId: String,
    @SerializedName("amount") val amount: String,
)
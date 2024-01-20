package com.example.theaterparking.utils

object ParkingUtils {
    fun isCorrectVehicleNumber(vehicleNumber: String): Boolean {
        return vehicleNumber.matches(Regex("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}"))
    }
}
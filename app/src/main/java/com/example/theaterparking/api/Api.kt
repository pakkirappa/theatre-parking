package com.example.theaterparking.api

import com.example.theaterparking.models.Parking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Api {
    @GET("/api/v1/parkings")
    suspend fun getParkings(): List<Parking>
}

// create a service here
val api = Retrofit.Builder()
    .baseUrl("https://theater-parking.herokuapp.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Api::class.java)
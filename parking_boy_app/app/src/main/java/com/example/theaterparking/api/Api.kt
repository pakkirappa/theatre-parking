package com.example.theaterparking.api

import com.example.theaterparking.dto.LoginDto
import com.example.theaterparking.dto.ParkingEntryDto
import com.example.theaterparking.response.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val LOCAL_API_URL = "http://192.168.0.100/api/"
private const val PROD_API_URL = "https://theater-parking.herokuapp.com"

interface Api {
    @POST("users/login")
    suspend fun login(@Body loginDto: LoginDto): Response<LoginResponse>

    @POST("parkings")
    suspend fun createParking(@Body data: ParkingEntryDto): Response<CommonResponse>

    @GET("parkings/filter")
    suspend fun getParkingsByUserId(@Query("userId") userId: String): Response<List<ParkingResponse>>
}

// Using Dependency Injection
class ApiProvider {
    companion object {
        fun provideApi(): Api {
            return Retrofit.Builder()
                .baseUrl(LOCAL_API_URL) // Change this to PROD_API_URL for produ
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }
}

// Usage
val api = ApiProvider.provideApi()
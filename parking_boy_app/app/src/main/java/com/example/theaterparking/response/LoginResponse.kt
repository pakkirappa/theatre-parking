package com.example.theaterparking.response

data class LoginResponse (
    val msg: String,
    val token: String,
    val id: Int,
    val clientId: Int,
    val shows: List<String>
)
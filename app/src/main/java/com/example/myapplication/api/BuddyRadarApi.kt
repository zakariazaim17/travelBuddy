package com.example.myapplication.api

import com.example.myapplication.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BuddyRadarApi {
    //Get Methods


    //Post Methods
    @POST("user/create")
    suspend fun createUser(
        @Body user:RegisterUser
    ):Response<RegistrationResponse>

    @POST("user/login")
    suspend fun login(
        @Body user: LoginUser
    ):Response<LoginResponse>

    //
}
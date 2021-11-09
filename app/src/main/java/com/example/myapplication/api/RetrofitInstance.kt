package com.example.myapplication.api

import com.example.myapplication.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api:BuddyRadarApi by lazy {
        retrofit.create(BuddyRadarApi::class.java)
    }
}
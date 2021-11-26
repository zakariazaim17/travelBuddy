package com.example.myapplication.api

import android.content.Context
import com.example.myapplication.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {


private val client = OkHttpClient.Builder().apply {
    addInterceptor(MyInterceptor())
   readTimeout(30, TimeUnit.SECONDS)
    writeTimeout(30, TimeUnit.SECONDS)

}.build()
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api:BuddyRadarApi by lazy {
        retrofit.create(BuddyRadarApi::class.java)
    }
}
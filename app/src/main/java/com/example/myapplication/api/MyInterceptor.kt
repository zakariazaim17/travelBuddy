package com.example.myapplication.api

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.myapplication.MainActivity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.nio.file.attribute.AclEntry.newBuilder
private val applicationContext = MainActivity().applicationContext
class MyInterceptor : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {

        //val sharedPreferences: SharedPreferences = MainActivity().MainContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
       // val userToken: String? = sharedPreferences.getString("User_token",null);
        val request: Request = chain.request()
            .newBuilder()
            //.addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJtYWluIiwiaXNzIjoiaHR0cDovLzAuMC4wLjA6ODA4Mi8iLCJleHAiOjE2NjgwODg1ODMsInVzZXJJZCI6IjYxODUwMzA0YTY3NDUxMjQ5MzBhYzJkNiJ9._vLaTWAOEdmRI5BLQDlR0TZsQnu8IMywDwzhLM6pAgc")
            .build()

        return chain.proceed(request)
    }
}
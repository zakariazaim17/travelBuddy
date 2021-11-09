package com.example.myapplication.repository

import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.model.*
import retrofit2.Response

class Repository {


    suspend fun createUser(user:RegisterUser):Response<RegistrationResponse>{
       return RetrofitInstance.api.createUser(user)
    }

    suspend fun login(user:LoginUser):Response<LoginResponse>{
        return  RetrofitInstance.api.login(user)
    }
}
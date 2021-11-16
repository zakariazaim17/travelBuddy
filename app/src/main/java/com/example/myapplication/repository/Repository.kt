package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.model.*

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.File

class Repository {


    suspend fun createUser(user:RegisterUser):Response<RegistrationResponse>{
       return RetrofitInstance.api.createUser(user)
    }

    suspend fun login(user:LoginUser):Response<LoginResponse>{
        return  RetrofitInstance.api.login(user)
    }

    suspend fun FollowUser(token:String,users:Follow):Response<FollowResponse>{
        return  RetrofitInstance.api.followUser(token,users)
    }
    suspend fun UnfollowUSer(token:String, users: Unfollow):Response<FollowResponse>{
        return RetrofitInstance.api.unFollowUser(token, users)
    }
    suspend fun createPost(token: String, post:CreatePost):Response<CreatePostResponse> {
        return RetrofitInstance.api.createPost(token, post)
    }
    suspend fun getAllPosts(token:String):Response<List<GetAllPostResponse>>{
        return RetrofitInstance.api.getAllPosts(token= token)
    }






}
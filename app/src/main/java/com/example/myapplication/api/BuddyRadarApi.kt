package com.example.myapplication.api

import com.example.myapplication.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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

    @POST("following/follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Body users:Follow

    ):Response<FollowResponse>

    @POST("following/unfollow")
    suspend fun unFollowUser(
        @Header("Authorization") token: String,
        @Body users: Unfollow
    ):Response<FollowResponse>

    //Post
    @POST("post/createe")
    suspend fun createPost(
        @Header("Authorization") token:String,
        @Body post:CreatePost
    ):Response<CreatePostResponse>


}
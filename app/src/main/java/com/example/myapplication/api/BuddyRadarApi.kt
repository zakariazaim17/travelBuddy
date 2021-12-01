package com.example.myapplication.api

import com.example.myapplication.model.*
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

    //Posts

    @POST("post/createe")
    suspend fun createPost(
        @Header("Authorization") token:String,
        @Body post:CreatePost
    ):Response<CreatePostResponse>

    @GET("post/all")
    suspend fun getAllPosts(
        @Header("Authorization") token:String,
    ):Response<List<GetAllPostResponse>>


    //Comments
    @GET("comment/get")
    suspend fun getPostComments(
        @Query("postId") postId:String,
        @Header("Authorization") token:String,
    ):Response<List<GetCommentsResponse>>

    @POST("comment/create")
    suspend fun createComment(
        @Header("Authorization") token:String,
        @Body comment:CreateComment
    ):Response<CreateCommentResponse>


    //Plans
    @GET("plan/filter")
    suspend fun getFilteredPlans(
        @Query("category") category:String,
        @Query("subCategory") subCategory:String?,
        @Header("Authorization") token: String?,
    ):Response<List<GetAllPlansResponse>>

    @POST("plan/create")
    suspend fun createPlans(
        @Header("Authorization") token: String?,
        @Body plan:CreatePlanRequest,
    ):Response<CreatePlanResponse>


    //Profile

    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String?,
        @Query("userId") userId:String,
    ):Response<GetUserProfileResponse>

    @GET("user/posts")
    suspend fun getOwnUserPosts(
        @Header("Authorization") token: String?,

        ):Response<List<GetAllPostResponse>>

}
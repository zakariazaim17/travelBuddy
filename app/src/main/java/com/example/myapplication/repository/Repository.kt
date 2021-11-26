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
    suspend fun getPostComments(token: String, postId:String):Response<List<GetCommentsResponse>>{
        return RetrofitInstance.api.getPostComments(token = token, postId = postId)
    }
    suspend fun createComment(token: String, comment:CreateComment):Response<CreateCommentResponse>{
        return RetrofitInstance.api.createComment(token, comment)
    }
    suspend fun getFilteredPlans(token: String, category:String, subCategory:String?):Response<List<GetAllPlansResponse>>{
        return RetrofitInstance.api.getFilteredPlans(token= token, category= category, subCategory=subCategory)
    }






}
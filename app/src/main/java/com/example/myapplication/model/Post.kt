package com.example.myapplication.model

import java.io.File
import java.net.URI

data class CreatePost(
    val description:String,
    val image:String,
)

data class CreatePostResponse(
    val success:Boolean,
    val message:String
)
data class GetAllPostResponse(
    val imageUrl:String,
    val userId:String,
    val timestamp:Long,
    val username:String,
    val profileImageUrl:String,
    val description:String,
    val likeCount:Int = 0,
    val commentCount:Int = 0,
    val id:String,
    val isImage:Boolean,
)
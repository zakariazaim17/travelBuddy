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

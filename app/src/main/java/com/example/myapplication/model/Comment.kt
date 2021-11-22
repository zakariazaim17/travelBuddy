package com.example.myapplication.model


data class GetCommentsResponse(
    val id:String,
    val username:String,
    val profilePictureUrl:String,
    val timestamp:Long,
    val comment:String,
    val isLiked:Boolean,
    val likeCount:Int
)
data class CreateComment(
        val comment: String,
        val postId:String
)

data class CreateCommentResponse(
    val success:Boolean,
    val message:String,
    val data:String

)

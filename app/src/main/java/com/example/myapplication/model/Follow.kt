package com.example.myapplication.model

data class Follow(
    val followingUserId:String,
    val followedUserId:String,
)

data class FollowResponse(
  val  success: Boolean,
  val  message: String,
  val data: String,
)
data class Unfollow(
    val followingUserId:String,
    val followedUserId:String,
)
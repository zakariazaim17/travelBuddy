package com.example.myapplication.model

data class CreatePlanRequest(
    val title:String,
    val from:String,
    val to:String,
    val date:String,
    val time: String,
    val category:String,
    val subCategory:String
)

data class CreatePlanResponse(
   val success: Boolean,
    val message: String
)

data class GetAllPlansResponse(
    val userId:String,
    val title:String,
    val category:String,
    val username:String,
    val profilePictureUrl:String,
    val subCategory:String,
    val from:String,
    val to:String,
    val date:String,
    val time:String,
    val id:String,
    val isBuddy:Boolean
)

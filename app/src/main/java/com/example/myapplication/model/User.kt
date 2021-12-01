package com.example.myapplication.model
//Registration Models
data class RegisterUser (
    val email:String,
    val username:String,
    val password:String,
)
//represents model of returned registered user data from Backend
data class RegistrationResponse(
    val success: Boolean,
    val message:String,
)
//<--------------------/>

//Login Models
data class LoginUser(
    val email: String,
    val password: String,
)
//represents model of returned loged-In user data from Backend
data class LoginResponse (
    val success: Boolean,
    val data: LoginresponseData,
    val message:String,
    )

data class LoginresponseData(
    val token:String,
    val userId: String
)
//<--------------------/>

data class GetUserProfileResponse(
    val success: Boolean,
    val message: String,
    val data:UserData,

)

data class UserData (
        val userId:String,
        val username:String,
        val profilePictureUrl:String,
        val bio:String,
        val hobbies:List<String>,
        val followerCount:Int,
        val followingCount:Int,
        val postCount:Int,
        val bannerImageUrl:String,
        val isBuddy:Boolean,
        val isOwnProfile:Boolean,
        )



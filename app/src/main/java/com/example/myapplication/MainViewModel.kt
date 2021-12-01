package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository:Repository): ViewModel() {

    //Response Models
    //register
    val registerResponse:MutableLiveData<Response<RegistrationResponse>> = MutableLiveData()
    //login
    val loginResponse:MutableLiveData<Response<LoginResponse>> = MutableLiveData()
    //follow
    val followResponse: MutableLiveData<Response<FollowResponse>> = MutableLiveData()
    val unfollowResponse: MutableLiveData<Response<FollowResponse>> = MutableLiveData()

    //posts
    val createPostResponse: MutableLiveData<Response<CreatePostResponse>> = MutableLiveData()
    val getAllPostsResponse:MutableLiveData<Response<List<GetAllPostResponse>>> = MutableLiveData()

    //Comments
    val getPostcommentResponse:MutableLiveData<Response<List<GetCommentsResponse>>> = MutableLiveData()
    val createCommentResponse:MutableLiveData<Response<CreateCommentResponse>> = MutableLiveData()
    val getCurrentUserPlansResponse:MutableLiveData<Response<List<GetAllPlansResponse>>> = MutableLiveData()

    //Plans
    val createPlanResponse:MutableLiveData<Response<CreatePlanResponse>> = MutableLiveData()

    //Profile
    val getUserProfileResponse:MutableLiveData<Response<GetUserProfileResponse>> = MutableLiveData()
    val getOwnUserPostsResponse:MutableLiveData<Response<List<GetAllPostResponse>>> = MutableLiveData()


//ViewModelsFunctions
    //register
    fun createUser(user:RegisterUser){
        viewModelScope.launch {
            val response:Response<RegistrationResponse> = repository.createUser(user)
            registerResponse.value = response
        }
    }
    //login
    fun login(user:LoginUser){
        viewModelScope.launch {
            val response:Response<LoginResponse> = repository.login(user)
            loginResponse.value = response
        }
    }
    //Follow

    fun followUser(token:String,users:Follow){
        viewModelScope.launch {
            val response:Response<FollowResponse> = repository.FollowUser(token, users)
            followResponse.value = response
        }
    }

    fun unfollowUser(token: String, users: Unfollow){
        viewModelScope.launch {
            val response:Response<FollowResponse> = repository.UnfollowUSer(token, users)
            unfollowResponse.value = response
        }
    }

    fun createPost(token:String, post:CreatePost ){
        viewModelScope.launch {
            val response:Response<CreatePostResponse> = repository.createPost(token, post )
            createPostResponse.value = response

        }
    }

    fun getAllPosts(token: String){
        viewModelScope.launch {
            val response:Response<List<GetAllPostResponse>> = repository.getAllPosts(token)
            getAllPostsResponse.value = response
        }
    }

    fun getPostComments(token: String, postId:String){
        viewModelScope.launch {
            val response:Response<List<GetCommentsResponse>> = repository.getPostComments(token, postId)
            getPostcommentResponse.value = response
        }
    }

    fun createPostcomment(token:String, comment:CreateComment){
        viewModelScope.launch {
            val response:Response<CreateCommentResponse> = repository.createComment(token, comment)
            createCommentResponse.value = response
        }
    }

    fun getCurrentUserPlans(token: String, category:String, subCategory:String?){
        viewModelScope.launch {
            val response:Response<List<GetAllPlansResponse>> = repository.getFilteredPlans(token, category, subCategory)
            getCurrentUserPlansResponse.value = response
        }
    }

    fun createPlans(token:String, plan:CreatePlanRequest){
        viewModelScope.launch {
            val response:Response<CreatePlanResponse> = repository.createPlans(token, plan)
            createPlanResponse.value = response
        }
    }

    fun getUserProfile(token:String, userId:String){
        viewModelScope.launch {
            val response:Response<GetUserProfileResponse> = repository.getUseProfile(token, userId)
            getUserProfileResponse.value = response
        }
    }

    fun getOwnUserPosts(token:String){
        viewModelScope.launch {
            val response:Response<List<GetAllPostResponse>> = repository.getOwnUserPosts(token)
            getOwnUserPostsResponse.value = response
        }
    }
}
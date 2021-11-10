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
}
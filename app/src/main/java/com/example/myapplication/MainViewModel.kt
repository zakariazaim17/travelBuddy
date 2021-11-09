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
}
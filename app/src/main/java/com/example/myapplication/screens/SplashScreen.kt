package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSplashBinding
import com.example.myapplication.repository.Repository
import java.util.*


private var _binding: FragmentSplashBinding?= null
private val binding get() = _binding!!

class SplashScreen : Fragment() {


    private lateinit var viewModel: MainViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val userToken: String? = sharedPreferences.getString("User_token",null);
        val mapAnimated:Boolean? = sharedPreferences.getBoolean("mapAnimated", false)
        val isUserLoggedIn:Boolean = sharedPreferences.getBoolean("isLoggedIn", false)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();
        editor.apply {
            putBoolean("mapAnimated", false)
        }.apply()

        Handler(Looper.myLooper()!!).postDelayed({



            if (isUserLoggedIn){
                (activity as MainActivity).replaceCurrentFragment(MapScreen())
            }else{
                (activity as MainActivity).replaceCurrentFragment(LoginScreen())
            }


        }, 3000)
    }

}
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
import com.example.myapplication.repository.Repository
import kotlinx.android.synthetic.main.fragment_splash.*
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SplashScreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: MainViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val userToken: String? = sharedPreferences.getString("User_token",null);
        val mapAnimated:Boolean? = sharedPreferences.getBoolean("mapAnimated", false)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();
        editor.apply {
            putBoolean("mapAnimated", false)
        }.apply()

        Handler(Looper.myLooper()!!).postDelayed({



            if (userToken != null){
                    Log.d("dd", "{\"description\": \"hellothreree\", \"image\":\"https://1.bp.blogspot.com/-lIfFZILZhBg/WyfY50xXXFI/AAAAAAAABEE/fGD29MdIHhIGwcf0tdvl6lz8uRYQSNcOgCLcBGAs/w1200-h630-p-k-no-nu/android-retrofit-2-crud-example.jpg\"}")
                (activity as MainActivity).replaceCurrentFragment(MapScreen())
            }else{
                (activity as MainActivity).replaceCurrentFragment(LoginScreen())
            }


        }, 3000)
    }

}
package com.example.myapplication.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterBinding

import com.example.myapplication.model.RegisterUser
import com.example.myapplication.repository.Repository



private var _binding: FragmentRegisterBinding? = null
private val binding get() = _binding!!
private lateinit var viewModel: MainViewModel
class RegisterScreen : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
       return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)



        binding.registerBtn?.setOnClickListener {
            val newUser = RegisterUser(binding.editTextTextEmail?.text.toString(), binding.editTextTextPersonName.text.toString(), binding.editTextTextPassword?.text.toString())

            viewModel.createUser(newUser)
            viewModel.registerResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    if(response.body()!!.success){

                        Toast.makeText(context, "Please Log in",Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).replaceCurrentFragment(LoginScreen())
                    }
                    Log.d("Main1", response.body().toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())


                    /*Toast.makeText(context, "Registered successfully",Toast.LENGTH_SHORT).show()
                    binding.editTextTextEmail?.text?.clear()*/


                }else{
                    Toast.makeText(context, "Seems there is a problem, Retry later ⏳",Toast.LENGTH_SHORT).show()
                    Log.d("Main1", response.errorBody().toString())

                }

            })
            // (activity as MainActivity).replaceCurrentFragment(LoginScreen())
        }
    }

}
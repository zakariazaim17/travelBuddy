package com.example.myapplication

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
import com.example.myapplication.model.RegisterUser
import com.example.myapplication.repository.Repository

import kotlinx.android.synthetic.main.fragment_register.*



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
       return inflater.inflate(R.layout.fragment_register, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)



        register_btn.setOnClickListener {
            val newUser = RegisterUser(editTextTextEmail.text.toString(), editTextTextPersonName.text.toString(), editTextTextPassword.text.toString())

            viewModel.createUser(newUser)
            viewModel.registerResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    Log.d("Main1", response.body().toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())
                    Toast.makeText(context, "Registered successfully",Toast.LENGTH_SHORT).show()
                    editTextTextEmail.text.clear()


                }else{
                    Log.d("Main1", response.errorBody().toString())

                }

            })
           // (activity as MainActivity).replaceCurrentFragment(LoginScreen())
        }
    }

}
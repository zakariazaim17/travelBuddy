package com.example.myapplication.screens

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
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.databinding.FragmentMapScreenBinding
import com.example.myapplication.model.LoginUser
import com.example.myapplication.repository.Repository


private var _binding: FragmentLoginBinding? = null
private val binding get() = _binding!!

private lateinit var viewModel: MainViewModel
class LoginScreen : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
       return binding.root

       // view.findViewById<TextView>(R.id.register_textview).setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //textView_logo_name.text = "hope"
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.registerTextview?.setOnClickListener{
            (activity as MainActivity).replaceCurrentFragment(RegisterScreen())

        }

        binding.loginBtn?.setOnClickListener {

val user = LoginUser(binding.editTextEmail?.text.toString(), binding.editTextPassword?.text.toString())

            viewModel.login(user)
            viewModel.loginResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    if(!response.body()!!.success){
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    }else{
                        //save token for later use
                            editor.putString("User_token", response.body()?.data?.token.toString())
                            .apply()
                        Toast.makeText(context, "login successfully" , Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).replaceCurrentFragment((MapScreen()))
                    }
                    Log.d("Main1", response.body()?.data?.token.toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())
                }else{
                    Log.d("Main1", response.errorBody().toString())

                }

            })
        }




    }


}
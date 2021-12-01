package com.example.myapplication.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentProfileScreenBinding
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.repository.Repository

private var _binding: FragmentProfileScreenBinding? = null
private val binding get() = _binding!!
private var userToken:String? = null
private var listOfPosts:List<GetAllPostResponse>? = null
private var userId :String ?= null
private lateinit var viewModel: MainViewModel


class ProfileScreen : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("User_token",null);
        userId = sharedPreferences.getString("User_Id", null)

        val layoutManager: LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.ownUserPostsRecyclerView.layoutManager = layoutManager

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)


        binding.ownUserPostsRecyclerView.visibility = View.GONE
        binding.parentUpdatingDialogButtons.visibility = View.GONE



        viewModel.getUserProfile(token = "Bearer ${userToken.toString()}", userId = userId!!)
        viewModel.getOwnUserPosts(token = "Bearer ${userToken.toString()}")

        viewModel.getUserProfileResponse.observe(viewLifecycleOwner , {response ->
            if (response.isSuccessful){

                if (response.body()?.success == true){
                    Log.d("successful Call", response.body().toString() )


                  binding.userProfileImageImageView.load(response.body()?.data?.profilePictureUrl){
                      crossfade(true)
                      crossfade(100)
                      transformations(CircleCropTransformation())
                  }
                    binding.userBioEditText.setText(response.body()?.data?.bio)
                    binding.userBioEditText.isEnabled = false

                    binding.usernameEditText.setText(response.body()?.data?.username)
                    binding.usernameEditText.isEnabled = false


                }
            }else{
                Log.d("some error in calling", response.errorBody().toString())
            }
        })

        viewModel.getOwnUserPostsResponse.observe(viewLifecycleOwner , {response ->
            if (response.isSuccessful){

                if (response.body()?.size!! > 0 ){
                    Log.d("successful Call", response.body().toString() )

                    binding.ownUserPostsRecyclerView.visibility = View.VISIBLE
                   binding.ownUserPostsRecyclerView.adapter =ProfileAdapter(response.body()!!,requireContext())


                }else{
                    Log.d("profilerecycler", "No Post found for this user")
                }
            }else{
                Log.d("some error in calling", response.errorBody().toString())
            }
        })



        binding.updateUserInfoButton.setOnClickListener {
            binding.usernameEditText.isEnabled = true
            binding.userBioEditText.isEnabled = true
            binding.parentUpdatingDialogButtons.visibility = View.VISIBLE


        }

        binding.ConfirmUpdateBtn.setOnClickListener {
            //if (binding.userBioEditText.text.trim().isNotEmpty() &&  binding.usernameEditText.text.trim().isNotEmpty())  -------------- continue here ><<<<<<<<
        }

    }




}
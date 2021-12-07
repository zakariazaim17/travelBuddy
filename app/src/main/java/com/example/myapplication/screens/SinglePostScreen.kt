package com.example.myapplication.screens

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentSinglePostScreenBinding
import com.example.myapplication.model.CreateComment
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.model.GetCommentsResponse
import com.example.myapplication.repository.Repository

private lateinit var viewModel: MainViewModel
private var _binding:FragmentSinglePostScreenBinding ? = null
private val binding get() = _binding!!
private var listOfComments:List<GetCommentsResponse>? = null
private var isCommentsShown:Boolean = false

class SinglePostScreen(post: GetAllPostResponse? = null) : Fragment() {
    val singlePost: GetAllPostResponse ?= post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentSinglePostScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentsRecyclerView.visibility = View.GONE
        binding.commentLayout.visibility = View.GONE

        val layoutManager: LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.commentsRecyclerView.layoutManager = layoutManager

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
       val userToken = sharedPreferences.getString("User_token",null);

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.singlePostImageImageView.load(singlePost?.imageUrl){
            crossfade(true)
            crossfade(100)
            transformations(RoundedCornersTransformation(20f))
        }
        binding.postDescriptionTextView
        binding.postCommentsSumTextView.setOnClickListener {
            if (!isCommentsShown){
                isCommentsShown = true
                viewModel.getPostComments(token = "Bearer ${userToken.toString()}", postId = singlePost?.id.toString())

                viewModel.getPostcommentResponse.observe(viewLifecycleOwner, { response->
                    if (response.isSuccessful){
                        if (response.body()?.size!! > 0){
                            listOfComments = response.body()
                            binding.postDescriptionTextView.visibility = View.GONE
                            binding.commentLayout.visibility = View.VISIBLE

                            binding.commentsRecyclerView.visibility = View.VISIBLE
                            Log.d("GetComments", response.body().toString() )
                            Log.d("GetComments", response.body()!!.size.toString())


                            binding.commentsRecyclerView.adapter = CommentsAdapter(listOfComments!!)
                        }else{

                            binding.commentLayout.visibility = View.VISIBLE
                            binding.postDescriptionTextView.visibility = View.GONE
                            binding.commentsRecyclerView.visibility = View.VISIBLE
                            Log.d("GetComments", "No comments yet")
                        }
                        binding.sendCommentButton.setOnClickListener {



                            viewModel.createPostcomment("Bearer ${userToken.toString()}", CreateComment(binding.commentEditText.text.toString(), singlePost?.id!!))
                            Toast.makeText(requireContext(), "Comment Posted", Toast.LENGTH_SHORT).show()
                            binding.commentEditText.text.clear()
                            viewModel.getPostComments(token = "Bearer ${userToken.toString()}", postId = singlePost?.id.toString())

                        }
                    }else{
                        Log.d("GetComments", response.errorBody().toString())
                    }

                })
            }else{
                isCommentsShown = false
                binding.postDescriptionTextView.visibility = View.VISIBLE
                binding.commentsRecyclerView.visibility = View.GONE
                binding.commentLayout.visibility = View.GONE
            }


        }

        binding.postDescriptionTextView.text = singlePost?.description
        binding.postCommentsSumTextView.text = "${singlePost?.commentCount} Comments"
        binding.backButton.setOnClickListener {
            (activity as MainActivity).goBack()
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
package com.example.myapplication.screens

import android.app.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentAddPostScreenBinding

import com.example.myapplication.model.CreatePost

import com.example.myapplication.repository.Repository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.Dispatcher


import java.io.File

import kotlin.random.Random
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat.getSystemService


private var isFabOpen :Boolean = false
private var ImageUri:Uri ?= null
private lateinit var viewModel: MainViewModel
private var userToken:String ?= null
private var isAnimated:Boolean = false


class AddPostScreen : Fragment() {

    private var _binding: FragmentAddPostScreenBinding? = null
    private val binding get()= _binding!!

    private val CAMERA_REQUEST_CODE = 200
    private val GALLERY_REQUEST_CODE = 300
    var process: Job? = null
    var process2: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostScreenBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        ImageUri = null

        process?.cancel()
        process2?.cancel()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        process?.cancel()
        process2?.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.uploadPostAnimation.visibility = View.GONE
        binding?.uploadPostAnimation.alpha = 0.0f


        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("User_token",null);


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)





        binding.editTextPostDescription.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(binding.editTextPostDescription)
            }
        })





        binding?.addFloatingBtn.setOnClickListener {
            //hideKeyboard(binding.editTextPostDescription)

            openGalleryForImage()
        }


        binding?.buttonUploadPost.setOnClickListener {
        if (ImageUri != null && binding.editTextPostDescription.text.trim().isNotEmpty()){

            uploadImageToFirebase(ImageUri!!)

        }else{
            Toast.makeText(requireContext(), "Please fill-in the required fields", Toast.LENGTH_SHORT).show()
        }


        }


    }
    //for capturing photo with camera
   /* fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }
    */

    fun hideKeyboard(view: View) {

        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }





/*
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    */

    //for picking image from gallery
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

//For getting result after picking or capturing Photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null){
            binding?.imageView.setImageURI(data?.data) // handle chosen image
            ImageUri = data?.data
        }else if(resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE && data != null){
            binding?.imageView.setImageBitmap(data.extras?.get("data") as Bitmap)

            ImageUri = data?.data
        }
    }

//To upload post
    private fun uploadImageToFirebase(uri:Uri){
    process= CoroutineScope(Dispatchers.IO).launch {

        requireActivity().runOnUiThread(java.lang.Runnable {
            binding.buttonUploadPost.isEnabled = false
            binding.uploadPostAnimation.visibility = View.VISIBLE
            binding.uploadPostAnimation.animate().alpha(1.0f)
            binding.addFloatingBtn.isEnabled = false
            binding.editTextPostDescription.isEnabled = false
        })




        Log.d("pic", uri.toString())


        var filePath:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c:Cursor? = requireActivity().contentResolver.query(
            uri!!, filePath,
            null, null, null);
        c?.moveToFirst();
        var columnIndex:Int = c!!.getColumnIndex(filePath[0]);
        val FilePathStr = c.getString(columnIndex);
        Log.d("mm", FilePathStr)
        c.close();

        val originalFile:File = File(FilePathStr)

//appending image to firebase
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("travel/${originalFile.name}${Random.nextInt(0, 50)}${Random.nextDouble(4.00, 99.9)}")
        val upload = imageRef.putFile(uri)

        upload
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Couldn't upload image to firebase", Toast.LENGTH_LONG ).show()
            }.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { url->

                   if (_binding !=null) {
                        uploadPostToServer(url.toString())
                    }else{
                        Log.d("statuss", "you where?")
                   }


                }
            }
    }

    }
//upload to ktor server
    private fun uploadPostToServer(url:String) {



        val postPart:CreatePost =CreatePost(binding.editTextPostDescription.text.toString(),url)

        viewModel.createPost(token = "Bearer ${userToken.toString()}",post=postPart)

        viewModel.createPostResponse.observe(viewLifecycleOwner, {response ->
            requireActivity().runOnUiThread(java.lang.Runnable {
                binding.uploadPostAnimation.visibility = View.GONE
            })

            if(response.isSuccessful){

                if(response.body()!!.success){
                    Toast.makeText(context, "Post Uploaded", Toast.LENGTH_LONG).show()
                    (activity as MainActivity).replaceCurrentFragment(FeedScreen())

                }else{
                    Toast.makeText(context, "couldn't upload post" , Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.d("uploadPostToServerFun", response?.errorBody().toString())
            }



        })
    }




    //controls the animations of the Floating buttons

}
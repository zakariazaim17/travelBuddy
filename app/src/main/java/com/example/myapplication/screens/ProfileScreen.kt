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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentProfileScreenBinding
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.model.CreatePost
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.model.GetUserProfileResponse
import com.example.myapplication.model.udpateUserProfile
import com.example.myapplication.repository.Repository
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

private var _binding: FragmentProfileScreenBinding? = null
private val binding get() = _binding!!
private var userToken:String? = null
private var listOfPosts:List<GetAllPostResponse>? = null
private var userId :String ?= null
private lateinit var viewModel: MainViewModel
private val GALLERY_REQUEST_CODE = 300
private var ImageUri: Uri?= null
private var isUserProfileImageUpdated:Boolean ?= null
private var currentUser: GetUserProfileResponse ?= null

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
        val editor:SharedPreferences.Editor = sharedPreferences.edit();
        userToken = sharedPreferences.getString("User_token",null);
        userId = sharedPreferences.getString("User_Id", null)

        val layoutManager: LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.ownUserPostsRecyclerView.layoutManager = layoutManager

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.parentEmptyPostContainer.visibility = View.GONE

        binding.ownUserPostsRecyclerView.visibility = View.GONE
        binding.parentUpdatingDialogButtons.visibility = View.GONE


        viewModel.getUserProfile(token = "Bearer ${userToken.toString()}", userId = userId!!)
        viewModel.getOwnUserPosts(token = "Bearer ${userToken.toString()}")

        viewModel.getUserProfileResponse.observe(viewLifecycleOwner , {response ->
            if (response.isSuccessful){

                if (response.body()?.success == true){
                    Log.d("successful Call", response.body().toString() )

                    currentUser = response.body()

                    if (response.body()?.data?.bio!! == "") {

                        binding.userBioEditText.setText(response.body()?.data?.bio)
                    }

                    if(response.body()?.data?.profilePictureUrl!!.isNullOrEmpty()) {

                        binding.userProfileImageImageView.load("https://images.pexels.com/photos/3464632/pexels-photo-3464632.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260"){
                            crossfade(true)
                            crossfade(100)
                            transformations(CircleCropTransformation())
                        }

                    }else{
                        binding.userProfileImageImageView.load(response.body()?.data?.profilePictureUrl){
                            crossfade(true)
                            crossfade(100)
                            transformations(CircleCropTransformation())
                        }
                    }


                    binding.updateUserInfoButton.visibility = View.VISIBLE

                    binding.userProfileImageImageView.isEnabled = false
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
                    binding.ownUserPostsRecyclerView.visibility =View.GONE
                    binding.parentEmptyPostContainer.visibility = View.VISIBLE
                    Log.d("profilerecycler", "No Post found for this user")
                }
            }else{
                Log.d("some error in calling", response.errorBody().toString())
            }
        })



        binding.updateUserInfoButton.setOnClickListener {
            binding.usernameEditText.isEnabled = true
            binding.userBioEditText.isEnabled = true
            binding.userProfileImageImageView.isEnabled = true
            binding.parentUpdatingDialogButtons.visibility = View.VISIBLE
            binding.updateUserInfoButton.visibility =View.GONE


        }

        binding.userProfileImageImageView.setOnClickListener {

            openGalleryForImage()

        }

        binding.ConfirmUpdateBtn.setOnClickListener {
            //if (binding.userBioEditText.text.trim().isNotEmpty() &&  binding.usernameEditText.text.trim().isNotEmpty())  -------------- continue here ><<<<<<<<
       if (isUserProfileImageUpdated == true){
           uploadImageToFirebase(ImageUri!!)
       }else{
           uploadPostToServer(currentUser?.data?.profilePictureUrl!!)
       }
        }

        binding.CancelUpdateBtn.setOnClickListener {

            binding.userProfileImageImageView.load(currentUser?.data?.profilePictureUrl!!){
                crossfade(true)
                crossfade(100)
                transformations(CircleCropTransformation())
            }
            binding.userBioEditText.setText(currentUser?.data?.bio!!)
            binding.usernameEditText.setText(currentUser?.data?.username!!)
            binding.usernameEditText.isEnabled = false
            binding.userBioEditText.isEnabled = false
            binding.userProfileImageImageView.isEnabled = false
            binding.updateUserInfoButton.visibility = View.VISIBLE
            binding.parentUpdatingDialogButtons.visibility =View.GONE
        }

        binding.logoutButton.setOnClickListener {

            editor.putString("User_token", null)
                .apply()
            editor.putString("User_Id", null).apply()
            editor.putBoolean("isLoggedIn", false).apply()

            (activity as MainActivity).replaceCurrentFragment(SplashScreen())
        }

    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST_CODE && data != null) {
            binding?.userProfileImageImageView.setImageURI(data?.data)
            binding.userProfileImageImageView.load(data.data){
                crossfade(true)
                crossfade(100)
                transformations(CircleCropTransformation())
            }
            // handle chosen image
            ImageUri = data?.data
            isUserProfileImageUpdated = true

        }
    }

    //To upload post
    private fun uploadImageToFirebase(uri:Uri){







            Log.d("pic", uri.toString())


            var filePath:Array<String> = arrayOf(MediaStore.Images.Media.DATA)
            var c: Cursor? = requireActivity().contentResolver.query(
                uri!!, filePath,
                null, null, null);
            c?.moveToFirst();
            var columnIndex:Int = c!!.getColumnIndex(filePath[0]);
            val FilePathStr = c.getString(columnIndex);
            Log.d("mm", FilePathStr)
            c.close();

            val originalFile: File = File(FilePathStr)

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

    private fun uploadPostToServer(url:String) {




        val userUpdate: udpateUserProfile = udpateUserProfile(username = binding.usernameEditText.text.toString(), profileImageUrl = url, bio = binding.userBioEditText.text.toString(), hobbies = currentUser?.data?.hobbies!!)

        viewModel.updateUserProfile(token = "Bearer ${userToken.toString()}",updateUser = userUpdate)

        viewModel.getUpdateUserProfileResponse.observe(viewLifecycleOwner, {response ->


            if(response.isSuccessful){

                if(response.body()!!.success){
                    Toast.makeText(context, "Info updated", Toast.LENGTH_LONG).show()
                    binding.parentUpdatingDialogButtons.visibility = View.GONE
                    binding.updateUserInfoButton.visibility = View.VISIBLE
                    binding.userBioEditText.isEnabled = false
                    binding.usernameEditText.isEnabled = false
                    binding.userProfileImageImageView.isEnabled = false
                    isUserProfileImageUpdated = false

                }else{
                    Toast.makeText(context, "couldn't update info" , Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.d("uploadPostToServerFun", response?.errorBody().toString())
            }



        })
    }





}
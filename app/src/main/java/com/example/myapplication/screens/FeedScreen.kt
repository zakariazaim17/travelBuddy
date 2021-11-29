package com.example.myapplication.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentFeedScreenBinding
import com.example.myapplication.model.CreateComment
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.repository.Repository
import okhttp3.internal.concurrent.formatDuration
import android.app.Activity
import android.app.Application
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService





private lateinit var viewModel: MainViewModel
private var _binding:FragmentFeedScreenBinding? = null
private val binding get()= _binding!!
private var userToken:String? = null
private var listOfPosts:List<GetAllPostResponse>? = null


class FeedScreen : Fragment() {

    private var isFabOpen :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun controlSubFab (){
        Log.d("floatingBtn", isFabOpen.toString())
        if(!isFabOpen){

            binding.addPlanFloatingBtn.show()
            binding.addPostFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_55))
            binding.addPostFloatingBtn.show()
            binding.addPostFloatingBtn.extend()
            binding.addPlanFloatingBtn.animate().translationY(-resources.getDimension(R.dimen.standard_105))
            binding.addPlanFloatingBtn.extend()
            isFabOpen = true
        }else{
            binding.addPlanFloatingBtn.shrink()
            binding.addPostFloatingBtn.shrink()
            binding.addPostFloatingBtn.animate().translationY(0F)
            binding.addPlanFloatingBtn.animate().translationY(0F)

            binding.addPlanFloatingBtn.hide()
            binding.addPostFloatingBtn.hide()
            isFabOpen = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedScreenBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public fun showExpandableImage(url:String){
        binding.expandableImageImageView.load(url){
            formatDuration(200)
        }
        binding.expandableImageImageView.visibility = View.VISIBLE
    }



    public fun closeExpandableImage(){
        binding.expandableImageImageView.visibility = View.GONE
    }

    fun hideKeyboard(view: View, contexti:Context) {

        val inputMethodManager: InputMethodManager? =
            contexti.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?

        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)

    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)


        userToken = sharedPreferences.getString("User_token",null);

        binding.addPlanFloatingBtn.shrink()
        binding.addPostFloatingBtn.shrink()
        binding.addPlanFloatingBtn.visibility = View.GONE
        binding.addPostFloatingBtn.visibility = View.GONE
        binding.emptyPostAnimation.visibility = View.GONE
        binding.textEmptyStateTextview.visibility = View.GONE
        binding.expandableImageImageView.visibility = View.GONE


        val layoutManager:LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.postsRecyclerView.layoutManager = layoutManager


        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.getAllPosts(token = "Bearer ${userToken.toString()}")


        viewModel.getAllPostsResponse.observe(viewLifecycleOwner, {response ->
            if (response.isSuccessful){
                if (response.body()?.size!! > 0 ){
                    binding.loadingPostAnimation.visibility = View.GONE

                    binding.emptyPostAnimation.visibility = View.GONE
                    binding.textEmptyStateTextview.visibility = View.GONE
                    listOfPosts = response.body()
                    Log.d("fetchPosts", response.body().toString())
                        binding.postsRecyclerView.adapter = Adapter(listOfPosts!!, requireContext(), viewModel)

                }else{
                    binding.emptyPostAnimation.visibility = View.VISIBLE
                    binding.textEmptyStateTextview.visibility = View.VISIBLE

                    Log.d("fetchPosts", "List is empty???")
                }

            }else{

                Log.d("fetchPosts", response.errorBody().toString())

            }
        })




        binding.addFloatingBtn.setOnClickListener {
            Log.d("floatingBtn", isFabOpen.toString())
            controlSubFab()
        }
        binding.addPostFloatingBtn.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(AddPostScreen())
        }
        binding.addPlanFloatingBtn.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(AddEventScreen())

        }

        binding.expandableImageImageView.setOnClickListener {
            closeExpandableImage()
        }

    }

    fun postComment(comment: CreateComment):Boolean {
        viewModel.createPostcomment(token = "Bearer ${userToken.toString()}",comment )
        var responseResult:Boolean = false
        viewModel.createCommentResponse.observe(viewLifecycleOwner, {response ->
            if (response.isSuccessful){
                responseResult = response.body()?.success!!
            }else{Log.d("postingComment", response.errorBody().toString())}
        })
        return responseResult
    }


}
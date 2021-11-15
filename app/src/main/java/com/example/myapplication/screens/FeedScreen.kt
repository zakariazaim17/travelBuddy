package com.example.myapplication.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFeedScreenBinding



private var _binding:FragmentFeedScreenBinding? = null
private val binding get()= _binding!!

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPlanFloatingBtn.shrink()
        binding.addPostFloatingBtn.shrink()
        binding.addPlanFloatingBtn.visibility = View.GONE
        binding.addPostFloatingBtn.visibility = View.GONE

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

    }


}
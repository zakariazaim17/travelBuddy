package com.example.myapplication.screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_feed_screen.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FeedScreen : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var isFabOpen :Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private fun controlSubFab (){
        Log.d("floatingBtn", isFabOpen.toString())
        if(!isFabOpen){

       addPlan_Floating_Btn.show()
            addPost_Floating_Btn.animate().translationY(-resources.getDimension(R.dimen.standard_55))
            addPost_Floating_Btn.show()
            addPost_Floating_Btn.extend()
            addPlan_Floating_Btn.animate().translationY(-resources.getDimension(R.dimen.standard_105))
            addPlan_Floating_Btn.extend()
            isFabOpen = true
        }else{
            addPlan_Floating_Btn.shrink()
            addPost_Floating_Btn.shrink()
            addPost_Floating_Btn.animate().translationY(0F)
            addPlan_Floating_Btn.animate().translationY(0F)

            addPlan_Floating_Btn.hide()
            addPost_Floating_Btn.hide()
            isFabOpen = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPlan_Floating_Btn.shrink()
        addPost_Floating_Btn.shrink()
        addPlan_Floating_Btn.visibility = View.GONE
        addPost_Floating_Btn.visibility = View.GONE

        add_Floating_Btn.setOnClickListener {
            Log.d("floatingBtn", isFabOpen.toString())
            controlSubFab()
        }
        addPost_Floating_Btn.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(AddPostScreen())
        }
        addPlan_Floating_Btn.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(AddEventScreen())

        }

    }


}
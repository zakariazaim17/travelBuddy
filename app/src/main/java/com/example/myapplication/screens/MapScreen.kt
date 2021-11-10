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
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.R
import com.example.myapplication.model.Follow
import com.example.myapplication.model.Unfollow
import com.example.myapplication.repository.Repository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map_screen.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Boolean.getBoolean

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MapScreen : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private  lateinit var mMap :GoogleMap
    private var mapReady = false
    private var isFabOpen :Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


            val rootView =  inflater.inflate(R.layout.fragment_map_screen, container, false)

return  rootView




    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val userToken: String? = sharedPreferences.getString("User_token",null);



        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)


        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
val users = Follow("6179b4e15c5c4430042c5319", "61850139a6745124930ac2d5")
        travelBtn.setOnClickListener {
viewModel.followUser(token = "Bearer ${userToken.toString()}",users)
            viewModel.followResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    if(!response.body()!!.success){
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    }else{
                        //save token for later use

                        Toast.makeText(context, "follow successfully" , Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).replaceCurrentFragment((MapScreen()))
                    }
                    Log.d("Main1", response.body().toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())
                }else{
                    Log.d("Main1", response.errorBody().toString())
                }

            })
        }

        eventBtn.setOnClickListener {
            val users1 = Unfollow("61850139a6745124930ac2d5", "61850304a6745124930ac2d6")

            viewModel.unfollowUser(token = "Bearer ${userToken.toString()}",users1)
            viewModel.unfollowResponse.observe(viewLifecycleOwner, {response ->
                if (response.isSuccessful){
                    if(!response.body()!!.success) {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()


                    } else{
                        Toast.makeText(context, "successfully unfollow", Toast.LENGTH_LONG).show()
                    }
                    Log.d("Main2", response.body().toString())
                    Log.d("Main2", response.code().toString())
                    Log.d("Main2", response.message())
                }else{
                    Log.d("Main2", response.errorBody().toString())

                }
            })
        }


    }

    override fun onMapReady(googleMap: GoogleMap)  {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val mapAnimated:Boolean? = sharedPreferences.getBoolean("mapAnimated", false)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();
       // mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
 fun initializeMap() {

        mMap = googleMap!!
        val sydney = LatLng(60.1699, 24.9384)
        val cameraPosition = CameraPosition.Builder()
            .target(sydney) // Sets the center of the map to Mountain View
            .zoom(13f)            // Sets the zoom
            .bearing(10f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()
        mMap.uiSettings.isCompassEnabled = false
           if(mapAnimated == false){
               Log.d("anim", mapAnimated.toString())
               mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null)
               editor.apply {
                   putBoolean("mapAnimated", true)
               }.apply()
           }else{

               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13F))
           }


    }
        initializeMap()


}











      //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F), 4000, null )
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F))
       // mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        //mMap.setMaxZoomPreference(14.0f)


}
package com.example.myapplication.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMapScreenBinding
import com.example.myapplication.databinding.FragmentProfileScreenBinding
import com.example.myapplication.model.CreatePost
import com.example.myapplication.model.Follow
import com.example.myapplication.model.Unfollow
import com.example.myapplication.repository.Repository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Boolean.getBoolean
import java.util.jar.Manifest

private var _binding: FragmentMapScreenBinding? = null
private val binding get() = _binding!!

class MapScreen : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext());


    }

    private  lateinit var mMap :GoogleMap
    private var mapReady = false
    private var isFabOpen :Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentMapScreenBinding.inflate(inflater, container, false)


        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return  binding.root




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val userToken: String? = sharedPreferences.getString("User_token",null);



        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

       binding.sportBtn.setOnClickListener {
            Log.d("sport", "sport")
        }


val users = Follow("6179b4e15c5c4430042c5319", "61850139a6745124930ac2d5")
        binding.travelBtn.setOnClickListener {
viewModel.followUser(token = "Bearer ${userToken.toString()}",users)
            viewModel.followResponse.observe(viewLifecycleOwner, {response ->
                if(response.isSuccessful){
                    if(!response.body()!!.success){
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    }else{
                        //save token for later use

                        Toast.makeText(context, "follow successfully" , Toast.LENGTH_SHORT).show()
                        //(activity as MainActivity).replaceCurrentFragment((MapScreen()))
                    }
                    Log.d("Main1", response.body().toString())
                    Log.d("Main1", response.code().toString())
                    Log.d("Main1", response.message())
                }else{
                    Log.d("Main1", response.errorBody().toString())
                }

            })
        }

        binding.eventBtn.setOnClickListener {
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


        mMap = googleMap!!

        val helsinki = LatLng(60.1699, 24.9384)
        val cameraPosition = CameraPosition.Builder()
            .target(helsinki) // Sets the center of the map to Mountain View
            .zoom(13f)            // Sets the zoom
            .bearing(10f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()
        mMap.uiSettings.isCompassEnabled = false


           /*
           if (ContextCompat.checkSelfPermission(requireContext(),
                   android.Manifest.permission.ACCESS_FINE_LOCATION)
               == PackageManager.PERMISSION_GRANTED) {
               locationPermissionGranted = true
           } else {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                   PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
           }

            mMap.isMyLocationEnabled = true

           mMap.uiSettings.isMyLocationButtonEnabled = true
            */
           if(mapAnimated == false){
               Log.d("anim", mapAnimated.toString())
               mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null)
               editor.apply {
                   putBoolean("mapAnimated", true)
               }.apply()
           }else{

               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(helsinki, 13F))
           }





}











      //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F), 4000, null )
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F))
       // mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        //mMap.setMaxZoomPreference(14.0f)


}
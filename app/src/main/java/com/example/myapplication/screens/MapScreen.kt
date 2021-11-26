package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.*
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMapScreenBinding
import java.util.*


import com.example.myapplication.model.CreatePost
import com.example.myapplication.model.Follow
import com.example.myapplication.model.Unfollow
import com.example.myapplication.repository.Repository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Boolean.getBoolean
import java.util.jar.Manifest
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import android.graphics.drawable.Drawable
import com.example.myapplication.utils.Constants

import com.google.android.gms.maps.model.BitmapDescriptor




private var _binding: FragmentMapScreenBinding? = null
private val binding get() = _binding!!
private var geocode: Geocoder ?= null
private  lateinit var mMap :GoogleMap
private var userToken:String?= null
class MapScreen : Fragment(), OnMapReadyCallback {

    private val markObj = object:GoogleMap.OnMarkerDragListener {


        override fun onMarkerDrag(p0: Marker) {
            Log.d("ggg", "started")
        }

        override fun onMarkerDragEnd(p0: Marker) {
            Log.d("ggg", p0.position.toString())
        }

        override fun onMarkerDragStart(p0: Marker) {
            Log.d("ggg", "sttt")
        }

    }

    private var helsinki = LatLng(60.1699, 24.9384)

    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext());
geocode = Geocoder(requireContext())

    }


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
         userToken = sharedPreferences.getString("User_token",null);

        val layoutManager:LinearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.plansRecyclerView.layoutManager = layoutManager

        binding.plansRecyclerView.visibility = View.GONE

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

       binding.sportBtn.setOnClickListener {
           filterPlans(Constants.SPORT)

        }

        binding.travelBtn.setOnClickListener {
            filterPlans(Constants.TRIP)

        }

        binding.eventBtn.setOnClickListener {
            filterPlans(Constants.EVENT)

        }


    }

    private fun filterPlans(category:String){

        viewModel.getCurrentUserPlans(token = "Bearer ${userToken.toString()}", category = category, null)
        viewModel.getCurrentUserPlansResponse.observe(viewLifecycleOwner, {response->
            if (response.isSuccessful){
                if (response.body()?.size!! > 0){
                    binding.plansRecyclerView.visibility = View.VISIBLE
                    when(category){
                         "sport" -> binding.plansRecyclerView.adapter = SportPlansAdapter(response.body()!!)
                        "event" -> binding.plansRecyclerView.adapter = EventPlansAdapter(response.body()!!)
                        "trip" ->binding.plansRecyclerView.adapter = TripPlansAdapter(response.body()!!)
                    }

                }else{
                    binding.plansRecyclerView.visibility = View.GONE
                    Log.d("fetchingPlans", "seems user doesn't have plans")
                }
            }else{
                Log.d("fetchingPlans", response.errorBody().toString())
            }
        })
    }

    public fun navigateBetweenPlans(location:LatLng){
        val helsinki = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(helsinki) // Sets the center of the map to Mountain View
            .zoom(13f)// Sets the zoom
            // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()

        mMap.clear()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null)
       // val icon :BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_fire)
        mMap.addMarker(MarkerOptions().position(helsinki))
    }

    override fun onMapReady(googleMap: GoogleMap)  {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val mapAnimated:Boolean? = sharedPreferences.getBoolean("mapAnimated", false)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();
       // mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))


        mMap = googleMap!!


        val cameraPosition = CameraPosition.Builder()
            .target(helsinki) // Sets the center of the map to Mountain View
            .zoom(13f)            // Sets the zoom
            .bearing(10f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()
        mMap.uiSettings.isCompassEnabled = false
        mMap.addMarker(MarkerOptions().position(helsinki))





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




    /*override fun onMarkerDrag(p0: Marker) {
        TODO("Not yet implemented")
    }

    override fun onMarkerDragEnd(p0: Marker) {
        Log.d("MarkerUpdd", p0.position.toString() )
    }

    override fun onMarkerDragStart(p0: Marker) {
        TODO("Not yet implemented")
    }*/


    //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F), 4000, null )
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12F))
       // mMap.moveCamera(CameraUpdateFactory.zoomTo(15F))
        //mMap.setMaxZoomPreference(14.0f)


}

/*
helsinki = LatLng( 60.20759890000001, 24.9668617)
           val cameraPosition = CameraPosition.Builder()
               .target(helsinki) // Sets the center of the map to Mountain View
               .zoom(13f)// Sets the zoom
                        // Sets the orientation of the camera to east
               .tilt(30f)            // Sets the tilt of the camera to 30 degrees
               .build()
           mMap.clear()
           mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null)
           mMap.addMarker(MarkerOptions().position(helsinki).draggable(true))
        val listOfAddress:List<Address> =
            geocode?.getFromLocationName("Bengalinpolku 1E17,00560,Helsinki",1) as List<Address>
           val adreess:Address = listOfAddress.get(0)
           Log.d("whatAddress", "${adreess.longitude} && ${adreess.latitude}")


           /*mMap.setOnMarkerClickListener( GoogleMap.OnMarkerDragListener {
               @Override fun onMarkerDragEnd(Marker marker){
                    Log.d("markerUpdate", marker.)
               }
           })*/

mMap.setOnMarkerDragListener(markObj)
 */
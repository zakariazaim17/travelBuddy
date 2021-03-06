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

    private var isFabOpen :Boolean = false

    private val markObj = object:GoogleMap.OnMarkerDragListener {


        override fun onMarkerDrag(p0: Marker) {
            Log.d("marker", "draging")
        }

        override fun onMarkerDragEnd(p0: Marker) {
            Log.d("marker", p0.position.toString())
        }

        override fun onMarkerDragStart(p0: Marker) {
            Log.d("marker", "started draging")
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

        binding.searchAnimation?.visibility = View.GONE



        // manage floating Buttons
        binding.planCategoryFloatingBtn?.extend()

        binding.eventBtnFloatingButton?.shrink()
        binding.eventBtnFloatingButton?.visibility = View.GONE

        binding.travelBtnFloatingButton?.shrink()
        binding.travelBtnFloatingButton?.visibility = View.GONE

        binding.sportBtnFloatingButton?.shrink()
        binding.sportBtnFloatingButton?.visibility = View.GONE

        binding.addPlanButton?.visibility = View.GONE

        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.addPlanButton?.setOnClickListener {
            (activity as MainActivity).replaceCurrentFragment(AddEventScreen())
        }
        binding.planCategoryFloatingBtn?.setOnClickListener {
            controlSubFab()
        }
       binding.sportBtnFloatingButton?.setOnClickListener {
           controlSubFab()
           filterPlans(Constants.SPORT)
        }

        binding.travelBtnFloatingButton?.setOnClickListener {
            controlSubFab()
            filterPlans(Constants.TRIP)
        }

        binding.eventBtnFloatingButton?.setOnClickListener {
            controlSubFab()
            filterPlans(Constants.EVENT)
        }




    }

    private fun controlSubFab (){
        Log.d("floatingBtn", isFabOpen.toString())
        if(!isFabOpen){
            binding.planCategoryFloatingBtn?.shrink()

            binding.sportBtnFloatingButton?.extend()
            binding.sportBtnFloatingButton?.animate()?.translationX(resources.getDimension(R.dimen.standard_105))
            binding.sportBtnFloatingButton?.show()

            binding.travelBtnFloatingButton?.show()
            binding.travelBtnFloatingButton?.extend()
            binding.travelBtnFloatingButton?.animate()?.translationX(resources.getDimension(R.dimen.standard_55))
            binding.travelBtnFloatingButton?.animate()?.translationY(resources.getDimension(R.dimen.standard_55))

            binding.eventBtnFloatingButton?.show()
            binding.eventBtnFloatingButton?.extend()
            binding.eventBtnFloatingButton?.animate()?.translationY(resources.getDimension(R.dimen.standard_105))


            isFabOpen = true

        }else{



            binding.sportBtnFloatingButton?.shrink()
            binding.sportBtnFloatingButton?.animate()?.translationX(-resources.getDimension(R.dimen.standard_105))
            binding.sportBtnFloatingButton?.hide()

            binding.travelBtnFloatingButton?.shrink()
            binding.travelBtnFloatingButton?.animate()?.translationX(-resources.getDimension(R.dimen.standard_105))
            binding.travelBtnFloatingButton?.animate()?.translationY(-resources.getDimension(R.dimen.standard_105))
            binding.travelBtnFloatingButton?.hide()

            binding.eventBtnFloatingButton?.shrink()
            binding.eventBtnFloatingButton?.animate()?.translationY(-resources.getDimension(R.dimen.standard_105))
            binding.eventBtnFloatingButton?.hide()

            binding.planCategoryFloatingBtn?.extend()

            isFabOpen = false

        }
    }

    private fun filterPlans(category:String){
        binding.searchAnimation?.visibility = View.VISIBLE

        binding.plansRecyclerView.visibility = View.GONE
        viewModel.getCurrentUserPlans(token = "Bearer ${userToken.toString()}", category = category, null)
        viewModel.getCurrentUserPlansResponse.observe(viewLifecycleOwner, {response->
            if (response.isSuccessful){
                if (response.body()?.size!! > 0){
                    binding.searchAnimation?.visibility = View.GONE

                    binding.plansRecyclerView.visibility = View.VISIBLE
                    when(category){
                         "sport" -> binding.plansRecyclerView.adapter = SportPlansAdapter(response.body()!!)
                        "event" -> binding.plansRecyclerView.adapter = EventPlansAdapter(response.body()!!)
                        "trip" ->binding.plansRecyclerView.adapter = TripPlansAdapter(response.body()!!)
                    }

                }else{
                    binding.plansRecyclerView.visibility = View.GONE
                    Toast.makeText(requireContext(), "No plans Found!", Toast.LENGTH_LONG).show()
                    Log.d("fetchingPlans", "seems user doesn't have plans")
                }
            }else{
                Toast.makeText(requireContext(), "Some error occurred, please try later",Toast.LENGTH_SHORT).show()
                Log.d("fetchingPlans", response.errorBody().toString())
            }
            binding.addPlanButton?.visibility = View.VISIBLE

        })
    }

    public fun navigateBetweenPlans(location:LatLng, title:String){
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
        mMap.addMarker(
            MarkerOptions()
                .position(helsinki)
                .title(title))

    }

    override fun onMapReady(googleMap: GoogleMap)  {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val mapAnimated:Boolean? = sharedPreferences.getBoolean("mapAnimated", false)
        val editor:SharedPreferences.Editor = sharedPreferences.edit();



        mMap = googleMap!!


        val cameraPosition = CameraPosition.Builder()
            .target(helsinki) // Sets the center of the map to Mountain View
            .zoom(13f)            // Sets the zoom
            .bearing(10f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()
        mMap.uiSettings.isCompassEnabled = false


           /*

           //realtime permission checking testing

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

}


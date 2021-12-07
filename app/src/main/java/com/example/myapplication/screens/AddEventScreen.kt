package com.example.myapplication.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.myapplication.databinding.FragmentAddEventScreenBinding

import com.example.myapplication.utils.Constants
import android.location.Address
import android.location.Geocoder
import android.text.Editable

import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.MainActivity
import com.example.myapplication.MainViewModel
import com.example.myapplication.MainViewModelFactory
import com.example.myapplication.model.CreatePlanRequest
import com.example.myapplication.repository.Repository
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

private var geocode: Geocoder?= null
private  lateinit var mMap :GoogleMap
private var _binding: FragmentAddEventScreenBinding? = null
private val binding get()= _binding!!
private val subSportData = arrayOf(Constants.BASKETBALL, Constants.FOOTBALL, Constants.BADMINTON, Constants.HOKEY, Constants.TENNIS, Constants.SKATING, Constants.OTHER)
private val subTripData = arrayOf(Constants.AIR, Constants.BUS, Constants.FERRY,Constants.OTHER)
private val subEventData = arrayOf(Constants.ART, Constants.MOVIE, Constants.MUSIC, Constants.THEATER,Constants.VIDEO_GAMES, Constants.OTHER)
private var planCategory:String ?= null
private var subCategory:String ?= null
private var planDate:String ?= null
private var planTime:String ?= null
private var selectedLocationType :String ?= null
private var selectedLocation :String ?= null
private var userToken:String?= null
private val geocoder:Geocoder ?= null
private var tempMapLocation : LatLng ?= null
private var userId:String ?= null
private var isProfileCompleted: Boolean ?= false
class AddEventScreen : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainViewModel

    private val markObj = object:GoogleMap.OnMarkerDragListener {


        override fun onMarkerDrag(p0: Marker) {
            Log.d("ggg", "started")
        }

        override fun onMarkerDragEnd(p0: Marker) {
            Log.d("ggg", p0.position.toString())
            selectedLocation = "${p0.position.latitude},${p0.position.longitude}"
            tempMapLocation = LatLng(p0.position.latitude,p0.position.longitude)

        }

        override fun onMarkerDragStart(p0: Marker) {
            Log.d("ggg", "sttt")
        }

    }

    private var helsinki = LatLng(60.1699, 24.9384)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(requireContext());
        geocode = Geocoder(requireContext())

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEventScreenBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(com.example.myapplication.R.id.planMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        userToken = sharedPreferences.getString("User_token",null);
        userId = sharedPreferences.getString("User_Id", null)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.getUserProfile(token = "Bearer ${userToken.toString()}", userId = userId!!)

        viewModel.getUserProfileResponse.observe(viewLifecycleOwner , {response ->
            if (response.isSuccessful){
                if (response.body()?.data?.profilePictureUrl.isNullOrEmpty()){
                    Log.d("successful Call", response.body().toString() )
                    isProfileCompleted = false
                    Toast.makeText(requireContext(), "\uD83D\uDCCC Please complete your Profile before posting", Toast.LENGTH_LONG).show()
                    }else {
                    isProfileCompleted = true
                }
                }
            else{
                Log.d("some error in calling", response.errorBody().toString())

                //this will be by default to prevent sudden break down
                isProfileCompleted = true
            }
        })

        binding.addressLocationEditText.visibility = View.GONE
        binding.addressLocationFromEditText?.visibility = View.GONE

        binding.resultPlanDateTextView.visibility = View.GONE
        binding.resultPlanTimeTextView.visibility = View.GONE


        binding.tripRadioButton.text = Constants.TRIP
        binding.eventRadioButton.text = Constants.EVENT
        binding.sportRadioButton.text = Constants.SPORT

       binding.parentLayout?.setOnClickListener {
           FeedScreen().hideKeyboard(binding.editTextPostDescription, requireContext())

       }

        binding.editTextPostDescription.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    FeedScreen().hideKeyboard(v, requireContext())

                }
            }


        binding.subCategoryPicker.visibility = View.GONE

        binding.parentMapContainer.visibility = View.GONE
       // binding.confirmPlanLocationButton.visibility = View.GONE
      //  binding.cancelLocationButton.visibility = View.GONE
        binding.uploadPostAnimation.visibility = View.GONE



        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()


        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(10)
            .setTitleText("Select plan timing")
            .build()



        binding.subCategoryPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        binding.setPlanDateTextView.setOnClickListener {
        fragmentManager?.let { it -> datePicker.show(it,"selectPlanDate") }
        //binding.datePicker.visibility = View.VISIBLE
        }


        // ----------> after selecting date

        datePicker.addOnPositiveButtonClickListener {
var pickedDate = convertTimeLongToTime(datePicker.selection!!)
            Log.d("whatDate", pickedDate)

            planDate = pickedDate
            binding.resultPlanDateTextView.visibility = View.VISIBLE
            binding.resultPlanDateTextView.text = planDate

        }

        // ---------------> show timePicker
        binding.setPlanTimeTextView.setOnClickListener {

            fragmentManager?.let { it -> timePicker.show(it,"selectPlanDate")}
        }

        // ----------> after selecting time
        timePicker.addOnPositiveButtonClickListener {

            Log.d("whatTime", "${timePicker.hour}:${timePicker.minute}")
            planTime = "${timePicker.hour}:${timePicker.minute}"

            binding.resultPlanTimeTextView.visibility = View.VISIBLE
            binding.resultPlanTimeTextView.text = planTime

        }

        // --------------> canceling in map
        binding.cancelLocationButton.setOnClickListener {
            binding.parentMapContainer.visibility = View.GONE
            binding.addressLocationEditText.visibility = View.GONE
            selectedLocation = null
        }

        // ---------> confirming map
        binding.confirmPlanLocationButton.setOnClickListener {

            binding.parentMapContainer.visibility = View.GONE
           // binding.addressLocationEditText.setText(convertLatLongToAddress(tempMapLocation!!))
        binding.addressLocationEditText.setText("Location Set ✔️")
            binding.addressLocationEditText.visibility = View.VISIBLE

            binding.addressLocationEditText.isEnabled = false

        }


        // -------------------> plan Location Radio
binding.planLocationRadioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener{ group, checkedId ->

    val locationRadiogroup = group.findViewById(checkedId) as RadioButton
    selectedLocationType = locationRadiogroup.text.toString()
    Log.d("selectedLocationType", selectedLocationType.toString())

    when(selectedLocationType){
        "Address Location" -> {
            binding.addressLocationEditText.isEnabled = true
            if (planCategory !== null && planCategory == Constants.TRIP) {
            binding.addressLocationEditText.visibility = View.VISIBLE
               // binding.addressLocationFromEditText?.visibility = View.VISIBLE
        }else if(planCategory == null){
            Toast.makeText(requireContext(), "Please select your Event Category beforehand", Toast.LENGTH_LONG).show()
        }else{
                binding.addressLocationEditText.visibility = View.VISIBLE
        }}
            else ->{
                if (planCategory == null){
                    Toast.makeText(requireContext(), "Please select your Event Category beforehand", Toast.LENGTH_LONG).show()
                }
                else {
                    //binding.addressLocationFromEditText?.visibility = View.VISIBLE
                    binding.parentMapContainer.visibility = View.VISIBLE
                    binding.addressLocationEditText.visibility = View.VISIBLE


                }

            }
    }

})
        // ------> Plan Type radio

        binding.radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->


            //binding.subCategoryPicker.visibility = View.GONE
            var radioButton = group.findViewById(checkedId) as RadioButton



            planCategory = radioButton.text.toString()
            binding.subCategoryPicker.visibility = View.VISIBLE


            when(planCategory){
                Constants.TRIP ->{
                    binding.subCategoryPicker.clearFocus()
                    //binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.displayedValues = null
                    binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.maxValue = subTripData.size -1
                    binding.subCategoryPicker.displayedValues = subTripData


                    binding.subCategoryPicker.visibility = View.VISIBLE}
                Constants.EVENT ->{
                    binding.subCategoryPicker.clearFocus()
                    binding.subCategoryPicker.visibility = View.GONE
                    //binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.displayedValues = null
                    binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.maxValue = subEventData.size -1
                    binding.subCategoryPicker.displayedValues = subEventData


                    binding.subCategoryPicker.visibility = View.VISIBLE}
                Constants.SPORT ->{
                    binding.subCategoryPicker.clearFocus()
                    binding.subCategoryPicker.visibility = View.GONE
                    // binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.displayedValues = null
                    binding.subCategoryPicker.maxValue = subSportData.size
                    binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.displayedValues = subSportData

                    binding.subCategoryPicker.visibility = View.VISIBLE}
            }


            Log.d("whaatRadi", radioButton.text.toString())
        })


        // ------> subCategory picker

        binding.subCategoryTextView.setOnClickListener {


            when(planCategory){
                Constants.TRIP ->{
                    binding.subCategoryPicker.clearFocus()
                    //binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.displayedValues = null
                    binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.maxValue = subTripData.size -1
                    binding.subCategoryPicker.displayedValues = subTripData


                    binding.subCategoryPicker.visibility = View.VISIBLE}
                Constants.EVENT ->{
                    binding.subCategoryPicker.clearFocus()
                    binding.subCategoryPicker.visibility = View.GONE
                    //binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.displayedValues = null
                    binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.maxValue = subEventData.size -1
                    binding.subCategoryPicker.displayedValues = subEventData


                    binding.subCategoryPicker.visibility = View.VISIBLE}

                Constants.SPORT -> {
                    binding.subCategoryPicker.clearFocus()
                    binding.subCategoryPicker.visibility = View.GONE
                   // binding.subCategoryPicker.minValue = 1
                    binding.subCategoryPicker.displayedValues = null

                    binding.subCategoryPicker.minValue = 0
                    binding.subCategoryPicker.maxValue = subSportData.size -1
                    binding.subCategoryPicker.displayedValues = subSportData

                    binding.subCategoryPicker.visibility = View.VISIBLE
                }
            }


        }


        // ----------------> Upload Btn
        binding.buttonUploadPost.setOnClickListener {
            binding.uploadPostAnimation.visibility = View.VISIBLE

            Log.d("whatselectedLocPlan", selectedLocationType.toString())
            if (isProfileCompleted!!){


                if(inspectCheckedPlanType()){
                    Log.d("hmsm", inspectCheckedPlanType().toString())
                    /* if(selectedLocationType == "Address Location") {
                         geocodeAddressLocation(binding.addressLocationEditText.text.toString())
                     }*/

                    Log.d("whaaaatcat", planCategory.toString())
                    Log.d("whaaaatcat", subCategory.toString())
                    Log.d("whaaaatcat", planDate.toString())
                    Log.d("whaaaatcat", planTime.toString())
                    Log.d("whaaaatcat", selectedLocation.toString())
                    Log.d("whaaaatcat", binding.editTextPostDescription.text.toString())

                    viewModel.createPlans(token ="Bearer ${userToken.toString()}", CreatePlanRequest(title = binding.editTextPostDescription.text.toString(),
                        from = "null",
                        date =  planDate.toString(),
                        time = planTime.toString(),
                        to = selectedLocation.toString(),
                        category = planCategory.toString(),
                        subCategory = subCategory.toString()))

                    viewModel.createPlanResponse.observe(viewLifecycleOwner, {response ->

                        if (response.isSuccessful){
                            if (response.body()!!.success){
                                Log.d("Sending Plan", response.body()!!.success.toString())
                                Toast.makeText(requireContext(), "Event Uploaded successfully", Toast.LENGTH_LONG).show()
                                (activity as MainActivity).replaceCurrentFragment(FeedScreen())
                            }else{
                                Log.d("Sending Plan", response.body()!!.success.toString())
                                Toast.makeText(requireContext(), "error while Uploading, try later", Toast.LENGTH_LONG).show()
                            }
                            binding.uploadPostAnimation.visibility = View.GONE

                            (activity as MainActivity).replaceCurrentFragment(MapScreen())
                        }else{
                            Log.d("Something went Wrong", response.errorBody().toString())
                            binding.uploadPostAnimation.visibility = View.GONE
                        }

                    })
                }else {
                    if (selectedLocation == null) {
                        Toast.makeText(
                            requireContext(),
                            "❌ Please give a proper address",
                            Toast.LENGTH_LONG
                        ).show()

                        binding.uploadPostAnimation.visibility = View.GONE

                    } else {


                        Toast.makeText(requireContext(), "❌ Please fill-in all fields", Toast.LENGTH_LONG)
                            .show()
                        binding.uploadPostAnimation.visibility = View.GONE
                    }
                }


            }else{
                Toast.makeText(requireContext(), "\uD83D\uDCCC Please complete your Profile before posting", Toast.LENGTH_LONG).show()
            }




        }
    }

    private fun convertLatLongToAddress(location:LatLng): String? {

        val city:List<Address>? =
            geocoder?.getFromLocation(location.latitude, location.longitude,1) as? List<Address>
        city?.get(0)?.getAddressLine(0)?.let { Log.d("what is more info", it) }
        return city?.get(0)?.locality
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap!!
        mMap.uiSettings.isCompassEnabled = false



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

        mMap.setOnMarkerDragListener(markObj)
        /*val listOfAddress:List<Address> =
            geocode?.getFromLocationName("Bengalinpolku 1E17,00560,Helsinki",1) as List<Address>
        val adreess: Address = listOfAddress.get(0)

        Log.d("whatAddress", "${adreess.longitude} && ${adreess.latitude}")
*/

        /*mMap.setOnMarkerClickListener( GoogleMap.OnMarkerDragListener {
            @Override fun onMarkerDragEnd(Marker marker){
                 Log.d("markerUpdate", marker.)
            }
        })*/

    }

    // ---------------------> inspect before uploading where i am setting te correct subcategory

    private fun inspectCheckedPlanType():Boolean{

        val value: String = java.lang.String.valueOf(binding.subCategoryPicker.getValue())
        if(selectedLocationType == "Address Location") {
            if (binding.addressLocationEditText.text.isNullOrEmpty()){
                return false
            }else{
                geocodeAddressLocation(binding.addressLocationEditText.text.toString())
            }

        }
        when(planCategory){
            Constants.SPORT-> {
            subCategory = subSportData[value.toInt() -1].toString()
                Log.d("whatchoosed", subSportData[value.toInt()].toString())
            }
                Constants.TRIP-> {
                    subCategory = subTripData[value.toInt()].toString()

                    Log.d("whatchoosed", subTripData[value.toInt()].toString())
                }
                    Constants.EVENT-> {
                        subCategory = subEventData[value.toInt() -1].toString()

                        Log.d("whatchoosed", subEventData[value.toInt()].toString())
                    }
        }
        return (subCategory != null && planCategory != null && planDate != null && planTime != null && binding.editTextPostDescription.text.isNotEmpty() && selectedLocation != null && selectedLocationType != null )

    }

    private fun geocodeAddressLocation(address:String){

        val listOfAddress:List<Address> =
            geocode?.getFromLocationName(address,1) as List<Address>
        if (listOfAddress.isNotEmpty()){
            val finalAddressLocation: Address = listOfAddress?.get(0)
            Log.d("leseoutcomeofLocation", finalAddressLocation.toString())

            Log.d("whatAddress", "${finalAddressLocation.longitude} && ${finalAddressLocation.latitude}")
            Log.d("selectedLoc", selectedLocation.toString())
            selectedLocation= "${finalAddressLocation.latitude},${finalAddressLocation.longitude}"
        }else{
            selectedLocation = null
        }


    }

    private fun convertTimeLongToTime(time:Long):String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/MMMM")
        return format.format(date)
    }


}
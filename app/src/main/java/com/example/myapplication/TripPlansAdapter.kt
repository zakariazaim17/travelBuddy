package com.example.myapplication

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.databinding.CustomizedSportPlanBinding
import com.example.myapplication.databinding.CustomizedTripPlanRowBinding
import com.example.myapplication.model.GetAllPlansResponse
import com.example.myapplication.screens.MapScreen
import com.example.myapplication.utils.Constants
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class TripPlansAdapter(plans: List<GetAllPlansResponse>): RecyclerView.Adapter<TripPlansAdapter.ViewHolder>() {

    private val plans:List<GetAllPlansResponse> = plans
    private var geocoder: Geocoder?= null
    private var contexti: Context?= null


    class ViewHolder(val binding: CustomizedTripPlanRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        contexti = parent.context
        return ViewHolder(CustomizedTripPlanRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        geocoder = Geocoder(contexti)
        val singlePlan = plans[position]
        holder.binding.plannerUserNameTextview.text =singlePlan.username

        holder.binding.plannerProfileImageImageView.load(singlePlan.profilePictureUrl){
            crossfade(true)
            crossfade(100)
            transformations(CircleCropTransformation())
        }

        val destination = singlePlan.to.split(",")
        val latitudeDestination:Double = destination[0].toDouble()
        val longitudeDestination:Double = destination[1].toDouble()
        val latlongLocationDestination: LatLng = LatLng(latitudeDestination, longitudeDestination)


        val origin = singlePlan.from.split(",")
        val latitudeOrigin:Double = origin[0].toDouble()
        val longitudeOrigin:Double = origin[1].toDouble()
        val latlongLocationOrigin: LatLng = LatLng(latitudeOrigin, longitudeOrigin)



        holder.binding.parentPlanLayout.setOnClickListener {

            MapScreen().navigateBetweenPlans(latlongLocationDestination)
        }
        holder.binding.tripTimeTextView.text = singlePlan.time
        holder.binding.tripPlaceOriginTextView.text = convertLatLongToAddress(latlongLocationOrigin)
        holder.binding.tripPlaceDestinationTextView.text =  convertLatLongToAddress(latlongLocationDestination)


        when(singlePlan.subCategory){
            Constants.AIR       ->      holder.binding.tripMeanOfTransportation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plane,0,0,0)
            Constants.FERRY     ->      holder.binding.tripMeanOfTransportation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ferry,0,0,0)
            Constants.BUS       ->      holder.binding.tripMeanOfTransportation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bus2,0,0,0)
            Constants.OTHER     ->      holder.binding.tripMeanOfTransportation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wheel,0,0,0)
        }


    }

    private fun convertLatLongToAddress(location:LatLng): String? {

        val city:List<Address>? =
            geocoder?.getFromLocation(location.latitude, location.longitude,1) as? List<Address>
        return city?.get(0)?.locality
    }


    override fun getItemCount(): Int {
        return plans.size
    }



    private fun convertTimeLongToTime(time:Long):String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/MMMM HH:mm")
        return format.format(date)
    }


}
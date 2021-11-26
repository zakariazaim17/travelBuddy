package com.example.myapplication

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.databinding.CustomizedEventPlanBinding
import com.example.myapplication.model.GetAllPlansResponse
import com.example.myapplication.model.GetCommentsResponse
import com.example.myapplication.screens.MapScreen
import com.example.myapplication.utils.Constants
import com.google.android.gms.maps.model.LatLng
import java.text.SimpleDateFormat
import java.util.*

class EventPlansAdapter(plans: List<GetAllPlansResponse>): RecyclerView.Adapter<EventPlansAdapter.ViewHolder>() {

   private val plans:List<GetAllPlansResponse> = plans
    private var geocoder: Geocoder?= null
    private var contexti: Context?= null


    class ViewHolder(val binding: CustomizedEventPlanBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        contexti = parent.context
        return ViewHolder(CustomizedEventPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        val location = singlePlan.to.split(",")
        val latitude:Double = location[0].toDouble()
        val longitude:Double = location[1].toDouble()
        val latLongLocation: LatLng = LatLng(latitude, longitude)


        holder.binding.parentPlanLayout.setOnClickListener {

            MapScreen().navigateBetweenPlans(latLongLocation)

        }


        holder.binding.tripTimeTextView.text = singlePlan.to
        holder.binding.eventTitleTextView.text = singlePlan.title
        holder.binding.eventPlaceTextView.text = convertLatLongToAddress(latLongLocation)

       when(singlePlan.subCategory){
           Constants.MOVIE          ->      holder.binding.subCategoryButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_movie,0,0,0)
           Constants.MUSIC          ->      holder.binding.subCategoryButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_music,0,0,0)
           Constants.THEATER        ->      holder.binding.subCategoryButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_theater,0,0,0)
           Constants.VIDEO_GAMES    ->      holder.binding.subCategoryButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vide_game,0,0,0)
           Constants.OTHER          ->      holder.binding.subCategoryButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_fire,0,0,0)
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
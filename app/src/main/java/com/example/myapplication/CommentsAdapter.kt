package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.databinding.CustomCommentRowBinding

import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.model.GetCommentsResponse
import com.example.myapplication.repository.Repository
import com.example.myapplication.screens.FeedScreen
import com.example.myapplication.screens.SinglePostScreen
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(comments: List<GetCommentsResponse>): RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    val comments:List<GetCommentsResponse> = comments



    class ViewHolder(val binding: CustomCommentRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CustomCommentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val singleComment = comments[position]
        holder.binding.commentTimestampTextView.text = convertTimeLongToTime(singleComment.timestamp)

        holder.binding.userImageImageView.load(singleComment.profilePictureUrl){
            crossfade(true)
            crossfade(100)
            transformations(CircleCropTransformation())
        }
        holder.binding.usernameTextView.text = singleComment.username
        holder.binding.commentTextView.text = singleComment.comment


    }

    override fun getItemCount(): Int {
        return comments.size
    }

     private fun convertTimeLongToTime(time:Long):String{
        val date = Date(time)
        val format = SimpleDateFormat("dd/MMMM HH:mm")
        return format.format(date)
    }


}
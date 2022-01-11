package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myapplication.databinding.CustomPostRowBinding
import com.example.myapplication.databinding.CustomProfilePostRowBinding
import com.example.myapplication.model.CreateComment
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.screens.FeedScreen
import com.example.myapplication.screens.SinglePostScreen

class ProfileAdapter(profilePosts: List<GetAllPostResponse>, context: Context):
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    val posts: List<GetAllPostResponse> = profilePosts

    val context: Context = context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val userToken = sharedPreferences.getString("User_token", null)

    class ViewHolder(val binding: CustomProfilePostRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomProfilePostRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val post = posts[position]
        holder.binding.descriptionTextView.text = post.description


        holder.binding.postImageImageView.load(post.imageUrl) {
            crossfade(true)
            crossfade(100)
        }
        holder.binding.postImageImageView.setOnLongClickListener {
            FeedScreen().showExpandableImage(post.imageUrl)
            Log.d("fdvdf", "yesyesyesyes")
            true
        }
        holder.binding.postImageImageView.setOnClickListener {
            (holder.itemView.context as MainActivity).replaceCurrentFragment(
                SinglePostScreen(post)
            )
        }


    }

    override fun getItemCount(): Int {
        return posts.size
    }

}

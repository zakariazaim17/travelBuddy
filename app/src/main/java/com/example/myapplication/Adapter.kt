package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.myapplication.databinding.CustomPostRowBinding
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.screens.FeedScreen
import kotlin.coroutines.coroutineContext

class Adapter(postData: List<GetAllPostResponse>):RecyclerView.Adapter<Adapter.ViewHolder>() {
    val posts:List<GetAllPostResponse> = postData

    class ViewHolder(val binding: CustomPostRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CustomPostRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.descriptionTextView.text = post.description
        holder.binding.postImageImageView.load(post.imageUrl){
            crossfade(true)
            crossfade(100)

        }
        holder.binding.postImageImageView.setOnLongClickListener{
            FeedScreen().showExpandableImage(post.imageUrl)
            Log.d("fdvdf", "yesyesyesyes")
          true
        }

    }

    override fun getItemCount(): Int {
      return posts.size
    }

}
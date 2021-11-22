package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.myapplication.databinding.CustomPostRowBinding
import com.example.myapplication.model.CreateComment
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.screens.FeedScreen
import com.example.myapplication.screens.SinglePostScreen
import kotlin.coroutines.coroutineContext





class Adapter(postData: List<GetAllPostResponse>, context:Context, viewModel: MainViewModel):RecyclerView.Adapter<Adapter.ViewHolder>() {
    val posts:List<GetAllPostResponse> = postData
    var viewModel:MainViewModel = viewModel
val context:Context = context
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val userToken = sharedPreferences.getString("User_token",null);

    class ViewHolder(val binding: CustomPostRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CustomPostRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.commentLayout.visibility = View.GONE
        holder.binding.seeAllCommentsTextView.visibility = View.GONE
        val post = posts[position]
        holder.binding.descriptionTextView.text = post.description

        holder.binding.sendCommentButton.setOnClickListener {


             viewModel.createPostcomment("Bearer ${userToken.toString()}", CreateComment(holder.binding.commentEditText.text.toString(), post.id))
Toast.makeText(context, "Comment Posted", Toast.LENGTH_LONG).show()

            holder.binding.commentEditText.text.clear()



        }
        Log.d("textin", holder.binding.descriptionTextView.length().toString())
        holder.binding.postImageImageView.load(post.imageUrl){
            crossfade(true)
            crossfade(100)
        }
        holder.binding.postImageImageView.setOnLongClickListener{
            FeedScreen().showExpandableImage(post.imageUrl)
            Log.d("fdvdf", "yesyesyesyes")
          true
        }
        holder.binding.postImageImageView.setOnClickListener {
            (holder.itemView.context as MainActivity).replaceCurrentFragment(SinglePostScreen(post))
        }
        holder.binding.commentButton.setOnClickListener {
            holder.binding.commentLayout.visibility = View.VISIBLE
            holder.binding.seeAllCommentsTextView.visibility = View.VISIBLE
        }
        holder.binding.seeAllCommentsTextView.setOnClickListener {
            (holder.itemView.context as MainActivity).replaceCurrentFragment(SinglePostScreen(post))
        }

    }

    override fun getItemCount(): Int {
      return posts.size
    }

}
package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.example.myapplication.databinding.CustomPostRowBinding
import com.example.myapplication.model.CreateComment
import com.example.myapplication.model.GetAllPostResponse
import com.example.myapplication.screens.FeedScreen
import com.example.myapplication.screens.SinglePostScreen
import kotlin.coroutines.coroutineContext





class Adapter(postData: List<GetAllPostResponse>, context:Context, viewModel: MainViewModel):RecyclerView.Adapter<Adapter.ViewHolder>() {
    val posts: List<GetAllPostResponse> = postData
    var viewModel: MainViewModel = viewModel
    val context: Context = context
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    val userToken = sharedPreferences.getString("User_token", null)

    class ViewHolder(val binding: CustomPostRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CustomPostRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.commentLayout.visibility = View.GONE
        holder.binding.parentContainer.removeView(holder.binding.seeAllCommentsTextView)
        val post = posts[position]
        holder.binding.descriptionTextView.text = post.description

        holder.binding.posterImageImageView.load(post.profilePictureUrl) {
            crossfade(true)
            crossfade(90)
            transformations(CircleCropTransformation())

        }




        holder.binding.posterUserNameTextView.text = post.username

        holder.binding.sendCommentButton.setOnClickListener {


            viewModel.createPostcomment(
                "Bearer ${userToken.toString()}",
                CreateComment(holder.binding.commentEditText.text.toString(), post.id)
            )
            Toast.makeText(context, "Comment Posted", Toast.LENGTH_LONG).show()

            holder.binding.commentEditText.text.clear()


        }
        holder.binding.commentEditText.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    FeedScreen().hideKeyboard(v, context)

                }
            }
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
                holder.binding.commentButton.setOnClickListener {
                    holder.binding.commentLayout.visibility = View.VISIBLE
                    holder.binding.parentContainer.removeView(holder.binding.seeAllCommentsTextView)
                    holder.binding.parentContainer.addView(holder.binding.seeAllCommentsTextView)
                    //holder.binding.seeAllCommentsTextView.visibility = View.VISIBLE
                }
                holder.binding.seeAllCommentsTextView.setOnClickListener {
                    (holder.itemView.context as MainActivity).replaceCurrentFragment(
                        SinglePostScreen(post)
                    )
                }

            }

        override fun getItemCount(): Int {
            return posts.size
        }

    }

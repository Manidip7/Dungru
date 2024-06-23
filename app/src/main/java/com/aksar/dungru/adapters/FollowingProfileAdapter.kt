package com.aksar.dungru.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FollowingItemBinding
import com.aksar.dungru.models.response.FollowerFollowingData
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView

class FollowingProfileAdapter(private val context: Context, private val data: List<FollowerFollowingData>, private val onUnfollowClick: (Int,FollowerFollowingData,ProgressBar,MaterialTextView) -> Unit) :
    RecyclerView.Adapter<FollowingProfileAdapter.FollowingProfileHolder>() {

    class FollowingProfileHolder(var binding: FollowingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: FollowerFollowingData){
            val imageUrl = Constance.ImageBaseUrl + item.image
            Glide.with(context).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.imgFollowingUser)
            binding.txtFollowingUser.text = item.username

            binding.imgFollowingUser.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra(Constance.DataPass,item.user_id)
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingProfileHolder {
        val binding = FollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowingProfileHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowingProfileHolder, position: Int) {
        val item = data[position]
        holder.bind(context, item)
        holder.binding.btnUnfollow.setOnClickListener {
            holder.binding.btnUnfollow.visibility = View.INVISIBLE
            holder.binding.progressBar.visibility = View.VISIBLE
            onUnfollowClick(position,item,holder.binding.progressBar,holder.binding.btnUnfollow)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
}
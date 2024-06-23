package com.aksar.dungru.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FollowersItemBinding
import com.aksar.dungru.models.response.FollowerFollowingData
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.bumptech.glide.Glide

class FollowerProfileAdapter(private val context: Context, private val data: List<FollowerFollowingData>, private val onItemClick: (FollowerFollowingData) -> Unit) :
    RecyclerView.Adapter<FollowerProfileAdapter.FollowerProfileHolder>() {

    class FollowerProfileHolder(private val binding: FollowersItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: FollowerFollowingData){
            val imageUrl = Constance.ImageBaseUrl + item.image
            Glide.with(context).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.imgFollowerProfile)
            binding.txtBlockUsername.text = item.username

            binding.imgFollowerProfile.setOnClickListener {
                val intent = Intent(context, UserProfileActivity::class.java)
                intent.putExtra(Constance.DataPass,item.user_id)
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerProfileHolder {
        val binding = FollowersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowerProfileHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowerProfileHolder, position: Int) {
        val item = data[position]
        holder.bind(context,item)
        holder.itemView.setOnClickListener {
            onItemClick.invoke(item)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
}
package com.aksar.dungru.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemProfileFillesRecyclerBinding
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.utils.Constance.ALL
import com.aksar.dungru.utils.Constance.IMAGE
import com.aksar.dungru.utils.Constance.VIDEO
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class ProfileFilesAdapter(
    private val context: Context,
    private var itemList: List<ProfilePostData>,
    private val type: String?,
    private var onClickListener: (item:ProfilePostData) -> Unit,
) : RecyclerView.Adapter<ProfileFilesAdapter.ProfileFilesViewHolder>() {

    class ProfileFilesViewHolder(val binding: ItemProfileFillesRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfilePostData, type: String?, context: Context) {

            Glide.with(context).load(item.post_url).centerCrop().placeholder(getShimmerDrawableForGlide()).error(R.drawable.image_placeholder).into(binding.imgThumbnail)
            when (type) {
                ALL -> {
                    if (item.post_type == "video")
                        binding.playVideoIv.visibility = View.VISIBLE
                    else
                        binding.playVideoIv.visibility = View.GONE
                }

                VIDEO -> {
                    binding.playVideoIv.visibility = View.VISIBLE
                }

                IMAGE -> {
                    binding.playVideoIv.visibility = View.GONE
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileFilesViewHolder {
        return ProfileFilesViewHolder(
            ItemProfileFillesRecyclerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ProfileFilesViewHolder,
        position: Int,
    ) {
        val item = itemList[position]
        holder.bind(item, type, context)
        holder.itemView.setOnClickListener {
            onClickListener.invoke(item)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
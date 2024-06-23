package com.aksar.dungru.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.BlockUserLayoutBinding
import com.aksar.dungru.models.response.BlockUserData
import com.aksar.dungru.utils.Constance.ImageBaseUrl
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView

class BlockUserAdapter(
    private var context: Context,
    private var data: ArrayList<BlockUserData>,
    private var onItemClick: (position: Int, item:BlockUserData, loader:ProgressBar, btnView:MaterialTextView) -> Unit
) :
    RecyclerView.Adapter<BlockUserAdapter.BlockUserHolder>() {

    class BlockUserHolder(val binding: BlockUserLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: BlockUserData) {
            val imageUrl = ImageBaseUrl + item.image
            Glide.with(context).load(imageUrl).centerCrop().placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder).into(binding.imgBlockUser)
            binding.txtBolckUser.text = item.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockUserHolder {
        val binding =
            BlockUserLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockUserHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockUserHolder, position: Int) {
        val item = data[position]
        holder.bind(context, item)
        holder.binding.btnUnblock.setOnClickListener {
            holder.binding.btnUnblock.visibility = View.INVISIBLE
            holder.binding.progressBar.visibility = View.VISIBLE
            onItemClick.invoke(position,item,holder.binding.progressBar,holder.binding.btnUnblock)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemCommentRecyclerBinding
import com.aksar.dungru.models.response.CommentData
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.Constance.ImageBaseUrl
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.bumptech.glide.Glide

class CommentsAdapter(
    private var context: Context,
    private var itemList: ArrayList<CommentData>,
    private var listener: CommentClickListener,
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    interface CommentClickListener {
        fun userAccountClicked(id: String)
    }

    class CommentViewHolder(var binding: ItemCommentRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: CommentData) {
            val imageUrl = ImageBaseUrl + item.image
            Glide.with(context).load(imageUrl).placeholder(getShimmerDrawableForGlide()).error(R.drawable.user_placeholder).into(binding.imgUserProfile)
//            convert Base64 decode comment Data
            val decodedByteArray = Base64.decode(item.comment_content, Base64.DEFAULT)
            val decodedString = String(decodedByteArray, Charsets.UTF_8)

            binding.txtComment.text = decodedString
            binding.txtUserName.text = item.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemCommentRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = itemList.size

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item)

        //Account click
        holder.binding.imgUserProfile.setOnClickListener {
            listener.userAccountClicked(item.user_id)
        }
        holder.binding.txtUserName.setOnClickListener {
            listener.userAccountClicked(item.user_id)
        }

    }
}
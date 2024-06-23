package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemFeedRecyclerBinding
import com.aksar.dungru.models.response.FeedPosts
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.timeFormatter
import com.bumptech.glide.Glide

class FeedAdapter(
    private var context: Context,
    private var itemList: ArrayList<FeedPosts>,
    private var listener: FollowClickListener,
) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    interface FollowClickListener {
        fun followButtonClicked(
            position: Int,
            item: FeedPosts,
            followProgress: ProgressBar,
            btnView: Button
        )

        fun likeButtonClicked(item: FeedPosts)
        fun commentButtonClicked(item: FeedPosts)
        fun shareButtonClicked(position: Int, item: FeedPosts)
        fun moreButtonClicked(position: Int, item: FeedPosts)
        fun userAccountClicked(id: String)
        fun onContentClick(item: FeedPosts)

    }

    class FeedViewHolder(var binding: ItemFeedRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, item: FeedPosts) {
            if (Utils(context).isDarkTheme())
                binding.root.setBackgroundColor(context.getColor(R.color.black_bg_dynamic_neutral_variant_10))
            else
                binding.root.setBackgroundColor(context.getColor(R.color.white_bg_dynamic_neutral_variant_99))

            val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
            binding.btnMore.visibility =
                if (item.user_id == data?.user_id) View.GONE else View.VISIBLE
            binding.btnFollow.visibility =
                if (item.user_id == data?.user_id) View.GONE else View.VISIBLE

            try {
                val base64Caption = item.caption
                val decodedByteArray = Base64.decode(base64Caption, Base64.DEFAULT)
                val formattedCaptionText = String(decodedByteArray, Charsets.UTF_8)
                binding.txtCaption.text = formattedCaptionText
            }catch (e:Exception){
                Log.e("Base64", "${e.message}")
            }

            binding.txtCaption.maxLines = 2
            binding.txtSeeMore.visibility = View.GONE
            binding.txtCaption.post {
                if (binding.txtCaption.lineCount > 2) {
                    binding.txtSeeMore.visibility = View.VISIBLE
                    binding.captionLayout.setOnClickListener {
                        binding.txtCaption.maxLines =
                            if (binding.txtCaption.maxLines == 2) Int.MAX_VALUE else 2
                        binding.txtSeeMore.text =
                            if (binding.txtCaption.maxLines == 2) context.getString(R.string.see_more) else context.getString(R.string.see_less)
                    }
                    binding.txtSeeMore.setOnClickListener {
                        binding.txtCaption.maxLines =
                            if (binding.txtCaption.maxLines == 2) Int.MAX_VALUE else 2
                        binding.txtSeeMore.text =
                            if (binding.txtCaption.maxLines == 2) context.getString(R.string.see_more) else context.getString(R.string.see_less)
                    }
                }
            }
            binding.txtFeedName.text = item.username
            val imageUrl = Constance.ImageBaseUrl + item.image
            Glide.with(context).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.imgFeedProfile)
            Glide.with(context).load(item.post_url).placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.image_placeholder).into(binding.imgContent)
            binding.txtFeedTime.text = timeFormatter(item.uploaded_on)
//            binding.txtNumberOfLikes.text = item.like_count
//            binding.txtNumberOfComments.text = item.comment_count
//            binding.txtNumberOfShare.text = item.share_count

            if (item.post_type == "video") {
                binding.videoPlayView.visibility = View.VISIBLE
            } else {
                binding.videoPlayView.visibility = View.GONE
            }

            if (item.isFollowed) {
                binding.btnFollow.background =
                    context.getDrawable(R.drawable.bg_rounded_gradiant_color_button_50dp)
                binding.btnFollow.text = context.getString(R.string.followed)
                binding.btnFollow.setTextColor(context.getColor(R.color.white))
            } else {
                binding.btnFollow.background =
                    context.getDrawable(R.drawable.bg_rounded_btn_pink_color_stroke)
                binding.btnFollow.text = context.getString(R.string.follow)
                binding.btnFollow.setTextColor(context.getColor(R.color.pink))
            }

            if (item.isLiked) {
                binding.icLike.setIconResource(R.drawable.ic_liked_click_icon)
            } else {
                binding.icLike.setIconResource(R.drawable.ic_like)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(
            ItemFeedRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = itemList.size

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item)
        var count = item.like_count.toInt()

        //Click functionalities
        holder.binding.btnFollow.setOnClickListener {
            holder.binding.progressBar.visibility = View.VISIBLE
            holder.binding.btnFollow.visibility = View.INVISIBLE
            listener.followButtonClicked(
                position,
                item,
                holder.binding.progressBar,
                holder.binding.btnFollow
            )
        }

        holder.binding.btnLike.setOnClickListener {
            listener.likeButtonClicked(item)
            if(item.isLiked){
                holder.binding.icLike.setIconResource(R.drawable.ic_like)
                count--
            }else{
                holder.binding.icLike.setIconResource(R.drawable.ic_liked_click_icon)
                count++
            }
            itemList[position].isLiked = !item.isLiked
            itemList[position].like_count = count.toString()
//            holder.binding.txtNumberOfLikes.text = "$count"

        }

        holder.binding.btnShare.setOnClickListener {
            listener.shareButtonClicked(position, item)
        }

        holder.binding.btnComment.setOnClickListener {
            listener.commentButtonClicked(item)
        }

        holder.binding.contentLayout.setOnClickListener {
            listener.onContentClick(item)
        }

        holder.binding.btnMore.setOnClickListener {
            listener.moreButtonClicked(position, item)
        }

        //Account click
        holder.binding.imgFeedProfile.setOnClickListener {
            listener.userAccountClicked(item.user_id)
        }
        holder.binding.profileInfoLayout.setOnClickListener {
            listener.userAccountClicked(item.user_id)
        }
    }
}
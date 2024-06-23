package com.aksar.dungru.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.LiveviewVideoItemBinding
import com.aksar.dungru.models.LiveVideoModel
import android.net.Uri
import android.view.View
import android.widget.MediaController
import com.aksar.dungru.R

class VideoShowAdapter(
    val context: Context,
    private val data: ArrayList<LiveVideoModel>,
    private val onItemClick: VideoData,
) :
    RecyclerView.Adapter<VideoShowAdapter.VideoViewHolder>() {
    interface VideoData {
        fun getData(position: Int, item: LiveVideoModel)
    }

    class VideoViewHolder(var binding: LiveviewVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, item: LiveVideoModel) {
            val videoView = binding.videoviewNew
            var isFollowed = false

            binding.loading.setAnimation(R.raw.loading_animation_livepage)
            val mediaController = MediaController(context)
            mediaController.setAnchorView(videoView)
            videoView.setVideoURI(Uri.parse(item.videoUrl))
            videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
                binding.loading.cancelAnimation()
                binding.progressBar.visibility = View.GONE
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.hideShimmer()
            }
            binding.loading.playAnimation()
            binding.progressBar.visibility = View.VISIBLE
            binding.shimmerViewContainer.startShimmer()
            binding.btnFollow.setOnClickListener {
                if (isFollowed) {
                    binding.btnFollow.text = "+Follow"
                    binding.btnFollow.background =
                        context.getDrawable(R.drawable.bg_rounded_btn_pink_color_stroke)
                    binding.btnFollow.setTextColor(context.getColor(R.color.white))
                    isFollowed = false
                } else {
                    binding.btnFollow.text = "Followed"
                    binding.btnFollow.background =
                        context.getDrawable(R.drawable.bg_rounded_gradiant_color_button_50dp)
                    binding.btnFollow.setTextColor(context.getColor(R.color.white))
                    isFollowed = true
                }
            }
            videoView.requestFocus()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding =
            LiveviewVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = data[position]
        holder.bind(context, item)
        holder.binding.root.setOnClickListener {
            onItemClick.getData(position, item)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
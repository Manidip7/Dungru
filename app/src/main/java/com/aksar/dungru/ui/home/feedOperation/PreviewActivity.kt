package com.aksar.dungru.ui.home.feedOperation

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityPreviewBinding
import com.aksar.dungru.models.request.PostLikeDislikeReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.utils.extensions.timeFormatter
import com.aksar.dungru.viewModel.FeedViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.bumptech.glide.Glide

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var feedViewModel: FeedViewModel
    private var data: LoggedUserData? = null
    private var mediaPlayer: MediaPlayer? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("imageUrl")
        val videoUrl = intent.getStringExtra("videoUrl")
        val postTime = intent.getStringExtra("postTime")
        val caption = intent.getStringExtra("caption")
        val userName = intent.getStringExtra("user_name")
        val userProfile = intent.getStringExtra("user_profile")
        var likeCount = intent.getStringExtra("like_count").toString().toInt()
        val commentCount = intent.getStringExtra("comment_count")
        val shareCount = intent.getStringExtra("share_count")
        val postId = intent.getStringExtra("post_id")
        val likeFlag = intent.getBooleanExtra("Like_flag",false)

        NetworkUtils.checkConnection(this)

        feedViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FeedViewModel::class.java]

        // Decode the Base64 encoded string
        val decodedByteArray = Base64.decode(caption, Base64.DEFAULT)
        val decodedString = String(decodedByteArray, Charsets.UTF_8)
        initObserver()
        data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)

        val imageUrlProfile = Constance.ImageBaseUrl + userProfile
        Glide.with(this).load(imageUrlProfile).placeholder(getShimmerDrawableForGlide()).into(binding.profileImg)

        binding.txtName.text = userName
        binding.likeCount.text = likeCount.toString()
        binding.commentCount.text = commentCount
        binding.shareCount.text = shareCount

        if (decodedString.isEmpty()) {
            binding.captionLayout.visibility = View.GONE
        } else {
            binding.captionLayout.visibility = View.VISIBLE
            binding.txtCaption.text = decodedString
            binding.txtCaption.maxLines = 2
            binding.txtSeeMore.visibility = View.GONE
            binding.txtCaption.post {
                if (binding.txtCaption.lineCount > 2) {
                    binding.txtSeeMore.visibility = View.VISIBLE
                    binding.captionLayout.setOnClickListener {
                        binding.txtCaption.maxLines = if (binding.txtCaption.maxLines == 2) Int.MAX_VALUE else 2
                        binding.txtSeeMore.text = if (binding.txtCaption.maxLines == 2) "...See More" else "See Less"
                    }
                    binding.txtSeeMore.setOnClickListener {
                        binding.txtCaption.maxLines = if (binding.txtCaption.maxLines == 2) Int.MAX_VALUE else 2
                        binding.txtSeeMore.text = if (binding.txtCaption.maxLines == 2) "...See More" else "See Less"
                    }
                }
            }
        }


        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).placeholder(R.drawable.image_placeholder).into(binding.imageView)
            binding.imageView.visibility = View.VISIBLE
        } else {
            binding.imageView.visibility = View.GONE
        }

        Log.d("VideoViewCreate: ",videoUrl.toString())
        Log.d("imageViewCreate: ",imageUrl.toString())
        if (videoUrl != null) {
            binding.videoViewLayout.visibility = View.VISIBLE
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            val videoUri = Uri.parse(videoUrl)
            binding.videoView.setVideoURI(videoUri)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.setOnPreparedListener { mediaPlayer ->
                this.mediaPlayer = mediaPlayer
            }
            binding.videoView.start()

        } else {
            binding.videoViewLayout.visibility = View.GONE
        }

        binding.txtTimeDate.text = timeFormatter(postTime!!)

        var isLiked = likeFlag

        binding.btnLike.setIconResource(if (isLiked) R.drawable.ic_liked_click_icon else R.drawable.ic_like)

        binding.btnLike.setOnClickListener {
            feedViewModel.postLikeDislike(PostLikeDislikeReq(data?.user_id.toString(),postId!!))
            isLiked = !isLiked


            if (isLiked){
                binding.btnLike.setIconResource(R.drawable.ic_liked_click_icon)
                likeCount++
                binding.likeCount.text = likeCount.toString()
            }else{
                binding.btnLike.setIconResource(R.drawable.ic_like)
                likeCount--
                binding.likeCount.text = likeCount.toString()
            }
        }
        binding.btnComments.setOnClickListener {
            val dialog = CommentBottomSheetDialogFragment(postId!!)
            dialog.show(this.supportFragmentManager,"CommentDialog")
        }
 

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        var isMute = false
        binding.btnSound.setOnClickListener {
            if(isMute){
                mediaPlayer?.setVolume(1f, 1f)
                binding.btnSound.setIconResource(R.drawable.ic_volume_up)
            }else{
                mediaPlayer?.setVolume(0f, 0f)
                binding.btnSound.setIconResource(R.drawable.ic_volume_off)
            }

            isMute = !isMute
        }

    }

    private fun initObserver() {
        feedViewModel.postLikeDislikeResponse.observe(this){response->
            when(response){
                is NetworkResponse.Loading->{}
                is NetworkResponse.Success->{
                    showToast(response.data?.message.toString(),false)
                }
                is NetworkResponse.Error->{
                    showToast(response.message.toString(),false)
                }
            }
        }
    }
}
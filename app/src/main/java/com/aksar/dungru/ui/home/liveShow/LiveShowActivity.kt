package com.aksar.dungru.ui.home.liveShow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.adapters.GiftUserAdapter
import com.aksar.dungru.adapters.VideoShowAdapter
import com.aksar.dungru.databinding.ActivityLiveShowBinding
import com.aksar.dungru.databinding.GiftLayoutBinding
import com.aksar.dungru.models.GiftDataModel
import com.aksar.dungru.models.LiveVideoModel
import com.aksar.dungru.utils.extensions.adjustSystemNavigationBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.aksar.dungru.utils.NetworkUtils


class LiveShowActivity : AppCompatActivity(),OnClickListener,VideoShowAdapter.VideoData{
    lateinit var binding: ActivityLiveShowBinding
    private var likeCount = 0
    private var isAnimating = false
    private lateinit var giftUserAdapter: GiftUserAdapter
    private lateinit var videoList: ArrayList<LiveVideoModel>
    private lateinit var videoShowAdapter: VideoShowAdapter

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveShowBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
        adjustSystemNavigationBar(window, binding.layoutBottomAllContent)
        NetworkUtils.checkConnection(this)

        videoList = arrayListOf(
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"),
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"),
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"),
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"),
            LiveVideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        )
        videoShowAdapter = VideoShowAdapter(this,videoList,this)

        binding.viewPager.adapter = videoShowAdapter
            binding.btnLike.setOnClickListener {
            if (!isAnimating) {
                likeCount++
                addLikeImage()
            }
        }
        binding.btnGift.setOnClickListener {
            val giftDialog = BottomSheetDialog(this)
            val giftDialogBinding = GiftLayoutBinding.inflate(layoutInflater)
            giftDialog.setContentView(giftDialogBinding.root)

            giftDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val data = arrayListOf(
                GiftDataModel("9",R.drawable.ic_pngwing),
                GiftDataModel("10",R.drawable.ic_cask),
                GiftDataModel("11",R.drawable.ic_bird),
                GiftDataModel("12",R.drawable.ic_lipes),
                GiftDataModel("13",R.drawable.ic_love),
                GiftDataModel("14",R.drawable.ic_love_ballon),
                GiftDataModel("15",R.drawable.ic_star),
                GiftDataModel("16",R.drawable.pngwing_10),
                GiftDataModel("17",R.drawable.pngwing_11),
                GiftDataModel("208",R.drawable.pngwing_12),
                GiftDataModel("458",R.drawable.pngwing_15),
                GiftDataModel("186",R.drawable.pngwing_16),
                GiftDataModel("148",R.drawable.pngwing_17),
                GiftDataModel("918",R.drawable.pngwing_18),
                GiftDataModel("718",R.drawable.pngwing_9),
                GiftDataModel("818",R.drawable.pngwing_20)
            )

            giftUserAdapter = GiftUserAdapter(data) {
                Toast.makeText(this, it.point, Toast.LENGTH_SHORT).show()
            }
            giftDialogBinding.giftRec.adapter = giftUserAdapter

            giftDialog.show()
        }

        binding.btnShare.setOnClickListener {

            Toast.makeText(this, "Share Button", Toast.LENGTH_SHORT).show()
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            startActivity(Intent.createChooser(sharingIntent, "Share your Friend"))
        }

    }
    private fun addLikeImage() {
        isAnimating = true

        val likeImage = ShapeableImageView(this)
        likeImage.setImageResource(R.drawable.ic_live_gift_show)

        val layoutParams = LinearLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen._35dp),
            resources.getDimensionPixelSize(R.dimen._35dp)
        )
        likeImage.layoutParams = layoutParams

        binding.layoutLikeShow.addView(likeImage)
        val slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.love_icon_animation)

        slideUpAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                binding.layoutLikeShow.removeView(likeImage)
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        likeImage.startAnimation(slideUpAnimation)
    }
    override fun onClick(view: View?) {
        when(view){
            binding.btnBack-> onBackPressed()
        }
    }

    override fun getData(position: Int, item: LiveVideoModel) {
        val data =  videoList[position].videoUrl
        Log.d("getdata: ",data)
    }
}





















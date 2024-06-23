package com.aksar.dungru.ui.profile


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.adapters.ViewPagerAdapter
import com.aksar.dungru.databinding.ActivityProfileBinding
import com.aksar.dungru.databinding.CustomTabBinding
import com.aksar.dungru.databinding.ImageshowdiglogboxBinding
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.FetchProfilePostsReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.network.BitmapUtils
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.follower.FollowerFollowingListActivity
import com.aksar.dungru.ui.profile.profilefragments.ProfileAllListFragment
import com.aksar.dungru.ui.profile.profilefragments.ProfileGiftListFragment
import com.aksar.dungru.ui.profile.profilefragments.ProfilePhotoListFragment
import com.aksar.dungru.ui.profile.profilefragments.ProfileVideoListFragment
import com.aksar.dungru.ui.wallet.WalletActivity
import com.aksar.dungru.utils.Constance.Flow
import com.aksar.dungru.utils.Constance.Follower
import com.aksar.dungru.utils.Constance.Following
import com.aksar.dungru.utils.Constance.ImageBaseUrl
import com.aksar.dungru.utils.Constance.USER_ID
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FeedViewModel
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelFeed: FeedViewModel
    private val REQUEST_PERMISSIONS = 111
    private val PICK_IMAGE_REQUEST_CODE = 222
    private val CAMERA_REQUEST_CODE = 123
    private var postFile: File? = null
    private var mediaType: String? = null
    private lateinit var addImageAndVideo: BottomSheetDialog
    private var data: LoggedUserData? = null
    private var postDataList = ArrayList<ProfilePostData>()
    private var imageList = ArrayList<ProfilePostData>()
    private var videoList = ArrayList<ProfilePostData>()
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_buttom_animation
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_buttom_anim
        )
    }
    private var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]
        viewModelFeed = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FeedViewModel::class.java]

        initObserver()
        setSavedData()

        data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        viewModel.fetchProfilePosts(FetchProfilePostsReq(data?.user_id.toString(),data?.user_id.toString()))
    }

    private fun refreshing() {
        binding.shimmerView.visibility = VISIBLE
        binding.profileLayout.visibility = INVISIBLE
        binding.profileInfoLayout.visibility = INVISIBLE
        viewModel.fetchProfilePosts(FetchProfilePostsReq(data?.user_id.toString(),data?.user_id.toString()))
    }

    private fun initObserver() {
        viewModel.profilePostsResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.profileLayout.visibility = INVISIBLE
                    binding.profileInfoLayout.visibility = INVISIBLE
                    binding.shimmerView.visibility = VISIBLE
                    binding.shimmerView.startShimmer()
                }

                is NetworkResponse.Success -> {
                    binding.profileLayout.visibility = VISIBLE
                    binding.profileInfoLayout.visibility = VISIBLE
                    binding.shimmerView.visibility = GONE
                    binding.shimmerView.stopShimmer()
                    if (response.data?.data != null) {
                        val data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
                        binding.txtWalletBalance.text = data?.no_of_coins
                        binding.txtFollower.text = response.data.data.user_data.follower_count
                        binding.txtFollowing.text = response.data.data.user_data.following_count

                        postDataList.clear()
                        postDataList.addAll(response.data.data.posts)

                        setUpProfileViewPager(response.data.data.user_data.username, response.data.data.user_data.image)
                    }
                }

                is NetworkResponse.Error -> {
                    binding.profileLayout.visibility = VISIBLE
                    binding.profileInfoLayout.visibility = VISIBLE
                    binding.shimmerView.visibility = GONE
                    binding.shimmerView.stopShimmer()
                    showToast(response.message.toString(), false)
                }
            }
        }


        // upload image and video
        viewModelFeed.uploadPostResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {

                }

                is NetworkResponse.Success -> {
                    refreshing()
                    onAddButtonClicked(!clicked)
                    showToast(response.data?.message.toString(), false)
                    addImageAndVideo.dismiss()
                }

                is NetworkResponse.Error -> {
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

    private fun setSavedData() {
        val data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        binding.txtUserName.text = data?.username
        binding.txtUniqueId.text = data?.unique_name

        //have to set image url from data
        if (!data?.image.isNullOrBlank()) {
            val imageUrl = ImageBaseUrl + data?.image
            Glide.with(this).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.profileImage)
        } else binding.profileImage.setImageResource(R.drawable.user_placeholder)

    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()

            binding.btnEdit -> startActivity(Intent(this, EditProfileActivity::class.java))

            binding.btnShare -> shareProfileDetails()

            binding.btnAddPost -> {
                onAddButtonClicked(!clicked)
            }

            binding.btnGalleryPicker -> selectPhotoAndVideo()

            binding.btnCameraPicker -> if (checkCameraPermission()) {
                openCamera()
            }

            binding.walletLayout -> startActivity(Intent(this, WalletActivity::class.java))

            binding.followerLayout -> {
                val intent = Intent(this, FollowerFollowingListActivity::class.java)
                intent.putExtra(Flow, Follower)
                intent.putExtra(USER_ID, data?.user_id)
                startActivity(intent)
            }

            binding.followingLayout -> {
                val intent = Intent(this, FollowerFollowingListActivity::class.java)
                intent.putExtra(Flow, Following)
                intent.putExtra(USER_ID, data?.user_id)
                startActivity(intent)
            }
        }
    }

    private fun onAddButtonClicked(clicked: Boolean) {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        this.clicked = clicked
    }

    private fun setVisibility(clicked: Boolean) {
        binding.btnGalleryPicker.visibility = if (clicked) VISIBLE else INVISIBLE
        binding.btnCameraPicker.visibility = if (clicked) VISIBLE else INVISIBLE
    }

    private fun setAnimation(clicked: Boolean) {
        binding.btnGalleryPicker.startAnimation(if (clicked) fromBottom else toBottom)
        binding.btnCameraPicker.startAnimation(if (clicked) fromBottom else toBottom)
        binding.btnAddPost.startAnimation(if (clicked) rotateOpen else rotateClose)
    }

    private fun setClickable(clicked: Boolean) {
        binding.btnGalleryPicker.isClickable = clicked
        binding.btnCameraPicker.isClickable = clicked
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
            return false
        }
        return true
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 101)
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun selectPhotoAndVideo() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!Utils(this).isAllPermissionsGranted(permissionList)) {
                Utils(this).requestPermission(permissionList, REQUEST_PERMISSIONS)
            } else {
                openMediaPicker()
            }
        } else {
            val permissionList =
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
            if (!Utils(this).isAllPermissionsGranted(permissionList)) {
                Utils(this).requestPermission(permissionList, REQUEST_PERMISSIONS)
            } else {
                openMediaPicker()
            }
        }
    }

    private fun openMediaPicker() {
        val mimeTypes = arrayOf("image/*", "video/*")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*, video/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Media"),
            PICK_IMAGE_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    openMediaPicker()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            Log.d("onActivityResult1: ", selectedImageUri.toString())
            selectedImageUri?.let {
                showSetVideoDialog(it)
            }
        } else {
            if (requestCode == 101) {
                val imageBitmap = data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    // Show the captured image in the dialog
                    val uri = getImageUri(this, it)
                    showSetVideoDialogCameraImage(uri)
                }
            }
        }
    }

    private fun getImageUri(context: Context, imageBitmap: Bitmap): Uri {
//        val bytes = ByteArrayOutputStream()
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            imageBitmap,
            "Camera",
            null
        )
        return Uri.parse(path)
    }


    private fun showSetVideoDialog(uri: Uri) {
        addImageAndVideo = BottomSheetDialog(this)
        val addImageAndVideoBinding = ImageshowdiglogboxBinding.inflate(layoutInflater)
        addImageAndVideo.setContentView(addImageAndVideoBinding.root)
        val imageAndVideo = addImageAndVideoBinding.imgSelectedImage

        // Check the MIME type to determine if it's a photo or video
        val uriType = this.contentResolver.getType(uri)
        if (uriType != null && uriType.contains("video")) {
            // It's a video
            Log.d("showSetVideoDialog: ", "Video selected: $uri")

            try {
                postFile = getFileFromUri(this, uri)
                Log.d("showSetVideoDialog123: ", postFile.toString())
                mediaType = "video/mp4"
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this, uri)
                val thumbnail =
                    retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                imageAndVideo.setImageBitmap(thumbnail)
                addImageAndVideoBinding.videoPlayView.visibility = View.VISIBLE
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        } else {
            val compressedImage = compressImage(uri)
            val compressedImageUri = BitmapUtils.saveBitmap(this.contentResolver, compressedImage!!)
            postFile = getFileFromUri(this, compressedImageUri!!)
            Log.d("showSetVideoDialog123: ", postFile.toString())
            mediaType = "image/jpeg"
            Log.d("showSetVideoDialog: ", "Photo selected: $uri")
            imageAndVideo.setImageURI(compressedImageUri)
        }


        addImageAndVideoBinding.btnPost.setOnClickListener {
            if (postFile != null) {
                val caption = addImageAndVideoBinding.etFeedback.text.toString()
                val requestFile: RequestBody =
                    RequestBody.create(mediaType?.toMediaTypeOrNull(), postFile!!)

                val postData =
                    MultipartBody.Part.createFormData("userfile", postFile!!.name, requestFile)

                val userId = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    data?.user_id.toString()
                )
                Log.d("showSetVideoDialog: ", userId.toString())
//                val captionRb = RequestBody.create(
//                    "text/plain".toMediaTypeOrNull(),
//                    caption.toString()
//                )

                val captionBase64 = Base64.encodeToString(caption.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
                val formattedCaption = RequestBody.create("text/plain".toMediaTypeOrNull(), captionBase64)
                viewModelFeed.uploadPost(userId, postData, formattedCaption)
            }
        }
        addImageAndVideo.show()
    }

    private fun showSetVideoDialogCameraImage(uri: Uri) {
        addImageAndVideo = BottomSheetDialog(this)
        val addImageAndVideoBinding = ImageshowdiglogboxBinding.inflate(layoutInflater)
        addImageAndVideo.setContentView(addImageAndVideoBinding.root)
        val imageAndVideo = addImageAndVideoBinding.imgSelectedImage


        postFile = getFileFromUri(this, uri)
        Log.d("showSetVideoDialog123: ", postFile.toString())
        mediaType = "image/jpeg"
        Log.d("showSetVideoDialog: ", "Photo selected: $uri")
        imageAndVideo.setImageURI(uri)


        addImageAndVideoBinding.btnPost.setOnClickListener {
            if (postFile != null) {
                val caption = addImageAndVideoBinding.etFeedback.text.toString()
                val requestFile: RequestBody =
                    RequestBody.create(mediaType?.toMediaTypeOrNull(), postFile!!)

                val postData =
                    MultipartBody.Part.createFormData("userfile", postFile!!.name, requestFile)

                val userId = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    data?.user_id.toString()
                )
                Log.d("showSetVideoDialog: ", userId.toString())
//                val captionRb = RequestBody.create(
//                    "text/plain".toMediaTypeOrNull(),
//                    caption.toString()
//                )
                val captionBase64 = Base64.encodeToString(caption.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
                val formattedCaption = RequestBody.create("text/plain".toMediaTypeOrNull(), captionBase64)
                viewModelFeed.uploadPost(userId, postData, formattedCaption)
            }
        }
        addImageAndVideo.show()
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        var file: File? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val tempFile = File.createTempFile("temp", null, context.cacheDir)
                tempFile.deleteOnExit()
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                file = tempFile
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun compressImage(uri: Uri): Bitmap? {
        return try {
            val inputStream = this.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inSampleSize = 2 // Adjust sample size as needed for quality and performance
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    private fun compressVideoAndGetUri(context: Context, uri: Uri): Uri? {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(context, uri, null)

        // Find and select the video track
        var trackIndex = -1
        for (i in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("video/") == true) {
                trackIndex = i
                break
            }
        }
        if (trackIndex == -1) {
            mediaExtractor.release()
            return null
        }
        mediaExtractor.selectTrack(trackIndex)

        // Get video format
        val videoFormat = mediaExtractor.getTrackFormat(trackIndex)
        val originalBitrate = videoFormat.getInteger(MediaFormat.KEY_BIT_RATE)
        println("Original Bitrate: $originalBitrate")

        // Calculate the target bitrate for approximately 70% compression
        val targetBitrate = (originalBitrate * 0.3).toInt()
        println("Target Bitrate: $targetBitrate")

        try {
            // Set up MediaCodec for encoding
            val encoder =
                MediaCodec.createEncoderByType(videoFormat.getString(MediaFormat.KEY_MIME) ?: "")
            val mediaFormat = MediaFormat.createVideoFormat(
                "video/avc",
                videoFormat.getInteger(MediaFormat.KEY_WIDTH),
                videoFormat.getInteger(MediaFormat.KEY_HEIGHT)
            )
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, targetBitrate)
            mediaFormat.setInteger(
                MediaFormat.KEY_FRAME_RATE,
                videoFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
            )
            mediaFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
            encoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            val surface = encoder.createInputSurface()
            encoder.start()

            // Set up the output file
            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            val outputFile = File(outputDir, "compressed_video.mp4")
            val outputStream = FileOutputStream(outputFile)

            // Write the compressed data to the file
            // (This part needs implementation, you'll need to write encoded data to the output file)
            // You can refer to official documentation or online resources for this part.

            // Monitor the size of the compressed video
            var compressedFileSize = outputFile.length()
            println("Initial Compressed File Size: $compressedFileSize")
            while (compressedFileSize > originalBitrate * 0.7) { // Adjust the threshold as needed
                // Adjust the bitrate and re-encode
                val newBitrate =
                    (mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE) * 0.95).toInt() // Reduce bitrate by 5%
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, newBitrate)
                encoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                // Write the compressed data to the file again
                // (This part needs implementation, you'll need to write encoded data to the output file)
                // Update the compressed file size
                compressedFileSize = outputFile.length()
                println("Current Compressed File Size: $compressedFileSize")
                println("Current Bitrate: $newBitrate")
            }

            // Release resources
            encoder.stop()
            encoder.release()
            mediaExtractor.release()
            outputStream.close()

            // Return the URI for the compressed video file
            return Uri.fromFile(outputFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun shareProfileDetails() {
        val userName = "#User123"
        val shareMsg = "Hey! Your friend just share his/ her details."
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareMsg + userName)
        }
        startActivity(Intent.createChooser(intent, "Share Via"))
    }

    private fun setUpProfileViewPager(username: String, image: String) {
        imageList.clear()
        videoList.clear()
        imageList.addAll(postDataList.filter { it.post_type != "video" })
        videoList.addAll(postDataList.filter { it.post_type == "video" })

        binding.tabLayout.setupWithViewPager(binding.viewPager, true)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ProfileAllListFragment.newInstance(postDataList,username,image), R.drawable.ic_all)
        viewPagerAdapter.addFragment(ProfilePhotoListFragment.newInstance(imageList,username,image), R.drawable.ic_photo)
        viewPagerAdapter.addFragment(ProfileVideoListFragment.newInstance(videoList,username,image), R.drawable.ic_video)
        viewPagerAdapter.addFragment(ProfileGiftListFragment(), R.drawable.ic_gift)

        binding.viewPager.adapter = viewPagerAdapter

        // Customize the tabs using the custom layout
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            val customTabBinding = CustomTabBinding.inflate(layoutInflater)
            customTabBinding.tabIcon.setImageResource(viewPagerAdapter.getIconResource(i))
            //setting custom view to tab
            tab?.customView = customTabBinding.root
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val icon = it.customView?.findViewById<ImageView>(R.id.tabIcon)
                    icon?.setColorFilter(
                        this@ProfileActivity.getColor(R.color.action_color),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val icon = it.customView?.findViewById<ImageView>(R.id.tabIcon)
                    icon?.setColorFilter(
                        this@ProfileActivity.getColor(R.color.text_color_grey),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onRestart() {
        super.onRestart()
        setSavedData()
        refreshing()
    }
}
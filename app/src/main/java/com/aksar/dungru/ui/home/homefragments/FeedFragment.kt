package com.aksar.dungru.ui.home.homefragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.FeedAdapter
import com.aksar.dungru.adapters.ReportUserAdapter
import com.aksar.dungru.databinding.FeedBlockReportDialogBinding
import com.aksar.dungru.databinding.FragmentFeedBinding
import com.aksar.dungru.databinding.ImageshowdiglogboxBinding
import com.aksar.dungru.databinding.UnfollowConfromDialogBinding
import com.aksar.dungru.models.ReportUsetModel
import com.aksar.dungru.models.request.AddOrRemoveFollowerReq
import com.aksar.dungru.models.request.AddReportReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.BlockOrUnblockUserReq
import com.aksar.dungru.models.request.PostLikeDislikeReq
import com.aksar.dungru.models.response.FeedPosts
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.BitmapUtils
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.home.feedOperation.CommentBottomSheetDialogFragment
import com.aksar.dungru.ui.home.feedOperation.PreviewActivity
import com.aksar.dungru.ui.profile.ProfileActivity
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.utils.Constance.DataPass
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.hideProgressDialog
import com.aksar.dungru.utils.extensions.showProgressDialog
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FeedViewModel
import com.aksar.dungru.viewModel.FollowerFollowingsViewModel
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class FeedFragment : Fragment(), FeedAdapter.FollowClickListener {
    private lateinit var binding: FragmentFeedBinding
    private var feedList = ArrayList<FeedPosts>()
    private lateinit var feedAdapter: FeedAdapter
    private val REQUEST_PERMISSIONS = 123
    private val PICK_IMAGE_REQUEST_CODE = 1001
    private var data: LoggedUserData? = null
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var followerViewModel: FollowerFollowingsViewModel
    private lateinit var settingViewModel: SettingViewModel
    private var postFile: File? = null
    private var mediaType: String? = null
    private lateinit var addImageAndVideo: BottomSheetDialog
    private lateinit var unfollowWarningDialog: BottomSheetDialog
    private lateinit var moreDialog: BottomSheetDialog
    private var followItemProgress : ProgressBar? = null
    private var followItemBtnView : Button? = null

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.from_buttom_animation) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(),R.anim.to_buttom_anim) }
    private var clicked = false
    private lateinit var moreDialogBinding :FeedBlockReportDialogBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        feedViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FeedViewModel::class.java]
        followerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FollowerFollowingsViewModel::class.java]
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[SettingViewModel::class.java]

        //Api calling for fetch feed posts
        data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        feedViewModel.fetchFeedPosts(BaseUserIDReq(data?.user_id.toString()))

        addImageAndVideo = BottomSheetDialog(requireContext())
        unfollowWarningDialog = BottomSheetDialog(requireContext())
        moreDialog = BottomSheetDialog(requireContext())

        val feedRecycler = binding.feedRecycler
        feedRecycler.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = FeedAdapter(requireContext(), feedList, this)
        feedRecycler.adapter = feedAdapter

        binding.swipeToRefreshLayout.setOnRefreshListener {
            feedViewModel.fetchFeedPosts(BaseUserIDReq(data?.user_id.toString()))
            binding.feedRecycler.visibility = GONE
            binding.noDataView.visibility = GONE
            binding.shimmerView.visibility = VISIBLE
            binding.shimmerView.startShimmer()
            binding.swipeToRefreshLayout.isRefreshing = false
        }

        binding.btnAddPost.setOnClickListener {
            onAddButtonClicked(!clicked)
        }

        binding.btnGalleryPicker.setOnClickListener {
            selectPhotoAndVideo()
        }
        binding.btnCameraPicker.setOnClickListener{
            if (checkCameraPermission()) {
                openCamera()
            }
        }

        return binding.root
    }
    private fun onAddButtonClicked(clicked:Boolean) {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        this.clicked = clicked
    }

    private fun setVisibility(clicked: Boolean) {
        binding.btnGalleryPicker.visibility = if (clicked) View.VISIBLE else View.INVISIBLE
        binding.btnCameraPicker.visibility = if (clicked) View.VISIBLE else View.INVISIBLE
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
            return false
        }
        return true
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 101)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        feedViewModel.uploadPostResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {}

                is NetworkResponse.Success -> {
                    showToast(response.data?.message.toString(), false)
                    addImageAndVideo.dismiss()
                }

                is NetworkResponse.Error -> showToast(response.message.toString(), false)

            }
        }

        feedViewModel._FeedData.observe(viewLifecycleOwner){ response->
            when (response) {
                is NetworkResponse.Loading -> {
//                    binding.shimmerView.visibility = VISIBLE
//                    binding.shimmerView.startShimmer()
                }

                is NetworkResponse.Success -> {
                    binding.feedRecycler.visibility = VISIBLE
                    binding.shimmerView.visibility = GONE
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.hideShimmer()
                    if(response.data?.data != null && response.data.data.isNotEmpty()){
                        binding.noDataView.visibility = GONE
                        feedList.clear()
                        feedList.addAll(response.data.data)
                        feedAdapter.notifyDataSetChanged()
                    }else{
                        binding.feedRecycler.visibility = GONE
                        binding.noDataView.visibility = VISIBLE
                    }
                }

                is NetworkResponse.Error -> {
                    binding.shimmerView.visibility = GONE
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.hideShimmer()
                    showToast(response.message.toString(), false)
                }
            }
        }

        feedViewModel.postLikeDislikeResponse.observe(viewLifecycleOwner){response->
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

        feedViewModel.addReportResponse.observe(viewLifecycleOwner){response->
            when(response){
                is NetworkResponse.Loading->{}
                is NetworkResponse.Success->{
                    moreDialog.dismiss()
                    showToast(response.data?.message.toString(),false)
                }
                is NetworkResponse.Error->{
                    moreDialogBinding.btnReportConfirm.visibility = VISIBLE
                    moreDialogBinding.reportProgress.visibility = GONE
                    showToast(response.message.toString(),false)
                }
            }
        }

        followerViewModel.addRemoveFollowerResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is NetworkResponse.Loading->{}
                is NetworkResponse.Success->{
                    followItemProgress?.visibility = GONE
                    followItemBtnView?.visibility = VISIBLE
//                    feedAdapter.notifyDataSetChanged()
                    feedViewModel.fetchFeedPosts(BaseUserIDReq(data?.user_id.toString()))
                    unfollowWarningDialog.dismiss()
                    showToast(response.data?.message.toString(),false)
                }
                is NetworkResponse.Error->{
                    followItemProgress?.visibility = GONE
                    followItemBtnView?.visibility = VISIBLE
                    showToast(response.message.toString(),false)
                }
            }
        }

        settingViewModel.blockUnblockUserResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is NetworkResponse.Loading->{}
                is NetworkResponse.Success->{
                    moreDialog.dismiss()
                    showToast(response.data?.message.toString(),false)
                }
                is NetworkResponse.Error->{
                    moreDialogBinding.btnBlockConfirm.visibility = VISIBLE
                    moreDialogBinding.blockProgress.visibility = GONE
                    showToast(response.message.toString(),false)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun followButtonClicked(position: Int, item: FeedPosts, followProgress:ProgressBar, btnView:Button) {
        followItemProgress = followProgress
        followItemBtnView = btnView
        if(item.isFollowed){
            showUnfollowWarningDialog(position, item)
        } else{
//            feedList[position].isFollowed = !item.isFollowed
            followerViewModel.addOrRemoveFollower(AddOrRemoveFollowerReq(data?.user_id.toString(),item.user_id))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun likeButtonClicked(item: FeedPosts) {
        feedViewModel.postLikeDislike(PostLikeDislikeReq(data?.user_id.toString(),item.post_id))
    }

    override fun commentButtonClicked(item: FeedPosts) {
       val dialog = CommentBottomSheetDialogFragment(item.post_id)
        dialog.show(requireActivity().supportFragmentManager,"CommentDialog")
    }

    override fun shareButtonClicked(position: Int, item: FeedPosts) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        startActivity(Intent.createChooser(sharingIntent, "Share your Friend"))
    }


    override fun moreButtonClicked(position: Int, item: FeedPosts) {
        moreDialogBinding = FeedBlockReportDialogBinding.inflate(layoutInflater)
        moreDialog.setContentView(moreDialogBinding.root)
        moreDialog.dismissWithAnimation
        moreDialog.show()
        var reportReason = ""

        moreDialogBinding.btnBlockUser.setOnClickListener {
            moreDialogBinding.buttonsLayout.visibility = GONE
            moreDialogBinding.confirmationLayout.visibility = VISIBLE
            moreDialogBinding.btnReportConfirm.visibility = GONE
            moreDialogBinding.reasonLayout.visibility = GONE

            moreDialogBinding.txtHeading.text = resources.getString(R.string.block_user)
            moreDialogBinding.txtWaring.text = resources.getString(R.string.block_title)
        }
        moreDialogBinding.btnReportUser.setOnClickListener {
            moreDialogBinding.buttonsLayout.visibility = GONE
            moreDialogBinding.confirmationLayout.visibility = VISIBLE
            moreDialogBinding.btnBlockConfirm.visibility = GONE

            moreDialogBinding.txtHeading.text = resources.getString(R.string.report_user)
//            moreDialogBinding.txtWaring.text = resources.getString(R.string.report_title)


            val data = listOf(
                ReportUsetModel("Sexual content"),
                ReportUsetModel("Violent content"),
                ReportUsetModel("Child abuse"),
                ReportUsetModel("Spam or fraud"),
                ReportUsetModel("Suicide or self injury"),
                ReportUsetModel("False information"),
                ReportUsetModel("Other")
            )
            val adapter = ReportUserAdapter(data) { item ->
                reportReason = item.reason
//                Toast.makeText(requireContext(), item.reason, Toast.LENGTH_SHORT).show()
            }
            moreDialogBinding.reasonList.adapter = adapter

        }
        moreDialogBinding.btnBlockConfirm.setOnClickListener {
            settingViewModel.blockOrUnblockUser(BlockOrUnblockUserReq(user_id = data?.user_id.toString(), block_user_id = item.user_id))
            moreDialogBinding.btnBlockConfirm.visibility = GONE
            moreDialogBinding.blockProgress.visibility = VISIBLE
        }
        moreDialogBinding.btnReportConfirm.setOnClickListener {
            if(reportReason==""){
                showToast("Please select above reason.",false)
            }else{
                feedViewModel.addReport(AddReportReq(data?.user_id.toString(),item.post_id,reportReason))
                moreDialogBinding.btnReportConfirm.visibility = GONE
                moreDialogBinding.reportProgress.visibility = VISIBLE
            }

        }
        moreDialogBinding.btnCancel.setOnClickListener {
            moreDialog.dismiss()
        }

    }

    override fun userAccountClicked(id: String) {
        if(id != data?.user_id){
            Log.d("userAccountClicked: ",id.toString())
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra(DataPass, id)
            startActivity(intent)
        }else{
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
    }

    override fun onContentClick(item: FeedPosts) {
        if (item.post_type == "video"){
            val intent = Intent(requireContext(),PreviewActivity::class.java)
            intent.putExtra("videoUrl",item.post_url)
            intent.putExtra("postTime",item.uploaded_on)
            intent.putExtra("caption",item.caption)
            intent.putExtra("user_name",item.username)
            intent.putExtra("user_profile",item.image)
            intent.putExtra("like_count",item.like_count)
            intent.putExtra("comment_count",item.comment_count)
            intent.putExtra("share_count",item.share_count)
            intent.putExtra("post_id",item.post_id)
            intent.putExtra("Like_flag",item.isLiked)
            startActivity(intent)
        }else{
            val intent = Intent(requireContext(),PreviewActivity::class.java)
            intent.putExtra("imageUrl",item.post_url)
            intent.putExtra("postTime",item.uploaded_on)
            intent.putExtra("caption",item.caption)
            intent.putExtra("user_name",item.username)
            intent.putExtra("user_profile",item.image)
            intent.putExtra("like_count",item.like_count)
            intent.putExtra("comment_count",item.comment_count)
            intent.putExtra("share_count",item.share_count)
            intent.putExtra("post_id",item.post_id)
            intent.putExtra("Like_flag",item.isLiked)
            startActivity(intent)
        }
    }

    private fun showUnfollowWarningDialog(position: Int, item: FeedPosts) {
        val dialogBinding = UnfollowConfromDialogBinding.inflate(layoutInflater)
        unfollowWarningDialog.setContentView(dialogBinding.root)
        val formattedText = getString(R.string.unfollow_warning, "<font color=\"#EA0054\">@${item.username}</font>")
        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        dialogBinding.txtWarning.text = spannedText

        dialogBinding.btnUnfollowConfirm.setOnClickListener {
            feedList[position].isFollowed = !item.isFollowed
            followerViewModel.addOrRemoveFollower(AddOrRemoveFollowerReq(data?.user_id.toString(),item.user_id))
        }

        dialogBinding.btnUnfollowCancle.setOnClickListener {
            unfollowWarningDialog.cancel()
        }
        unfollowWarningDialog.setOnCancelListener {
            followItemProgress?.visibility = GONE
            followItemBtnView?.visibility = VISIBLE
            followItemBtnView?.isClickable = true
            unfollowWarningDialog.dismiss()
        }

      unfollowWarningDialog.show()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun selectPhotoAndVideo() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!Utils(requireContext()).isAllPermissionsGranted(permissionList)) {
                Utils(requireContext()).requestPermission(permissionList, REQUEST_PERMISSIONS)
            } else {
                openMediaPicker()
            }
        } else {
            val permissionList = arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
            if (!Utils(requireContext()).isAllPermissionsGranted(permissionList)) {
                Utils(requireContext()).requestPermission(permissionList, REQUEST_PERMISSIONS)
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
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    openMediaPicker()
                } else {
                    // Permission denied, handle accordingly
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            101->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }else{
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
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
        }else{
            if (requestCode == 101) {
                val imageBitmap = data?.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    // Show the captured image in the dialog
                    val uri = getImageUri(requireContext(), it)
                    showSetVideoDialogCameraImage(uri)
                }
            }
        }
    }

    private fun getImageUri(context: Context, imageBitmap: Bitmap): Uri {
//        val bytes = ByteArrayOutputStream()
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, imageBitmap, "Camera", null)
        return Uri.parse(path)
    }

    private fun showSetVideoDialog(uri: Uri) {
        val addImageAndVideoBinding = ImageshowdiglogboxBinding.inflate(layoutInflater)
        addImageAndVideo.setContentView(addImageAndVideoBinding.root)
        val imageAndVideo = addImageAndVideoBinding.imgSelectedImage

        // Check the MIME type to determine if it's a photo or video
        val uriType = requireActivity().contentResolver.getType(uri)
        if (uriType != null && uriType.contains("video")) {
            // It's a video
            Log.d("showSetVideoDialog: ", "Video selected: $uri")
            try {

//                   val duration = getVideoDuration(requireContext(),uri)
//                    val durationInSeconds = duration?.div(1000) // Convert milliseconds to seconds
//                    Log.d("GetVideoDuration: ",durationInSeconds.toString())

                postFile = getFileFromUri(requireContext(), uri)
                Log.d("showSetVideoDialog123: ", postFile.toString())
                mediaType = "video/mp4"
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(requireContext(), uri)
                val thumbnail =
                    retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                imageAndVideo.setImageBitmap(thumbnail)
                addImageAndVideoBinding.videoPlayView.visibility = VISIBLE
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        } else {
            val compressedImage = compressImage(uri)
//                postFile = getFileFromUri(requireContext(), uri)
            val compressedImageUri = BitmapUtils.saveBitmap(requireActivity().contentResolver, compressedImage!!)
            postFile = getFileFromUri(requireContext(), compressedImageUri!!)
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
                val captionRb = RequestBody.create("text/plain".toMediaTypeOrNull(), captionBase64)
                //Api calling for upload post
                feedViewModel.uploadPost(userId, postData, captionRb)
            }
        }
        addImageAndVideo.show()
    }

    private fun showSetVideoDialogCameraImage(uri: Uri) {
        addImageAndVideo = BottomSheetDialog(requireContext())
        val addImageAndVideoBinding = ImageshowdiglogboxBinding.inflate(layoutInflater)
        addImageAndVideo.setContentView(addImageAndVideoBinding.root)
        val imageAndVideo = addImageAndVideoBinding.imgSelectedImage


        postFile = getFileFromUri(requireContext(), uri)
        Log.d("showSetVideoDialog123: ",postFile.toString())
        mediaType = "image/jpeg"
        Log.d("showSetVideoDialog:", "Photo selected: $uri")
        imageAndVideo.setImageURI(uri)


        addImageAndVideoBinding.btnPost.setOnClickListener {
            if (postFile != null){
                val caption = addImageAndVideoBinding.etFeedback.text
                val requestFile: RequestBody = RequestBody.create(mediaType?.toMediaTypeOrNull(), postFile!!)

                val postData = MultipartBody.Part.createFormData("userfile", postFile!!.name, requestFile)

                val userId = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    data?.user_id.toString()
                )
                Log.d("showSetVideoDialog: ",userId.toString())
                val captionRb = RequestBody.create(
                    "text/plain".toMediaTypeOrNull(),
                    caption.toString()
                )
                feedViewModel.uploadPost(userId, postData,captionRb)
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


    //    Image Compress Method
    private fun compressImage(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inSampleSize = 2 // Adjust sample size as needed for quality and performance
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getVideoDuration(context: Context, videoUri: Uri): Long? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            val durationString =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            return durationString?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
    override fun onResume() {
        super.onResume()
        feedViewModel.fetchFeedPosts(BaseUserIDReq(data?.user_id.toString()))
    }
}
package com.aksar.dungru.ui.profile

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.adapters.ViewPagerAdapter
import com.aksar.dungru.databinding.ActivityUserProfileBinding
import com.aksar.dungru.databinding.BlockConfirmDiglogBinding
import com.aksar.dungru.databinding.CustomTabBinding
import com.aksar.dungru.databinding.ReportConfirmDiglogBinding
import com.aksar.dungru.databinding.UnfollowConfromDialogBinding
import com.aksar.dungru.databinding.UnfollowWarningDialogBinding
import com.aksar.dungru.models.request.AddOrRemoveFollowerReq
import com.aksar.dungru.models.request.BlockOrUnblockUserReq
import com.aksar.dungru.models.request.FetchProfilePostsReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.ProfilePostData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.profile.profilefragments.ProfileAllListFragment
import com.aksar.dungru.ui.profile.profilefragments.ProfilePhotoListFragment
import com.aksar.dungru.ui.profile.profilefragments.ProfileVideoListFragment
import com.aksar.dungru.utils.Constance.DataPass
import com.aksar.dungru.utils.Constance.ImageBaseUrl
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.hideProgressDialog
import com.aksar.dungru.utils.extensions.showProgressDialog
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FeedViewModel
import com.aksar.dungru.viewModel.FollowerFollowingsViewModel
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout

class UserProfileActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var followerFollowingsViewModel: FollowerFollowingsViewModel
    private lateinit var settingViewModel: SettingViewModel
    private var isFollowed = false
    private var creatorId: String? = null
    private var data :LoggedUserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]
        followerFollowingsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FollowerFollowingsViewModel::class.java]
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[SettingViewModel::class.java]

        initObserver()
        data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        creatorId = intent.getStringExtra(DataPass).toString()
        profileViewModel.fetchProfilePosts(FetchProfilePostsReq(data?.user_id.toString(),creatorId.toString()))
    }

    private fun initObserver() {
        profileViewModel.profilePostsResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.profileLayout.visibility = View.INVISIBLE
                    binding.profileInfoLayout.visibility = View.INVISIBLE
                    binding.shimmerView.visibility = View.VISIBLE
                    binding.shimmerView.startShimmer()
                }

                is NetworkResponse.Success -> {
                    binding.profileLayout.visibility = View.VISIBLE
                    binding.profileInfoLayout.visibility = View.VISIBLE
                    binding.shimmerView.visibility = View.GONE
                    binding.shimmerView.stopShimmer()
                    if(response.data?.data?.user_data != null){
                        val imageUrl = ImageBaseUrl + response.data.data.user_data.image
                        Glide.with(this).load(imageUrl).centerCrop()
                            .placeholder(getShimmerDrawableForGlide())
                            .error(R.drawable.user_placeholder)
                            .into(binding.profileImage)
                        if(response.data.data.user_data.isFollowed){
                            isFollowed = response.data.data.user_data.isFollowed
                            binding.btnFollow.background = getDrawable(R.drawable.bg_follow_button)
                            binding.btnFollow.text = resources.getString(R.string.followed)
                            binding.btnFollow.setTextColor(resources.getColor(R.color.pink))
                        }

                        binding.txtName.text = response.data.data.user_data.unique_name
                        binding.txtUserName.text = response.data.data.user_data.username
                        binding.txtWalletBalance.text = response.data.data.user_data.coins_earned
                        binding.txtFollower.text = response.data.data.user_data.follower_count
                        binding.txtFollowing.text = response.data.data.user_data.following_count

                        setUpProfileViewPager(response.data.data.posts,response.data.data.user_data.username, response.data.data.user_data.image)
                    }
                }

                is NetworkResponse.Error -> {
                    binding.profileLayout.visibility = View.VISIBLE
                    binding.profileInfoLayout.visibility = View.VISIBLE
                    binding.shimmerView.visibility = View.GONE
                    binding.shimmerView.stopShimmer()
                    showToast(response.message.toString(), false)
                }
            }
        }

        followerFollowingsViewModel.addRemoveFollowerResponse.observe(this){ response->
            when(response){
                is NetworkResponse.Loading->{
                    showProgressDialog(R.raw.loading_animation_livepage)
                }
                is NetworkResponse.Success->{
                    hideProgressDialog()
                    isFollowed = !isFollowed
                    if(isFollowed){
                        binding.btnFollow.background = getDrawable(R.drawable.bg_follow_button)
                        binding.btnFollow.text = resources.getString(R.string.followed)
                        binding.btnFollow.setTextColor(resources.getColor(R.color.pink))
                    }else{
                        binding.btnFollow.text = resources.getString(R.string.follow)
                        binding.btnFollow.background =
                            getDrawable(R.drawable.bg_rounded_gradiant_color_text_5dp)
                        binding.btnFollow.setTextColor(resources.getColor(R.color.white))
                    }
                }
                is NetworkResponse.Error->{
                    hideProgressDialog()
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

    private fun setUpProfileViewPager(postDataList: List<ProfilePostData>,username: String, image: String) {
        val (videoList, imageList) = postDataList.partition { it.post_type == "video" }
        binding.tabLayout.setupWithViewPager(binding.viewPager, true)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ProfileAllListFragment.newInstance(postDataList,username,image), R.drawable.ic_all)
        viewPagerAdapter.addFragment(ProfilePhotoListFragment.newInstance(imageList,username,image), R.drawable.ic_photo)
        viewPagerAdapter.addFragment(ProfileVideoListFragment.newInstance(videoList,username,image), R.drawable.ic_video)

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
                        this@UserProfileActivity.getColor(R.color.action_color),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val icon = it.customView?.findViewById<ImageView>(R.id.tabIcon)
                    icon?.setColorFilter(
                        this@UserProfileActivity.getColor(R.color.text_color_grey),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()

            binding.btnChat -> {
//            chat activity call
            }

            binding.btnFollow -> {
                if (!isFollowed) {
                    followerFollowingsViewModel.addOrRemoveFollower(AddOrRemoveFollowerReq(data?.user_id.toString(),creatorId.toString()))
                } else {
                    val dialogBinding = UnfollowWarningDialogBinding.inflate(layoutInflater)
                    val dialog = BottomSheetDialog(this)
                    dialog.setContentView(dialogBinding.root)

                    // Unfollow Button
                    dialogBinding.btnUnfollow.setOnClickListener {
                        dialog.dismiss()
                        val dialogBindingUnfollowConfirm = UnfollowConfromDialogBinding.inflate(layoutInflater)
                        val dialogUnfollowConfirm = BottomSheetDialog(this)
                        dialogUnfollowConfirm.setContentView(dialogBindingUnfollowConfirm.root)
                        val formattedText = getString(R.string.unfollow_warning, "<font color=\"#EA0054\">@${binding.txtUserName.text}</font>")
                        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        dialogBindingUnfollowConfirm.txtWarning.text = spannedText


                        // UnfollowConfirm Button
                        dialogBindingUnfollowConfirm.btnUnfollowConfirm.setOnClickListener {
                            followerFollowingsViewModel.addOrRemoveFollower(AddOrRemoveFollowerReq(data?.user_id.toString(),creatorId.toString()))
                            dialogUnfollowConfirm.dismiss()
                        }

                        // Cancel Button Click
                        dialogBindingUnfollowConfirm.btnUnfollowCancle.setOnClickListener {
                            dialogUnfollowConfirm.dismiss()
                        }

                        dialogUnfollowConfirm.show()
                    }

                    // Report Button
                    dialogBinding.btnReport.setOnClickListener {
                        dialogBinding.btnReport.background = getDrawable(R.drawable.bg_rounded_gradiant_color_text_5dp)
                        binding.btnFollow.background = getDrawable(R.drawable.bg_follow_button)

                        dialog.dismiss()
                        val dialogBindingReport = ReportConfirmDiglogBinding.inflate(layoutInflater)
                        val dialogReportConfirm = BottomSheetDialog(this)
                        dialogReportConfirm.setContentView(dialogBindingReport.root)
                        val formattedText = getString(R.string.unfollow_warning, "<font color=\"#EA0054\">@${binding.txtUserName.text}</font>")
                        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        dialogBindingReport.txtWarning.text = spannedText

                        // ReportConfirm Button
                        dialogBindingReport.btnReportConfirm.setOnClickListener {
                            Toast.makeText(this, "Report SuccessFull", Toast.LENGTH_SHORT).show()
                            dialogReportConfirm.dismiss()
                        }

                        // ReportCancel Button
                        dialogBindingReport.btnCancel.setOnClickListener {
                            dialogReportConfirm.dismiss()
                        }
                        dialogReportConfirm.show()

                    }

                    // Block Button
                    dialogBinding.btnBlock.setOnClickListener {
                        dialogBinding.btnBlock.background = getDrawable(R.drawable.bg_rounded_gradiant_color_text_5dp)
                        binding.btnFollow.background = getDrawable(R.drawable.bg_follow_button)

                        dialog.dismiss()

                        val dialogBindingBlock = BlockConfirmDiglogBinding.inflate(layoutInflater)
                        val dialogBlockConfirm = BottomSheetDialog(this)
                        dialogBlockConfirm.setContentView(dialogBindingBlock.root)
                        val formattedText = getString(R.string.unfollow_warning, "<font color=\"#EA0054\">@${binding.txtUserName.text}</font>")
                        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        dialogBindingBlock.txtWarning.text = spannedText

                        dialogBindingBlock.btnBlockConfirm.setOnClickListener {
                            settingViewModel.blockOrUnblockUser(BlockOrUnblockUserReq(data?.user_id.toString(),creatorId.toString()))
                            dialogBlockConfirm.dismiss()
                        }

                        dialogBindingBlock.txtBlockCancel.setOnClickListener {
                            dialogBlockConfirm.dismiss()
                        }
                        dialogBlockConfirm.show()
                    }
                    dialog.show()
                }
            }

        }
    }
}
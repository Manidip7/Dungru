package com.aksar.dungru.ui.home


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityHomeBinding
import com.aksar.dungru.models.request.BaseUserIDAndTokenReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.MyApp
import com.aksar.dungru.ui.home.golive.GoLiveActivity
import com.aksar.dungru.ui.home.homefragments.ChatsListFragment
import com.aksar.dungru.ui.home.homefragments.FeedFragment
import com.aksar.dungru.ui.home.homefragments.LiveFragment
import com.aksar.dungru.ui.notifications.NotificationActivity
import com.aksar.dungru.ui.profile.ProfileActivity
import com.aksar.dungru.ui.search.SearchActivity
import com.aksar.dungru.ui.setting.SettingActivity
import com.aksar.dungru.ui.wallet.WalletActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.Constance.Live
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.utils.extensions.getShimmerDrawableForGlide
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.ProfileViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var selectedButton: MaterialButton
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PrefManager.initialize(this)
        NetworkUtils.checkConnection(this)
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[ProfileViewModel::class.java]

        //for loading setting data of user
         val data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        profileViewModel.fetchUserData(
            BaseUserIDAndTokenReq(
                data?.user_id.toString(),
                data?.unique_token.toString()
            )
        )
        initObserver()

        //initial add fragment
        if (supportFragmentManager.findFragmentById(binding.homeContainer.id) == null) {
            addFragment(binding.homeContainer.id, LiveFragment(), false, Live)
        }

        selectedButton = binding.bottomNavBar.itemLive
        buttonStateChange(binding.bottomNavBar.itemLive)

        //for controlling the page color based on theme
        themeColorControl()


        binding.bottomNavBar.itemLive.setOnClickListener {
            if (getCurrentFragment() !is LiveFragment) {
                supportFragmentManager.popBackStack()
                try {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, LiveFragment())
                        .commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.buttonsLayout.visibility = View.VISIBLE
            binding.searchView.visibility = View.GONE
            buttonStateChange(binding.bottomNavBar.itemLive)
        }

        binding.bottomNavBar.itemFeed.setOnClickListener {
            if (getCurrentFragment() !is FeedFragment) {
                supportFragmentManager.popBackStack()
                try {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, FeedFragment())
                        .commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            binding.buttonsLayout.visibility = View.VISIBLE
            binding.searchView.visibility = View.GONE
            buttonStateChange(binding.bottomNavBar.itemFeed)
        }
        binding.bottomNavBar.itemGoLive.setOnClickListener {
            startActivity(Intent(this, GoLiveActivity::class.java))
        }
        binding.bottomNavBar.itemChat.setOnClickListener {
            if (getCurrentFragment() !is ChatsListFragment) {
                supportFragmentManager.popBackStack()
                try {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.home_container, ChatsListFragment())
                        .commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            binding.buttonsLayout.visibility = View.GONE
            binding.searchView.visibility = View.VISIBLE
            buttonStateChange(binding.bottomNavBar.itemChat)
        }
        binding.bottomNavBar.itemSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun initObserver() {
        profileViewModel.profileResponse.observe(this) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
//                    showProgressDialog(R.raw.loading_animation_livepage)
                }

                is NetworkResponse.Success -> {
//                    hideProgressDialog()
                    PrefManager.get().save(PrefManager.GENERAL_SETTING, response.data?.data?.general_setting)
                    PrefManager.get().save(PrefManager.NOTIFICATION_SETTING, response.data?.data?.in_app_notifications)
                    if (!response.data?.data?.image.isNullOrBlank()) {
                        val imageUrl = Constance.ImageBaseUrl + response.data?.data?.image
                        Glide.with(this).load(imageUrl).centerCrop()
                            .placeholder(getShimmerDrawableForGlide())
                            .error(R.drawable.user_placeholder)
                            .into(binding.profileImg)
                    } else binding.profileImg.setImageResource(R.drawable.user_placeholder)
                }

                is NetworkResponse.Error -> {
//                    hideProgressDialog()
                    showToast(response.message.toString(), false)
                }
            }
        }

    }


    override fun onBackPressed() {
        val currentFragment = getCurrentFragment()

        if (currentFragment is FeedFragment || currentFragment is ChatsListFragment) {
            replaceFragment(binding.homeContainer.id, LiveFragment(), false, Live)
            selectedButton = binding.bottomNavBar.itemLive
            binding.buttonsLayout.visibility = View.VISIBLE
            binding.searchView.visibility = View.GONE
            buttonStateChange(selectedButton)
        } else {
            if (supportFragmentManager.backStackEntryCount == 0) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.bottomNavBar.itemLive -> {
                if (getCurrentFragment() !is LiveFragment) {
                    supportFragmentManager.popBackStack()
                    try {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_container, LiveFragment())
                            .commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                binding.buttonsLayout.visibility = View.VISIBLE
                binding.searchView.visibility = View.GONE
                buttonStateChange(binding.bottomNavBar.itemLive)
            }

            binding.bottomNavBar.itemFeed -> {
                if (getCurrentFragment() !is FeedFragment) {
                    supportFragmentManager.popBackStack()
                    try {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_container, FeedFragment())
                            .commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                binding.buttonsLayout.visibility = View.VISIBLE
                binding.searchView.visibility = View.GONE
                buttonStateChange(binding.bottomNavBar.itemFeed)
            }

            binding.bottomNavBar.itemGoLive -> {
                startActivity(Intent(this, GoLiveActivity::class.java))
            }

            binding.bottomNavBar.itemChat -> {
                if (getCurrentFragment() !is ChatsListFragment) {
                    supportFragmentManager.popBackStack()
                    try {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_container, ChatsListFragment())
                            .commit()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                binding.buttonsLayout.visibility = View.GONE
                binding.searchView.visibility = View.VISIBLE
                buttonStateChange(binding.bottomNavBar.itemChat)
            }
            // Handle other cases similarly
            // ...
            binding.bottomNavBar.itemSetting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }

            binding.btnSearch -> startActivity(Intent(this, SearchActivity::class.java))
            binding.btnNotification -> startActivity(Intent(this, NotificationActivity::class.java))
            binding.profileImg -> startActivity(Intent(this, ProfileActivity::class.java))
            binding.walletLayout -> startActivity(Intent(this, WalletActivity::class.java))

        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.home_container)
    }

    private fun themeColorControl() {
        if (Utils(this).isDarkTheme()) {
            binding.homeContainer.setBackgroundColor(resources.getColor(R.color.black))
            binding.toolBar.setBackgroundColor(resources.getColor(R.color.black_bg_dynamic_neutral_variant_10))
            binding.bottomNavBar.itemLayout.setBackgroundResource(R.drawable.bg_rounded_black_shape_50dp)
            binding.bottomNavBar.itemGoLive.setBackgroundResource(R.drawable.bg_rounded_live_btn_dark)
        } else {
            binding.homeContainer.setBackgroundColor(resources.getColor(R.color.search_border_color_grey))
            binding.toolBar.setBackgroundColor(resources.getColor(R.color.white))
            binding.bottomNavBar.itemLayout.setBackgroundResource(R.drawable.bg_rounded_white_shape_50dp)
            binding.bottomNavBar.itemGoLive.setBackgroundResource(R.drawable.bg_rounded_live_btn_light)
        }
    }

    private fun buttonStateChange(iconView: MaterialButton) {

        if (Utils(this).isDarkTheme()) {
            binding.bottomNavBar.itemLive.iconTint =
                ContextCompat.getColorStateList(this, R.color.white)
            binding.bottomNavBar.itemFeed.iconTint =
                ContextCompat.getColorStateList(this, R.color.white)
            binding.bottomNavBar.itemChat.iconTint =
                ContextCompat.getColorStateList(this, R.color.white)
            binding.bottomNavBar.itemSetting.iconTint =
                ContextCompat.getColorStateList(this, R.color.white)
        } else {
            binding.bottomNavBar.itemLive.iconTint =
                ContextCompat.getColorStateList(this, R.color.black)
            binding.bottomNavBar.itemFeed.iconTint =
                ContextCompat.getColorStateList(this, R.color.black)
            binding.bottomNavBar.itemChat.iconTint =
                ContextCompat.getColorStateList(this, R.color.black)
            binding.bottomNavBar.itemSetting.iconTint =
                ContextCompat.getColorStateList(this, R.color.black)
        }
        // Set icon tint for selected button
        iconView.iconTint = ContextCompat.getColorStateList(this, R.color.pink)
        //handling icons
//        if (Utils(this).isDarkTheme())
//            selectedButton.iconTint = ContextCompat.getColorStateList(this, R.color.white)
//        else
//            selectedButton.iconTint = ContextCompat.getColorStateList(this, R.color.black)
//
//        iconView.iconTint = ContextCompat.getColorStateList(this, R.color.pink)
//        selectedButton = iconView
    }
    override fun onRestart() {
        super.onRestart()
        val data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        if (!data?.image.isNullOrBlank()) {
            val imageUrl = Constance.ImageBaseUrl + data?.image
            Glide.with(this).load(imageUrl).centerCrop()
                .placeholder(getShimmerDrawableForGlide())
                .error(R.drawable.user_placeholder)
                .into(binding.profileImg)
        } else binding.profileImg.setImageResource(R.drawable.user_placeholder)
    }
}
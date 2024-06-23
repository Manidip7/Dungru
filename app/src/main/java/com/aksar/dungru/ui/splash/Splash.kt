package com.aksar.dungru.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aksar.dungru.databinding.ActivitySplashBinding
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.ui.auth.AuthActivity
import com.aksar.dungru.ui.home.HomeActivity
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.IS_LIGHT_MODE
import com.aksar.dungru.utils.extensions.slideInFromRightAnimationShow
import com.aksar.dungru.utils.extensions.slideUpAnimationShow

class Splash : AppCompatActivity() {
    private val splahTimeOut = 3000L

    private lateinit var binding: ActivitySplashBinding
    private var isDelayedOperationPosted = false
    private val DELAYED_OPERATION_KEY = "delayedOperationPosted"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        PrefManager.initialize(this)

        val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)

        savedInstanceState?.let {
            isDelayedOperationPosted = savedInstanceState.getBoolean(DELAYED_OPERATION_KEY, false)
        }

        val isNightModeEnabled = PrefManager.get().getBoolean(IS_LIGHT_MODE, true)
        setNightMode(isNightModeEnabled)

        slideInFromRightAnimationShow(this, binding.imageAppName)
        slideUpAnimationShow(this, binding.bottomView)


        if (!isDelayedOperationPosted) {
            binding.root.postDelayed({
                // User data not exists
                if (data == null || data.unique_token.isBlank() || data.email.isBlank()) {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                } else {
                    val appState = PrefManager.get().getString(PrefManager.APP_STATE, null)
                    if (!appState.isNullOrEmpty()) {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finish()
                    }else{
                        // User data exists
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
            }, splahTimeOut)
            isDelayedOperationPosted = true
        }
    }

    private fun setNightMode(isNightModeEnabled: Boolean) {
        val nightMode =
            if (isNightModeEnabled) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save the state of the flag
        outState.putBoolean(DELAYED_OPERATION_KEY, isDelayedOperationPosted)
        super.onSaveInstanceState(outState)
    }
}
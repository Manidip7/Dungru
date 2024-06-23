package com.aksar.dungru.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityAuthBinding
import com.aksar.dungru.ui.MyApp
import com.aksar.dungru.ui.auth.autfragments.ForgotPasswordFragment
import com.aksar.dungru.ui.auth.autfragments.OtpFragment
import com.aksar.dungru.ui.auth.autfragments.SetPasswordFragment
import com.aksar.dungru.ui.auth.autfragments.SignInFragment
import com.aksar.dungru.ui.home.HomeActivity
import com.aksar.dungru.utils.Constance.ForgotPassword
import com.aksar.dungru.utils.Constance.SignIn
import com.aksar.dungru.utils.Constance.SignUp
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.NetworkUtils.checkConnection
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.utils.extensions.clearFragmentBackstack
import com.aksar.dungru.utils.extensions.replaceFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val notificationRequest = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        checkPermissions()
        val appState = PrefManager.get().getString(PrefManager.APP_STATE, null)
        if (supportFragmentManager.findFragmentById(binding.authContainer.id) == null) {
            // If not added, add the SignInFragment
            when (appState) {
                SignIn -> addFragment(
                    binding.authContainer.id,
                    OtpFragment.newInstance(SignIn),
                    false,
                    SignIn
                )

                SignUp -> addFragment(
                    binding.authContainer.id,
                    OtpFragment.newInstance(SignUp),
                    false,
                    SignUp
                )

                else -> addFragment(binding.authContainer.id, SignInFragment(), false, SignIn)
            }
        }

    }

    private fun checkPermissions() {
        if (!Utils(this).isAllPermissionsGranted(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))) {
            Utils(this).requestPermission(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                notificationRequest
            )
        }
    }

    override fun onBackPressed() {
        // current fragment
        val currentFragment = supportFragmentManager.findFragmentById(R.id.authContainer)

        when (currentFragment) {
            is OtpFragment -> {
                clearFragmentBackstack()
                replaceFragment(R.id.authContainer, ForgotPasswordFragment(), false, ForgotPassword)
            }

            is ForgotPasswordFragment -> {
                // If currently on Forgot Password fragment, navigate back to previous fragment
                clearFragmentBackstack()
                replaceFragment(R.id.authContainer, SignInFragment(), false, SignIn)
            }

            is SetPasswordFragment -> {
                clearFragmentBackstack()
                replaceFragment(R.id.authContainer, ForgotPasswordFragment(), false, ForgotPassword)
            }

            else -> super.onBackPressed()
        }
    }
    override fun onStart() {
        super.onStart()
        MyApp.setCurrentActivity(this)
        checkConnection(this)
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
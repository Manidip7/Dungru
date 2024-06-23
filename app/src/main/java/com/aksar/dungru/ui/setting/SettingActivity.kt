package com.aksar.dungru.ui.setting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivitySettingBinding
import com.aksar.dungru.databinding.LogoutDialogBinding
import com.aksar.dungru.ui.MyApp
import com.aksar.dungru.ui.auth.AuthActivity
import com.aksar.dungru.ui.payment.PaymentSettingActivity
import com.aksar.dungru.ui.profile.EditProfileActivity
import com.aksar.dungru.ui.wallet.WalletActivity
import com.aksar.dungru.ui.wallet.addcoin.GetCoinActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.Constance.Feedback
import com.aksar.dungru.utils.Constance.Flow
import com.aksar.dungru.utils.Constance.GeneralSetting
import com.aksar.dungru.utils.Constance.HelpCenter
import com.aksar.dungru.utils.Constance.Notification
import com.aksar.dungru.utils.Constance.SetPassword
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.IS_LIGHT_MODE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivitySettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)

        if(Firebase.auth.currentUser!=null){
            binding.btnResetPassword.visibility = View.GONE
        }
        binding.btnLightModeOnOff.isChecked = PrefManager.get().getBoolean(IS_LIGHT_MODE, true)

        binding.btnLightModeOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                PrefManager.get().save(IS_LIGHT_MODE, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                PrefManager.get().save(IS_LIGHT_MODE, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        setContentView(binding.root)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()

            binding.btnGeneralSetting -> {
                val intent = Intent(this, SettingLoaderActivity::class.java)
                intent.putExtra(Flow, GeneralSetting)
                startActivity(intent)
            }

            binding.btnInviteFriends -> shareProfileDetails()

            binding.btnEditProfile -> startActivity(Intent(this, EditProfileActivity::class.java))

            binding.btnResetPassword -> {
                val intent = Intent(this, SettingLoaderActivity::class.java)
                intent.putExtra(Flow, SetPassword)
                startActivity(intent)
            }

            binding.btnNotificationSetting -> {
                val intent = Intent(this, SettingLoaderActivity::class.java)
                intent.putExtra(Flow, Notification)
                startActivity(intent)
            }

            binding.btnPaymentOptionSetting -> startActivity(
                Intent(
                    this,
                    PaymentSettingActivity::class.java
                )
            )

            binding.btnCoinSetting -> startActivity(Intent(this, GetCoinActivity::class.java))

            binding.btnWalletSetting -> startActivity(Intent(this, WalletActivity::class.java))

            binding.btnHelpCenter -> {
                val intent = Intent(this, SettingLoaderActivity::class.java)
                intent.putExtra(Flow, HelpCenter)
                startActivity(intent)
            }

            binding.btnHistorySetting -> {
                startActivity(Intent(this, TransactionActivity::class.java))
            }

            binding.btnFeedback -> {
                val intent = Intent(this, SettingLoaderActivity::class.java)
                intent.putExtra(Flow, Feedback)
                startActivity(intent)
            }

            binding.btnLogout -> showLogOutDialog()

        }
    }

    private fun showLogOutDialog() {
        val dialog = Dialog(this)
        val dialogBinding = LogoutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(getDrawable(R.drawable.bg_transparent))
        dialog.show()
        dialogBinding.btnYes.setOnClickListener {
            PrefManager.get().clearPrefs()
            if(Firebase.auth.currentUser!=null){
                Firebase.auth.signOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(Constance.WebClientId)
                    .requestEmail()
                    .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                mGoogleSignInClient.signOut().addOnCompleteListener{
                    val intent = Intent(this@SettingActivity, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }
            }else{
                val intent = Intent(this@SettingActivity, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                dialog.dismiss()
                finish()
            }
        }
        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonHoverEffect(view: View) {

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    v.elevation = resources.getDimension(R.dimen._10dp)
                    v.setBackgroundColor(resources.getColor(R.color.hint_color_grey))
                    true
                }

                MotionEvent.ACTION_HOVER_EXIT -> {
                    v.elevation = resources.getDimension(R.dimen._0dp)
                    v.setBackgroundColor(Color.TRANSPARENT)
                    true
                }

                else -> {

                    Log.d("HoverEffect", "${event.action}")
                    false
                }
            }
        }
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

    override fun onStart() {
        super.onStart()
        NetworkUtils.checkConnection(this)
    }

}
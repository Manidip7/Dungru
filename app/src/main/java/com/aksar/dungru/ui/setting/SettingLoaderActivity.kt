package com.aksar.dungru.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivitySettingLoaderBinding
import com.aksar.dungru.ui.MyApp
import com.aksar.dungru.ui.auth.autfragments.ForgotPasswordFragment
import com.aksar.dungru.ui.auth.autfragments.OtpFragment
import com.aksar.dungru.ui.auth.autfragments.SetPasswordFragment
import com.aksar.dungru.ui.auth.autfragments.SignInFragment
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.ui.setting.settingfragments.FeedbackFragment
import com.aksar.dungru.ui.setting.settingfragments.GeneralSettingFragment
import com.aksar.dungru.ui.help_support.HelpFragment
import com.aksar.dungru.ui.setting.settingfragments.EmailUpdateOtpFragment
import com.aksar.dungru.ui.setting.settingfragments.ResetPasswordFragment
import com.aksar.dungru.ui.setting.settingfragments.NotificationSettingFragment
import com.aksar.dungru.ui.setting.settingfragments.PrivacySecurityFragment
import com.aksar.dungru.ui.setting.settingfragments.UpdateEmailFragment
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.Constance.Feedback
import com.aksar.dungru.utils.Constance.GeneralSetting
import com.aksar.dungru.utils.Constance.HelpCenter
import com.aksar.dungru.utils.Constance.Notification
import com.aksar.dungru.utils.Constance.SetPassword
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.extensions.clearFragmentBackstack
import com.aksar.dungru.utils.extensions.replaceFragment

class SettingLoaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingLoaderBinding
    private lateinit var flow: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingLoaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        flow = intent.getStringExtra(Constance.Flow).toString()

        if (supportFragmentManager.findFragmentById(binding.settingContainer.id) == null) {

            when (flow) {
                GeneralSetting -> addFragment(binding.settingContainer.id, GeneralSettingFragment(), false)

                SetPassword -> addFragment(binding.settingContainer.id, ResetPasswordFragment(), false)

                Notification -> addFragment(binding.settingContainer.id, NotificationSettingFragment(),false)

                HelpCenter->addFragment(binding.settingContainer.id, HelpFragment(),false)

                Feedback->addFragment(binding.settingContainer.id,FeedbackFragment(),false)

            }
        }
    }
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.setting_container)
        when (currentFragment) {
            is EmailUpdateOtpFragment -> {
                clearFragmentBackstack()
                replaceFragment(R.id.setting_container, PrivacySecurityFragment(), true, "PrivacySecurity")
            }
            else -> super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        NetworkUtils.checkConnection(this)
    }
}
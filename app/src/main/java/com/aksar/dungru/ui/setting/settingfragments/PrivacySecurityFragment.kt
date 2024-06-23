package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentPrivacySecurityBinding
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.SettingData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class PrivacySecurityFragment : Fragment() {

    lateinit var binding:FragmentPrivacySecurityBinding
    private lateinit var viewModel: SettingViewModel
    private var data: LoggedUserData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPrivacySecurityBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(RetrofitClient.apiService)))[SettingViewModel::class.java]
        data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        initObserver()

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

//        binding.btnChat.setOnClickListener {
//            replaceFragment(R.id.setting_container,ChatSettingFragment(),true,"chats")
//        }

        val savedSettingData = PrefManager.get().getObject(PrefManager.GENERAL_SETTING, SettingData::class.java)

        binding.btnLastTimeSeen.isChecked = savedSettingData?.last_seen == "1"

        binding.btnLastTimeSeen.setOnCheckedChangeListener { buttonView, isChecked ->

            val settingValue = if (isChecked) "1" else "0"
            viewModel.setSetting(
                SetSettingReq(
                    data?.user_id.toString(),
                    data?.unique_token.toString(),
                    settingValue.toInt(),
                    null,
                    null, // Convert to Int for consistency with your model
                    null,
                    null
                )
            )
            PrefManager.get().save(PrefManager.GENERAL_SETTING, SettingData(settingValue, savedSettingData?.location, savedSettingData?.one_click_gifting, savedSettingData?.posts_moments, savedSettingData?.profile_picture))
        }

        binding.btnChangeEmail.setOnClickListener {
            replaceFragment(R.id.setting_container,UpdateEmailFragment(),true,"UpdateEmail")
        }

        binding.btnPostAndMoment.setOnClickListener {
            replaceFragment(R.id.setting_container,PostMomentsFragment(),true,"PostMoment")
        }
        return binding.root
    }

    private fun initObserver() {
        viewModel.setResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnLastTimeSeen.isClickable = false
                }
                is NetworkResponse.Success -> {
                    binding.btnLastTimeSeen.isClickable = true
                    showToast(response.data?.message.toString(), false)
                }
                is NetworkResponse.Error -> {
                    binding.btnLastTimeSeen.isClickable = true
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

}
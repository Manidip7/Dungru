package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aksar.dungru.R
import com.aksar.dungru.databinding.FragmentGeneralSettingBinding
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.SettingData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.PrefManager.Companion.GENERAL_SETTING
import com.aksar.dungru.utils.PrefManager.Companion.USER_DATA
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class GeneralSettingFragment : Fragment() {
    private lateinit var binding: FragmentGeneralSettingBinding
    private lateinit var viewModel: SettingViewModel
    private var data: LoggedUserData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentGeneralSettingBinding.inflate(layoutInflater)


        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(RetrofitClient.apiService)))[SettingViewModel::class.java]
        data = PrefManager.get().getObject(USER_DATA, LoggedUserData::class.java)
        initObserver()


        binding.btnPrivacyAndSecurity.setOnClickListener {
            replaceFragment(R.id.setting_container, PrivacySecurityFragment(), true, "PrivacySecurity")
        }

        binding.btnBlockUser.setOnClickListener {
            replaceFragment(R.id.setting_container, BlockUserFragment(), true, "BlockUser")
        }

        val savedSettingData = PrefManager.get().getObject(GENERAL_SETTING, SettingData::class.java)


        binding.btnOneClickGifting.isChecked = savedSettingData?.one_click_gifting == "1"

        binding.btnOneClickGifting.setOnCheckedChangeListener { buttonView, isChecked ->

                val settingValue = if (isChecked) "1" else "0"
                viewModel.setSetting(
                    SetSettingReq(
                        data?.user_id.toString(),
                        data?.unique_token.toString(),
                        null,
                        null,
                        settingValue.toInt(), // Convert to Int for consistency with your model
                        null,
                        null
                    )
                )
                PrefManager.get().save(GENERAL_SETTING, SettingData(savedSettingData?.last_seen, savedSettingData?.location, settingValue, savedSettingData?.posts_moments, savedSettingData?.profile_picture))
            }


        binding.btnLocation.isChecked = savedSettingData?.location == "1"
        binding.btnLocation.setOnCheckedChangeListener                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      { buttonView, isChecked ->
            val settingValue = if (isChecked) "1" else "0"
            viewModel.setSetting(
                SetSettingReq(
                    data?.user_id.toString(),
                    data?.unique_token.toString(),
                    null,
                    settingValue.toInt(),
                    null, // Convert to Int for consistency with your model
                    null,
                    null
                )
            )
            PrefManager.get().save(GENERAL_SETTING, SettingData(savedSettingData?.last_seen, settingValue, savedSettingData?.one_click_gifting, savedSettingData?.posts_moments, savedSettingData?.profile_picture))
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root

    }

    private fun initObserver() {
        viewModel.setResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnOneClickGifting.isClickable = false
                }

                is NetworkResponse.Success -> {
                    binding.btnOneClickGifting.isClickable = true
                    showToast(response.data?.message.toString(), false)
                }

                is NetworkResponse.Error -> {
                    binding.btnOneClickGifting.isClickable = true
                    showToast(response.message.toString(), false)
                }
            }
        }
    }
}
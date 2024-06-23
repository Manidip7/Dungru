package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.databinding.FragmentPostMomentsBinding
import com.aksar.dungru.databinding.PostScletingDiglogBinding
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.SettingData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog

class PostMomentsFragment : Fragment() {

    lateinit var binding:FragmentPostMomentsBinding
    private lateinit var viewModel: SettingViewModel
    private var data: LoggedUserData? = null
    var selectedRadioButton :Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPostMomentsBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(RetrofitClient.apiService)))[SettingViewModel::class.java]
        data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnPost.setOnClickListener {
            val diglogBinding = PostScletingDiglogBinding.inflate(layoutInflater)
            val dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(diglogBinding.root)


            val savedSettingData = PrefManager.get().getObject(PrefManager.GENERAL_SETTING, SettingData::class.java)

            diglogBinding.btnRadioButtonAll.isChecked = savedSettingData?.posts_moments == "1"
            diglogBinding.btnRadioButtonOnlyFriend.isChecked = savedSettingData?.posts_moments == "0"

            diglogBinding.btnRadioGroup.setOnCheckedChangeListener  { group, checkedId ->

                when (checkedId){
                   diglogBinding.btnRadioButtonAll.id-> {
                       selectedRadioButton = 1

                       viewModel.setSetting(
                           SetSettingReq(
                               data?.user_id.toString(),
                               data?.unique_token.toString(),
                               null,
                               null,
                               null, // Convert to Int for consistency with your model
                               selectedRadioButton,
                               null
                           )
                       )
                       PrefManager.get().save(PrefManager.GENERAL_SETTING, SettingData(savedSettingData?.last_seen, savedSettingData?.location, savedSettingData?.one_click_gifting, selectedRadioButton.toString(), savedSettingData?.profile_picture))
                   }
                    diglogBinding.btnRadioButtonOnlyFriend.id-> {
                        selectedRadioButton = 0

                        viewModel.setSetting(
                            SetSettingReq(
                                data?.user_id.toString(),
                                data?.unique_token.toString(),
                                null,
                                null,
                                null, // Convert to Int for consistency with your model
                                selectedRadioButton,
                                null
                            )
                        )
                        PrefManager.get().save(PrefManager.GENERAL_SETTING, SettingData(savedSettingData?.last_seen, savedSettingData?.location, savedSettingData?.one_click_gifting, selectedRadioButton.toString(), savedSettingData?.profile_picture))
                    }
                }
            }
            dialog.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {

                }
                is NetworkResponse.Success -> {

                    showToast(response.data?.message.toString(), false)
                }
                is NetworkResponse.Error -> {

                    showToast(response.message.toString(), false)
                }
            }
        }
    }
}
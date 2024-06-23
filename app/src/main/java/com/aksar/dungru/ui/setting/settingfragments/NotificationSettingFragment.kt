package com.aksar.dungru.ui.setting.settingfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import com.aksar.dungru.databinding.FragmentNotificationSettingBinding
import com.aksar.dungru.models.request.NotificationReq
import com.aksar.dungru.models.response.InAppNotifications
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class NotificationSettingFragment : Fragment() {

    private lateinit var binding: FragmentNotificationSettingBinding
    private lateinit var viewModel: SettingViewModel
    private var data: LoggedUserData? = null
    private var notificationsData: InAppNotifications? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationSettingBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity(), ViewModelFactory(Repository(RetrofitClient.apiService)))[SettingViewModel::class.java]

        data = data ?: PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        notificationsData = notificationsData ?: PrefManager.get().getObject(PrefManager.NOTIFICATION_SETTING, InAppNotifications::class.java)

        //Initial switch set
        binding.apply {
            btnSwitchPostStory.isChecked = notificationsData?.story == "1"
            btnLikes.isChecked = notificationsData?.likes == "1"
            btnComments.isChecked = notificationsData?.comment == "1"
            btnSomeoneFollows.isChecked = notificationsData?.follow_you == "1"
            btnGoesLive.isChecked = notificationsData?.follower_live == "1"
            btnRepliesComment.isChecked = notificationsData?.comment_reply == "1"

            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
        //Switch functionality
        setSwitchListener(binding.btnSwitchPostStory) { story -> notificationsData?.story = story }
        setSwitchListener(binding.btnLikes) { likes -> notificationsData?.likes = likes }
        setSwitchListener(binding.btnComments) { comment -> notificationsData?.comment = comment }
        setSwitchListener(binding.btnSomeoneFollows) { followYou -> notificationsData?.follow_you = followYou }
        setSwitchListener(binding.btnGoesLive) { followerLive -> notificationsData?.follower_live = followerLive }
        setSwitchListener(binding.btnRepliesComment) { commentReply -> notificationsData?.comment_reply = commentReply }

        return binding.root
    }

    private fun setSwitchListener(switch: CompoundButton, updateValue: (String) -> Unit) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            val notificationsValue = if (isChecked) "1" else "0"
            updateValue(notificationsValue)
            viewModel.notificaton_Setting(
                NotificationReq(
                    story = if (switch == binding.btnSwitchPostStory) notificationsValue.toInt() else null,
                    likes = if (switch == binding.btnLikes) notificationsValue.toInt() else null,
                    comment = if (switch == binding.btnComments) notificationsValue.toInt() else null,
                    follow_you = if (switch == binding.btnSomeoneFollows) notificationsValue.toInt() else null,
                    follower_live = if (switch == binding.btnGoesLive) notificationsValue.toInt() else null,
                    comment_reply = if (switch == binding.btnRepliesComment) notificationsValue.toInt() else null,
                    user_id =  data!!.user_id.toInt()
                )
            )
            PrefManager.get().save(PrefManager.NOTIFICATION_SETTING, notificationsData!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.notificatonResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {}
                is NetworkResponse.Success -> showToast(response.data?.message.toString(), false)
                is NetworkResponse.Error -> showToast(response.message.toString(), false)
                else -> { /* Ignore other cases */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notificationsData = null
        data = null
    }
}

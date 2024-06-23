package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.BlockOrUnblockUserReq
import com.aksar.dungru.models.request.NotificationReq
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.response.BlockUserListRes
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.NotificationRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: Repository) : ViewModel() {
    private val set_response: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> =
        MutableLiveData()
    val setResponse get() = set_response

    private val notificaton_response: MutableLiveData<NetworkResponse<NotificationRes>> =
        MutableLiveData()
    val notificatonResponse get() = notificaton_response

    private val block_unblock_user: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> =
        MutableLiveData()
    val blockUnblockUserResponse get() = block_unblock_user

    private val fetch_block_user_list: MutableLiveData<NetworkResponse<BlockUserListRes>> = MutableLiveData()
    val fetchBlockUserListResponse get() = fetch_block_user_list

    fun setSetting(setSettingReq: SetSettingReq) = viewModelScope.launch {
        set_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.setSetting(setSettingReq)
            if (responseData.isSuccessful) {
                when (responseData.body()?.respCode.toString()) {
                    "1" -> set_response.value = NetworkResponse.Success(responseData.body())

                    "-1" -> set_response.value =
                        NetworkResponse.Error(responseData.body()?.message.toString())
                }
            } else set_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            set_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Set setting error: ${e.message}")
        }
    }

    fun notificaton_Setting(notificationReq: NotificationReq) = viewModelScope.launch {
        notificaton_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.notificationSetting(notificationReq)
            if (responseData.isSuccessful) {
                when (responseData.body()?.respCode.toString()) {
                    "1" -> notificaton_response.value = NetworkResponse.Success(responseData.body())

                    "-1" -> notificaton_response.value =
                        NetworkResponse.Error(responseData.body()?.message.toString())
                }
            } else notificaton_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            notificaton_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Notification setting error: ${e.message}")
        }
    }

    fun blockOrUnblockUser(blockOrUnblockUserReq: BlockOrUnblockUserReq) = viewModelScope.launch {
        block_unblock_user.value = NetworkResponse.Loading()
        try {
            val responseData = repository.blockOrUnblockUser(blockOrUnblockUserReq)
            if (responseData.isSuccessful)
                block_unblock_user.value = NetworkResponse.Success(responseData.body())
            else
                block_unblock_user.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            block_unblock_user.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Block unblock user error: ${e.message}")
        }
    }
    fun fetchBlockUserList(baseUserIDReq: BaseUserIDReq) = viewModelScope.launch {
        fetch_block_user_list.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchBlockUserList(baseUserIDReq)
            if (responseData.isSuccessful)
                fetch_block_user_list.value = NetworkResponse.Success(responseData.body())
            else
                fetch_block_user_list.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            fetch_block_user_list.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch block user error: ${e.message}")
        }
    }
}
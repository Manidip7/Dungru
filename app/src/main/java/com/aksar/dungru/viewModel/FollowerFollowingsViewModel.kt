package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.AddOrRemoveFollowerReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.FetchFeedPostsRes
import com.aksar.dungru.models.response.FollowerFollowingsListRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FollowerFollowingsViewModel (private val repository: Repository): ViewModel() {
    private val add_remove_follower: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val addRemoveFollowerResponse get() = add_remove_follower

    private val follower_list: MutableLiveData<NetworkResponse<FollowerFollowingsListRes>> = MutableLiveData()
    val fetchFollowerResponse get() = follower_list

    private val following_list: MutableLiveData<NetworkResponse<FollowerFollowingsListRes>> = MutableLiveData()
    val fetchFollowingResponse get() = following_list

    fun addOrRemoveFollower(addOrRemoveFollowerReq: AddOrRemoveFollowerReq) = viewModelScope.launch {
        add_remove_follower.value = NetworkResponse.Loading()
        try {
            val responseData = repository.addOrRemoveFollower(addOrRemoveFollowerReq)
            if(responseData.isSuccessful)
                add_remove_follower.value = NetworkResponse.Success(responseData.body())
            else
                add_remove_follower.value = NetworkResponse.Error("Server error!")
        }catch (e:Exception){
            add_remove_follower.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Add or remove follower error: $e ")
        }
    }

    fun fetchFollowerList(baseUserIDReq: BaseUserIDReq) = viewModelScope.launch {
        follower_list.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchFollowerList(baseUserIDReq)
            if(responseData.isSuccessful)
                follower_list.value = NetworkResponse.Success(responseData.body())
            else
                follower_list.value = NetworkResponse.Error("Server error!")
        }catch (e: Exception) {
            follower_list.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch follower list error: $e")
        }
    }

    fun fetchFollowingList(baseUserIDReq: BaseUserIDReq) = viewModelScope.launch {
        following_list.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchFollowingList(baseUserIDReq)
            if(responseData.isSuccessful)
                following_list.value = NetworkResponse.Success(responseData.body())
            else
                following_list.value = NetworkResponse.Error("Server error!")
        }catch (e: Exception) {
            following_list.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch following list error: $e")
        }
    }
}
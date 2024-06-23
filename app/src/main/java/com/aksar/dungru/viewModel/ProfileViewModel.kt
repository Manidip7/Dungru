package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.BaseUserIDAndTokenReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.FetchProfilePostsReq
import com.aksar.dungru.models.request.ProfileReq
import com.aksar.dungru.models.request.UpdateEmailReq
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.FetchProfilePostsRes
import com.aksar.dungru.models.response.ProfileRes
import com.aksar.dungru.models.response.UpdateEmailRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileViewModel(private val repository: Repository) : ViewModel() {
    private val profile_response: MutableLiveData<NetworkResponse<ProfileRes>> = MutableLiveData()
    val profileResponse get() = profile_response

    private val profile_posts_response: MutableLiveData<NetworkResponse<FetchProfilePostsRes>> = MutableLiveData()
    val profilePostsResponse get() = profile_posts_response

    private val image_response: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val imageResponse get() = image_response

    private val updateEmail_response: MutableLiveData<NetworkResponse<UpdateEmailRes>> = MutableLiveData()
    val updateEmailResponse get() = updateEmail_response

    fun fetchUserData(baseUserIDAndTokenReq: BaseUserIDAndTokenReq) = viewModelScope.launch {
        profile_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchUserData(baseUserIDAndTokenReq)
            if (responseData.isSuccessful) {
                when (responseData.body()?.respCode.toString()) {
                    "1" -> {
                        PrefManager.get().save(PrefManager.USER_DATA, responseData.body()?.data)
                        profile_response.value = NetworkResponse.Success(responseData.body())
                    }
                    "-1" -> profile_response.value = NetworkResponse.Error(responseData.body()?.message)

                }
            } else profile_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            profile_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch user data error: ${e.message}")
        }
    }

    fun fetchProfilePosts(fetchProfilePostsReq: FetchProfilePostsReq) = viewModelScope.launch {
        profile_posts_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchProfilePosts(fetchProfilePostsReq)
            if (responseData.isSuccessful) {
                when (responseData.body()?.respCode.toString()) {
                    "1" -> profile_posts_response.value = NetworkResponse.Success(responseData.body())
                    "-1" -> profile_posts_response.value = NetworkResponse.Error(responseData.body()?.message)
                }
            } else profile_posts_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            profile_posts_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch profile posts error: ${e.message}")
        }
    }


    fun updateProfile(profileReq: ProfileReq) = viewModelScope.launch {
        profile_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.updateProfile(profileReq)
            if (responseData.isSuccessful) {
                when (responseData.body()?.respCode.toString()) {
                    "1" -> fetchUserData(BaseUserIDAndTokenReq(profileReq.user_id,profileReq.unique_token))
                    "-1" -> profile_response.value = NetworkResponse.Error(responseData.body()?.message)
                }
            } else profile_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            profile_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Update profile error: ${e.message}")
        }
    }

    fun uploadProfileImage(file: MultipartBody.Part, userId: RequestBody) = viewModelScope.launch {
        image_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.uploadProfileImage(file, userId)
            if (responseData.isSuccessful)
                image_response.value = NetworkResponse.Success(responseData.body())
            else
                image_response.value = NetworkResponse.Error("Server error!")

        } catch (e: Exception) {
            image_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Upload profile Image error: $e")
        }
    }


    fun updateEmail(updateEmailReq: UpdateEmailReq) = viewModelScope.launch {
        updateEmail_response.value = NetworkResponse.Loading()

        try {
            val responseData = repository.updateEmail(updateEmailReq)

            if (responseData.isSuccessful) {

                when (responseData.body()?.respCode.toString()) {
                    "1" -> updateEmail_response.value = NetworkResponse.Success(responseData.body())
                    "-1" -> updateEmail_response.value = NetworkResponse.Error(responseData.body()?.message)
                }
            } else updateEmail_response.value = NetworkResponse.Error("Server error!")
        } catch (e: Exception) {
            updateEmail_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Update email error: ${e.message}")
        }
    }

}

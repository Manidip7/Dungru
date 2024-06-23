package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.SignInReq
import com.aksar.dungru.models.request.SignUpReq
import com.aksar.dungru.models.request.SocialLogInReq
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.models.response.SignUpRes
import com.aksar.dungru.models.response.SocialLoginRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.NetworkUtils
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: Repository) : ViewModel() {
    private val social_log_in: MutableLiveData<NetworkResponse<SocialLoginRes>> = MutableLiveData()
    val socialLogInResponse: LiveData<NetworkResponse<SocialLoginRes>> get() = social_log_in

    private val sign_up : MutableLiveData<NetworkResponse<SignUpRes>> = MutableLiveData()
    val signUpResponse : LiveData<NetworkResponse<SignUpRes>> get() = sign_up

    private val log_in: MutableLiveData<NetworkResponse<LoggedUserData>> = MutableLiveData()
    val logInResponse: LiveData<NetworkResponse<LoggedUserData>> get() = log_in

    fun socialLogIn(socialLogInReq: SocialLogInReq) = viewModelScope.launch {
        social_log_in.value = NetworkResponse.Loading()
        try {
            if(NetworkUtils.IS_CONNECTED){
                val responseData = repository.socialLogin(socialLogInReq)
                if (responseData.isSuccessful)
                    when (responseData.body()?.respCode) {
                        1 -> social_log_in.value = NetworkResponse.Success(responseData.body())
                        -1 -> social_log_in.value = NetworkResponse.Error(responseData.body()?.message)
                    }
                else
                    social_log_in.value = NetworkResponse.Error(responseData.body()?.message)
            }else{
                social_log_in.value = NetworkResponse.Error("Network error")
            }
        } catch (e: Exception) {
            social_log_in.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Social login error: ${e.message}")
        }
    }

    fun registerUser(signUpReq: SignUpReq) = viewModelScope.launch {
        sign_up.value = NetworkResponse.Loading()
        try {
            val responseData = repository.signUp(signUpReq)
            if (responseData.isSuccessful)
                when (responseData.body()?.respCode) {
                    1 -> sign_up.value = NetworkResponse.Success(responseData.body())
                    -1 -> sign_up.value = NetworkResponse.Error(responseData.body()?.message)
                }
            else
                sign_up.value = NetworkResponse.Error(responseData.body()?.message)
        } catch (e: Exception) {
            sign_up.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Sign up error: ${e.message}")
        }
    }

    fun fetchUser(signInReq: SignInReq) = viewModelScope.launch {
        log_in.value = NetworkResponse.Loading()
        try {
            val responseData = repository.signIn(signInReq)
            if (responseData.isSuccessful) {
                Log.d("DATA", "${responseData.body()?.data}")
                when (responseData.body()?.respCode.toString()) {
                    "1" -> {
                        log_in.value = NetworkResponse.Success(responseData.body()?.data)
                    }
                    "-1" -> {
                        log_in.value = NetworkResponse.Error(responseData.body()?.message.toString())
                    }
                }
            }else{
                log_in.value = NetworkResponse.Error("Server error!")
            }
        } catch (e: Exception) {
            log_in.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Sign in error: ${e.message}")
        }
    }
}
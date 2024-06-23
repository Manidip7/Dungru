package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.SetPasswordReq
import com.aksar.dungru.models.request.ResetPassReq
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.ResetPassRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import kotlinx.coroutines.launch

class PasswordViewModel(private val repository: Repository):ViewModel() {
    private val reset_pwd_response: MutableLiveData<NetworkResponse<ResetPassRes>> = MutableLiveData()
    val resetPwdResponse get() = reset_pwd_response

    private val forgot_pwd_response : MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val forgotPwdResponse get() = forgot_pwd_response

    fun resetPassword(resetPassReq: ResetPassReq) = viewModelScope.launch {
        reset_pwd_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.resetPassword(resetPassReq)
            if (responseData.isSuccessful)
                when(responseData.body()?.respCode){
                    1 -> reset_pwd_response.value = NetworkResponse.Success(responseData.body())
                    -1 -> reset_pwd_response.value = NetworkResponse.Error(responseData.body()?.response)
                }
            else
                reset_pwd_response.value = NetworkResponse.Error(responseData.body()?.response)

        } catch (e: Exception) {
            reset_pwd_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Reset password error: ${e.message}")
        }
    }

    fun setNewPassword(setPasswordReq: SetPasswordReq) = viewModelScope.launch {
        forgot_pwd_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.setNewPassword(setPasswordReq)
            if(responseData.isSuccessful)
                when(responseData.body()?.respCode){
                    1 ->  forgot_pwd_response.value = NetworkResponse.Success(responseData.body())
                    -1 -> forgot_pwd_response.value = NetworkResponse.Error(responseData.body()?.message)
                }
            else
                forgot_pwd_response.value = NetworkResponse.Error(responseData.body()?.message)
        }catch (e: Exception){
            reset_pwd_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Forgot password error: ${e.message}")
        }
    }

}
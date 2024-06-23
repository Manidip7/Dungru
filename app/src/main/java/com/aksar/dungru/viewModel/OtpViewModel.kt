package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.models.request.UpdateEmailOtpReq
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.OtpRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import kotlinx.coroutines.launch

class OtpViewModel(private val repository: Repository) :  ViewModel() {
    private val otp_resend_response : MutableLiveData<NetworkResponse<OtpRes>> = MutableLiveData()
    val otpResendResponse get() = otp_resend_response

    private val otp_validation_response : MutableLiveData<NetworkResponse<OtpRes>> = MutableLiveData()
    val otpValidationResponse get() = otp_validation_response

    private val email_update_otp_validation : MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val emailUpdateOtpValidation get() = email_update_otp_validation

    //For otp generate
    fun getOtp(otpReq: OtpReq) = viewModelScope.launch {
        otp_resend_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.getOtp(otpReq)
            if (responseData.isSuccessful)
                when (responseData.body()?.respCode) {
                    1 -> otp_resend_response.value = NetworkResponse.Success(responseData.body())
                    -1 -> otp_resend_response.value = NetworkResponse.Error(responseData.body()?.response)
                }
            else
                otp_resend_response.value = NetworkResponse.Error(responseData.body()?.response)
        } catch (e: Exception) {
            otp_resend_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Otp error: ${e.message}")
        }
    }

    //For Otp validation
    fun otpVerification(otpReq: OtpReq) = viewModelScope.launch {
        otp_validation_response.value = NetworkResponse.Loading()
        try {
            val responseData = repository.otpVerification(otpReq)
            if(responseData.isSuccessful) {
                when (responseData.body()?.respCode) {
                    1 -> otp_validation_response.value = NetworkResponse.Success(responseData.body())
                    -1 -> otp_validation_response.value = NetworkResponse.Error(responseData.body()?.response)
                }
            } else
                otp_validation_response.value = NetworkResponse.Error(responseData.body()?.response)

        }catch (e:Exception){
            otp_validation_response.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Otp error: ${e.message}")
        }
    }

    //For Email update Otp validation
    fun updateEmailOtpVerification(updateEmailOtpReq: UpdateEmailOtpReq) = viewModelScope.launch {
        email_update_otp_validation.value = NetworkResponse.Loading()
        try {
            val responseData = repository.updateEmailOtpValidation(updateEmailOtpReq)
            if(responseData.isSuccessful) {
                when (responseData.body()?.respCode) {
                    1 -> email_update_otp_validation.value = NetworkResponse.Success(responseData.body())
                    -1 -> email_update_otp_validation.value = NetworkResponse.Error(responseData.body()?.message)
                }
            } else
                email_update_otp_validation.value = NetworkResponse.Error(responseData.body()?.message)

        }catch (e:Exception){
            email_update_otp_validation.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Otp error: ${e.message}")
        }
    }
}
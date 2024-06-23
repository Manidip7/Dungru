package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class UpdateEmailOtpReq(
    val email: String,
    val otp: String,
    val user_id: String?
)
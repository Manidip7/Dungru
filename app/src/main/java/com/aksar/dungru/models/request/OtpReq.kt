package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class OtpReq(
    val email: String,
    val otp: String?
)
package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class OtpRes(
    val respCode: Int,
    val response: String,
    val status: String
)
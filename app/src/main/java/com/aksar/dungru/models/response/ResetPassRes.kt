package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class ResetPassRes(
    val respCode: Int,
    val response: String
)
package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class SignUpReq(
    val email: String,
    val password: String,
    val username: String
)
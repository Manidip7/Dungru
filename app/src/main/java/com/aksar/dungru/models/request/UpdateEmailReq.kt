package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class UpdateEmailReq(
    val email: String,
    val unique_token: String,
    val user_id: String
)
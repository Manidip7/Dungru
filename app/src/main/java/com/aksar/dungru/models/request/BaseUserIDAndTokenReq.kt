package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class BaseUserIDAndTokenReq(
    val user_id: String,
    val unique_token: String
)
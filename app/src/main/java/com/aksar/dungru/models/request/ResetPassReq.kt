package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class ResetPassReq(
    val new_password: String,
    val old_password: String,
    val unique_token: String,
    val user_id: Int
)
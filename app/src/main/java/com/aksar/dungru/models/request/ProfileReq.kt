package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class ProfileReq(
    val unique_token: String,
    val user_id: String,
    val email: String?,
    val unique_name:String?,
    val username: String?,
    val dob: String?,
    val gender: String?
)
package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class SocialLogInReq(
    val id: String,
    val email: String,
    val name: String,
    val social_type :String
)
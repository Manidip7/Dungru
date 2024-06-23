package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class SocialLoginRes(
    val `data`: LoggedUserData,
    val respCode: Int,
    val message: String
)
package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class ProfileRes(
    val `data`: LoggedUserData,
    val message: String,
    val respCode: Int
)
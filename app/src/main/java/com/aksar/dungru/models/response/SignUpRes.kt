package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class SignUpRes(
    val respCode: Int,
    val message: String,
    val `data`: LoggedUserData,
)
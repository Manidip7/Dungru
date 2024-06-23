package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class UpdateEmailRes(
    val `data`: String,
    val message: String,
    val respCode: Int
)
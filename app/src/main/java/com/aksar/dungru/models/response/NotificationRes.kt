package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class NotificationRes(
    val `data`: CommentData,
    val message: String,
    val respCode: Int
)
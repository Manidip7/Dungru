package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class NotificationReq(
    val comment: Int?,
    val comment_reply: Int?,
    val follow_you: Int?,
    val follower_live: Int?,
    val likes: Int?,
    val story: Int?,
    val user_id: Int?
)
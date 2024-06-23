package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class AddCommentReq(
    val comments: String,
    val post_id: String,
    val user_id: String
)
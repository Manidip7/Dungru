package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class PostLikeDislikeReq(
    val user_id: String,
    val post_id:String
)
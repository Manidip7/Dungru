package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class AddOrRemoveFollowerReq(
    val user_id: String,
    val following_id: String
)
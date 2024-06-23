package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class BlockUserListRes(
    val `data`: List<BlockUserData>,
    val message: String,
    val respCode: Int
)
@Keep
data class BlockUserData(
    val image: String,
    val user_id: String,
    val username: String
)
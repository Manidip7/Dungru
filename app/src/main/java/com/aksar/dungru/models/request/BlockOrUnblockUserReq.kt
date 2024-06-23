package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class BlockOrUnblockUserReq(
    val user_id: String,
    val block_user_id: String

)
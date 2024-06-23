package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class FollowerFollowingsListRes(
    val `data`: List<FollowerFollowingData>,
    val message: String,
    val respCode: Int
)
@Keep
data class FollowerFollowingData(
    val image: String,
    val user_id: String,
    val username: String
)
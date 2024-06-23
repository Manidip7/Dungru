package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class SetSettingReq(
    val user_id: String,
    val unique_token: String,
    val last_seen: Int?,
    val location: Int?,
    val one_click_gifting: Int?,
    val posts_moments: Int?,
    val profile_picture: Int?,
)
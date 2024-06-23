package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class FetchProfilePostsReq(
    val user_id: String,
    val not_logging_user: String,
)
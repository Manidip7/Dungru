package com.aksar.dungru.models.request

import androidx.annotation.Keep

@Keep
data class FetchCommentsReq(
    val post_id: String
)
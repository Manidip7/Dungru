package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class FetchCommentsRes(
    val `data`: List<CommentData>,
    val message: String,
    val respCode: Int
)
@Keep
data class CommentData(
    val comment_content: String,
    val comment_id: String,
    val created_on: String,
    val post_id: String,
    val user_id: String,
    val username: String,
    val image: String
)
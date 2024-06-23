package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class FetchFeedPostsRes(
    val `data`: List<FeedPosts>,
    val respCode: Int
)
@Keep
data class FeedPosts(
    var isFollowed: Boolean,
    var isLiked: Boolean,
    val caption: String,
    val comment_count: String,
    val id: String,
    var like_count: String,
    val post_id: String,
    val post_name: String,
    val post_type: String,
    val post_url: String,
    val share_count: String,
    val status: String,
    val subscription: String,
    val uploaded_on: String,
    val user_id: String,
    val views: String,
    val username: String,
    val image: String,
)
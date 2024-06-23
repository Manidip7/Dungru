package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class FetchProfilePostsRes(
    val `data`: FeedData,
    val message: String,
    val respCode: Int
)
@Keep
data class FeedData(
    val posts: List<ProfilePostData>,
    val user_data: UserFollowerData
)
@Keep
data class UserFollowerData(
    val follower_count: String,
    val following_count: String,
    val coins_earned: String,
    val username: String,
    val unique_name: String,
    val image: String,
    var isFollowed : Boolean
)
@Keep
data class ProfilePostData(
    val caption: String,
    val comment_count: String,
    val id: String,
    val like_count: String,
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
    var isLiked: Boolean
)
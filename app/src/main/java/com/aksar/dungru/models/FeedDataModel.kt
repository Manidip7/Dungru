package com.aksar.dungru.models
import java.io.Serializable

data class FeedDataModel(
    val id: String,
    val isVideo:Boolean,
    val thumbnailImg : Int,
    val creatorProfileImg:Int,
    val creatorName : String,
    val time : String,
    var isFollowed: Boolean,
    var isLiked:Boolean,
    val likeCount: String,
    val commentCount: String,
    val shareCount: String,
):Serializable

package com.aksar.dungru.models.response

import androidx.annotation.Keep

@Keep
data class SignInRes(
    val `data`: LoggedUserData,
    val respCode: Int,
    val message: String
)
@Keep
data class LoggedUserData(
    val user_id: String,
    val unique_token: String,
    val username: String?,
    val unique_name: String?,
    val usertype: String?,
    val email: String,
    val phone: String?,
    val dob: String?,
    val gender: String?,
    val added_on: String?,
    val coin_purchase: String?,
    val coin_sent: String?,
    val no_of_coins: String?,
    val coins_earned: String?,
    val email_verified: String?,
    val general_setting: SettingData?,
    val image : String?,
    val in_app_notifications: InAppNotifications?,
)
@Keep
data class SettingData(
    val last_seen: String?,
    val location: String?,
    val one_click_gifting: String?,
    val posts_moments: String?,
    val profile_picture: String?
)
@Keep
data class InAppNotifications(
    var story: String?,
    var likes: String?,
    var comment: String?,
    var follow_you: String?,
    var comment_reply: String?,
    var follower_live: String?
)
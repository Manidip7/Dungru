# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Request model classes
-keep class com.aksar.dungru.models.request.AddCommentReq.** { *; }
-keep class com.aksar.dungru.models.request.AddOrRemoveFollowerReq.** { *; }
-keep class com.aksar.dungru.models.request.BaseUserIDAndTokenReq.** { *; }
-keep class com.aksar.dungru.models.request.BaseUserIDReq.** { *; }
-keep class com.aksar.dungru.models.request.BlockOrUnblockUserReq.** { *; }
-keep class com.aksar.dungru.models.request.FetchCommentsReq.** { *; }
-keep class com.aksar.dungru.models.request.NotificationReq.** { *; }
-keep class com.aksar.dungru.models.request.OtpReq.** { *; }
-keep class com.aksar.dungru.models.request.PostLikeDislikeReq.** { *; }
-keep class com.aksar.dungru.models.request.ProfileReq.** { *; }
-keep class com.aksar.dungru.models.request.ResetPassReq.** { *; }
-keep class com.aksar.dungru.models.request.SetPasswordReq.** { *; }
-keep class com.aksar.dungru.models.request.SetSettingReq.** { *; }
-keep class com.aksar.dungru.models.request.SignInReq.** { *; }
-keep class com.aksar.dungru.models.request.SignUpReq.** { *; }
-keep class com.aksar.dungru.models.request.UpdateEmailOtpReq.** { *; }
-keep class com.aksar.dungru.models.request.UpdateEmailReq.** { *; }
-keep class com.aksar.dungru.models.request.AddReportReq.** { *; }
-keep class com.aksar.dungru.models.request.FetchProfilePostDataReq.** { *; }
-keep class com.aksar.dungru.models.request.SocialLogInReq.** { *; }


# Response model classes
-keep class com.aksar.dungru.models.response.BlockUserListRes.** { *; }
-keep class com.aksar.dungru.models.response.BlockUserData.** { *; }
-keep class com.aksar.dungru.models.response.CodeAndMsgBaseRes.** { *; }
-keep class com.aksar.dungru.models.response.FetchCommentsRes.** { *; }
-keep class com.aksar.dungru.models.response.CommentData.** { *; }
-keep class com.aksar.dungru.models.response.FetchFeedPostsRes.** { *; }
-keep class com.aksar.dungru.models.response.FeedPosts.** { *; }
-keep class com.aksar.dungru.models.response.FetchProfilePostsRes.** { *; }
-keep class com.aksar.dungru.models.response.FeedData.** { *; }
-keep class com.aksar.dungru.models.response.UserFollowerData.** { *; }
-keep class com.aksar.dungru.models.response.ProfilePostData.** { *; }
-keep class com.aksar.dungru.models.response.FollowerFollowingsListRes.** { *; }
-keep class com.aksar.dungru.models.response.FollowerFollowingData.** { *; }
-keep class com.aksar.dungru.models.response.NotificationRes.** { *; }
-keep class com.aksar.dungru.models.response.OtpRes.** { *; }
-keep class com.aksar.dungru.models.response.ProfileRes.** { *; }
-keep class com.aksar.dungru.models.response.ResetPassRes.** { *; }
-keep class com.aksar.dungru.models.response.SignInRes.** { *; }
-keep class com.aksar.dungru.models.response.LoggedUserData.** { *; }
-keep class com.aksar.dungru.models.response.SettingData.** { *; }
-keep class com.aksar.dungru.models.response.InAppNotifications.** { *; }
-keep class com.aksar.dungru.models.response.SignUpRes.** { *; }
-keep class com.aksar.dungru.models.response.UpdateEmailRes.** { *; }
-keep class com.aksar.dungru.models.response.SocialLoginRes.** { *; }
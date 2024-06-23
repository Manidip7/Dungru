package com.aksar.dungru.network

import com.aksar.dungru.models.request.AddCommentReq
import com.aksar.dungru.models.request.AddOrRemoveFollowerReq
import com.aksar.dungru.models.request.AddReportReq
import com.aksar.dungru.models.request.BaseUserIDAndTokenReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.BlockOrUnblockUserReq
import com.aksar.dungru.models.request.FetchCommentsReq
import com.aksar.dungru.models.request.FetchProfilePostsReq
import com.aksar.dungru.models.request.NotificationReq
import com.aksar.dungru.models.request.OtpReq
import com.aksar.dungru.models.request.PostLikeDislikeReq
import com.aksar.dungru.models.request.ProfileReq
import com.aksar.dungru.models.request.ResetPassReq
import com.aksar.dungru.models.request.SetPasswordReq
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.request.SignInReq
import com.aksar.dungru.models.request.SignUpReq
import com.aksar.dungru.models.request.SocialLogInReq
import com.aksar.dungru.models.request.UpdateEmailOtpReq
import com.aksar.dungru.models.request.UpdateEmailReq
import com.aksar.dungru.models.response.BlockUserListRes
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.FetchCommentsRes
import com.aksar.dungru.models.response.FetchFeedPostsRes
import com.aksar.dungru.models.response.FetchProfilePostsRes
import com.aksar.dungru.models.response.FollowerFollowingsListRes
import com.aksar.dungru.models.response.NotificationRes
import com.aksar.dungru.models.response.OtpRes
import com.aksar.dungru.models.response.ProfileRes
import com.aksar.dungru.models.response.ResetPassRes
import com.aksar.dungru.models.response.SignInRes
import com.aksar.dungru.models.response.SignUpRes
import com.aksar.dungru.models.response.SocialLoginRes
import com.aksar.dungru.models.response.UpdateEmailRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    //All api endpoints here
    /**Authentication*/
    @POST("socialLogin")
    suspend fun socialLogin(@Body socialLogInReq: SocialLogInReq): Response<SocialLoginRes>
    @POST("Signup")
    suspend fun signUp(@Body signUpReq: SignUpReq): Response<SignUpRes>

    @POST("Login")
    suspend fun signIn(@Body signInReq: SignInReq): Response<SignInRes>

    @POST("EmailVerification")
    suspend fun getOtp(@Body otpReq: OtpReq): Response<OtpRes>

    @POST("OtpVerification")
    suspend fun otpVerification(@Body otpReq: OtpReq): Response<OtpRes>

    @POST("ForgotPassword")
    suspend fun setNewPassword(@Body setPasswordReq: SetPasswordReq): Response<CodeAndMsgBaseRes>


    /**User Profile*/
    @POST("Profile")
    suspend fun fetchUserData(@Body baseUserIDAndTokenReq: BaseUserIDAndTokenReq): Response<ProfileRes>

    @POST("Update")
    suspend fun updateProfile(@Body profileReq: ProfileReq): Response<ProfileRes>

    @Multipart
    @POST("imageUpload")
    suspend fun uplodeProfileImage(
        @Part userfile: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
    ): Response<CodeAndMsgBaseRes>

    @POST("fetchProfilePosts")
    suspend fun fetchProfilePosts(@Body fetchProfilePostsReq:FetchProfilePostsReq): Response<FetchProfilePostsRes>


    /**Settings*/
    @POST("ResetPassword")
    suspend fun resetPassword(@Body resetPassReq: ResetPassReq): Response<ResetPassRes>

    @POST("settings")
    suspend fun setSetting(@Body setSettingReq: SetSettingReq): Response<CodeAndMsgBaseRes>

    @POST("editEmail")
    suspend fun updateEmail(@Body updateEmailReq: UpdateEmailReq): Response<UpdateEmailRes>

    @POST("verifyOtpEmail")
    suspend fun updateEmailOtpValidation(@Body updateEmailOtpReq: UpdateEmailOtpReq): Response<CodeAndMsgBaseRes>

    @POST("userNotifications")
    suspend fun notificationUpdate(@Body notificationReq: NotificationReq): Response<NotificationRes>


    /**Feed Posts*/
    @Multipart
    @POST("uploadPost")
    suspend fun uploadPost(
        @Part("user_id") userId: RequestBody,
        @Part userfile: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): Response<CodeAndMsgBaseRes>

    @POST("getAllPosts")
    suspend fun fetchFeedPosts(@Body baseUserIDReq: BaseUserIDReq): Response<FetchFeedPostsRes>
    @POST("addLikeInPost")
    suspend fun postLikeDislike(@Body postLikeDislikeReq: PostLikeDislikeReq): Response<CodeAndMsgBaseRes>
    @POST("addComments")
    suspend fun addComment(@Body addCommentReq: AddCommentReq): Response<CodeAndMsgBaseRes>
    @POST("getComments")
    suspend fun fetchComments(@Body fetchCommentsReq: FetchCommentsReq): Response<FetchCommentsRes>
    @POST("addReport")
    suspend fun addReport(@Body addReportReq: AddReportReq): Response<CodeAndMsgBaseRes>


    /**Follower Following List, Add or remove follower And Block or unblock user*/
    @POST("followersList")
    suspend fun fetchFollowerList(@Body baseUserIDReq: BaseUserIDReq): Response<FollowerFollowingsListRes>

    @POST("followingsList")
    suspend fun fetchFollowingList(@Body baseUserIDReq: BaseUserIDReq): Response<FollowerFollowingsListRes>

    @POST("addFollower")
    suspend fun addOrRemoveFollower(@Body addOrRemoveFollowerReq: AddOrRemoveFollowerReq): Response<CodeAndMsgBaseRes>

    @POST("blockUser")
    suspend fun blockOrUnblockUser(@Body blockOrUnblockUserReq: BlockOrUnblockUserReq): Response<CodeAndMsgBaseRes>
    @POST("blockUsersList")
    suspend fun fetchBlockUserList(@Body baseUserIDReq: BaseUserIDReq): Response<BlockUserListRes>

}
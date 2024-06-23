package com.aksar.dungru.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.aksar.dungru.models.request.SetPasswordReq
import com.aksar.dungru.models.request.SignInReq
import com.aksar.dungru.models.request.SignUpReq
import com.aksar.dungru.models.request.ProfileReq
import com.aksar.dungru.models.request.ResetPassReq
import com.aksar.dungru.models.request.SetSettingReq
import com.aksar.dungru.models.request.SocialLogInReq
import com.aksar.dungru.models.request.UpdateEmailOtpReq
import com.aksar.dungru.models.request.UpdateEmailReq
import com.aksar.dungru.models.response.BlockUserListRes
import com.aksar.dungru.models.response.OtpRes
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.FetchCommentsRes
import com.aksar.dungru.models.response.FetchFeedPostsRes
import com.aksar.dungru.models.response.NotificationRes
import com.aksar.dungru.models.response.SignInRes
import com.aksar.dungru.models.response.SignUpRes
import com.aksar.dungru.models.response.ProfileRes
import com.aksar.dungru.models.response.ResetPassRes
import com.aksar.dungru.models.response.UpdateEmailRes
import com.aksar.dungru.models.response.FetchProfilePostsRes
import com.aksar.dungru.models.response.FollowerFollowingsListRes
import com.aksar.dungru.models.response.SocialLoginRes
import com.aksar.dungru.network.ApiService
import com.aksar.dungru.network.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class Repository(private val apiService: ApiService) {
    /**Authentication*/
    suspend fun socialLogin(socialLogInReq: SocialLogInReq): Response<SocialLoginRes>{
        return withContext(Dispatchers.IO){
            apiService.socialLogin(socialLogInReq)
        }
    }
    suspend fun signUp(signUpReq: SignUpReq): Response<SignUpRes> {
        return withContext(Dispatchers.IO){
            apiService.signUp(signUpReq)
        }
    }
    suspend fun signIn(signInReq: SignInReq): Response<SignInRes>{
        return withContext(Dispatchers.IO){
            apiService.signIn(signInReq)
        }
    }
    suspend fun getOtp(otpReq: OtpReq): Response<OtpRes>{
        return withContext(Dispatchers.IO){
            apiService.getOtp(otpReq)
        }
    }
    suspend fun otpVerification(otpReq: OtpReq): Response<OtpRes>{
        return withContext(Dispatchers.IO){
            apiService.otpVerification(otpReq)
        }
    }
    suspend fun setNewPassword(setPasswordReq: SetPasswordReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.setNewPassword(setPasswordReq)
        }
    }


    /**User Profile*/
    suspend fun fetchUserData(baseUserIDAndTokenReq: BaseUserIDAndTokenReq): Response<ProfileRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchUserData(baseUserIDAndTokenReq)
        }
    }
    suspend fun updateProfile(profileReq: ProfileReq): Response<ProfileRes>{
        return withContext(Dispatchers.IO){
            apiService.updateProfile(profileReq)
        }
    }
    suspend fun uploadProfileImage(file: MultipartBody.Part, userId: RequestBody): Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.uplodeProfileImage(file,userId)
        }
    }
    suspend fun fetchProfilePosts(fetchProfilePostsReq: FetchProfilePostsReq): Response<FetchProfilePostsRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchProfilePosts(fetchProfilePostsReq)
        }
    }


    /**Settings*/
    suspend fun setSetting(setSettingReq: SetSettingReq): Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.setSetting(setSettingReq)
        }
    }
    suspend fun resetPassword(resetPassReq: ResetPassReq):Response<ResetPassRes>{
        return withContext(Dispatchers.IO){
            apiService.resetPassword(resetPassReq)
        }
    }

    suspend fun updateEmail(updateEmailReq: UpdateEmailReq) : Response<UpdateEmailRes>{
        return withContext(Dispatchers.IO){
            apiService.updateEmail(updateEmailReq)
        }
    }
    suspend fun updateEmailOtpValidation(updateEmailOtpReq: UpdateEmailOtpReq) : Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.updateEmailOtpValidation(updateEmailOtpReq)
        }
    }
    suspend fun notificationSetting(notificationReq: NotificationReq) : Response<NotificationRes>{
        return withContext(Dispatchers.IO){
            apiService.notificationUpdate(notificationReq)
        }
    }

    /**Feed Posts*/
    suspend fun uploadPost(userId: RequestBody,file: MultipartBody.Part,caption:RequestBody):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.uploadPost(userId,file,caption)
        }
    }

    private val FetchFeedPostsLiveData = MutableLiveData<NetworkResponse<FetchFeedPostsRes>>()
    val _fetchFeedPosts : LiveData<NetworkResponse<FetchFeedPostsRes>>
        get() = FetchFeedPostsLiveData

    suspend fun getFeedData(baseUserIDReq: BaseUserIDReq){
        FetchFeedPostsLiveData.postValue(NetworkResponse.Loading())
        val result = apiService.fetchFeedPosts(baseUserIDReq)

        if (result.isSuccessful && result.body() != null){
            FetchFeedPostsLiveData.postValue(NetworkResponse.Success(result.body()))
        }else if (result.errorBody() != null){
            FetchFeedPostsLiveData.postValue(NetworkResponse.Error("Somthing went wrong"))
        }else{
            FetchFeedPostsLiveData.postValue(NetworkResponse.Error("Somthing went wrong"))
        }
    }

//    suspend fun fetchFeedPosts(baseUserIDReq: BaseUserIDReq):Response<FetchFeedPostsRes>{
//        return withContext(Dispatchers.IO){
//            apiService.fetchFeedPosts(baseUserIDReq)
//        }
//    }
    suspend fun postLikeDislike(postLikeDislikeReq: PostLikeDislikeReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.postLikeDislike(postLikeDislikeReq)
        }
    }
    suspend fun addComment(addCommentReq: AddCommentReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.addComment(addCommentReq)
        }
    }
    suspend fun fetchComments(fetchCommentsReq: FetchCommentsReq):Response<FetchCommentsRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchComments(fetchCommentsReq)
        }
    }
    suspend fun addReport(addReportReq: AddReportReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.addReport(addReportReq)
        }
    }

    /**Follower Following List, Add or remove follower And Block or unblock user*/
    suspend fun fetchFollowerList(baseUserIDReq: BaseUserIDReq):Response<FollowerFollowingsListRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchFollowerList(baseUserIDReq)
        }
    }
    suspend fun fetchFollowingList(baseUserIDReq: BaseUserIDReq):Response<FollowerFollowingsListRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchFollowingList(baseUserIDReq)
        }
    }
    suspend fun addOrRemoveFollower(addOrRemoveFollowerReq: AddOrRemoveFollowerReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.addOrRemoveFollower(addOrRemoveFollowerReq)
        }
    }
    suspend fun blockOrUnblockUser(blockOrUnblockUserReq: BlockOrUnblockUserReq):Response<CodeAndMsgBaseRes>{
        return withContext(Dispatchers.IO){
            apiService.blockOrUnblockUser(blockOrUnblockUserReq)
        }
    }
    suspend fun fetchBlockUserList(baseUserIDReq: BaseUserIDReq):Response<BlockUserListRes>{
        return withContext(Dispatchers.IO){
            apiService.fetchBlockUserList(baseUserIDReq)
        }
    }
}
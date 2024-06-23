package com.aksar.dungru.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aksar.dungru.models.request.AddCommentReq
import com.aksar.dungru.models.request.AddReportReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.FetchCommentsReq
import com.aksar.dungru.models.request.PostLikeDislikeReq
import com.aksar.dungru.models.response.CodeAndMsgBaseRes
import com.aksar.dungru.models.response.FetchCommentsRes
import com.aksar.dungru.models.response.FetchFeedPostsRes
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FeedViewModel (private val repository: Repository): ViewModel() {
    private val upload_post: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val uploadPostResponse get() = upload_post

//    private val fetch_post: MutableLiveData<NetworkResponse<FetchFeedPostsRes>> = MutableLiveData()
//    val fetchFeedPostsResponse get() = fetch_post

    private val post_like_dislike: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val postLikeDislikeResponse get() = post_like_dislike

    private val add_comment: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val addCommentResponse get() = add_comment

    private val fetch_comments: MutableLiveData<NetworkResponse<FetchCommentsRes>> = MutableLiveData()
    val fetchCommentResponse get() = fetch_comments

    private val add_report: MutableLiveData<NetworkResponse<CodeAndMsgBaseRes>> = MutableLiveData()
    val addReportResponse get() = add_report
    fun uploadPost(userId: RequestBody,file: MultipartBody.Part,caption:RequestBody ) = viewModelScope.launch {
        upload_post.value = NetworkResponse.Loading()
        try {
            val responseData = repository.uploadPost(userId,file,caption)
            if (responseData.isSuccessful)
                upload_post.value = NetworkResponse.Success(responseData.body())
            else
                upload_post.value = NetworkResponse.Error("Server error!")

        } catch (e: Exception) {
            upload_post.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Upload posts error: $e")
        }
    }


    fun fetchFeedPosts(baseUserIDReq: BaseUserIDReq) = viewModelScope.launch (Dispatchers.IO){
        repository.getFeedData(baseUserIDReq)
    }
    val _FeedData: LiveData<NetworkResponse<FetchFeedPostsRes>>
        get() = repository._fetchFeedPosts



//    fun fetchFeedPosts(baseUserIDReq: BaseUserIDReq) = viewModelScope.launch {
//        fetch_post.value = NetworkResponse.Loading()
//        try {
//            val responseData = repository.fetchFeedPosts(baseUserIDReq)
//            if(responseData.isSuccessful)
//                fetch_post.value = NetworkResponse.Success(responseData.body())
//            else
//                fetch_post.value = NetworkResponse.Error("Server error!")
//        }catch (e: Exception) {
//            upload_post.value = NetworkResponse.Error("Something went wrong")
//            Log.d("RES", "Fetch feed posts error: $e")
//        }
//    }

    fun postLikeDislike(postLikeDislikeReq: PostLikeDislikeReq) = viewModelScope.launch {
        post_like_dislike.value = NetworkResponse.Loading()
        try {
            val responseData = repository.postLikeDislike(postLikeDislikeReq)
            if(responseData.isSuccessful)
                post_like_dislike.value = NetworkResponse.Success(responseData.body())
            else
                post_like_dislike.value = NetworkResponse.Error("Server error!")

        }catch (e:Exception){
            post_like_dislike.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "post like and dislike error: $e")
        }
    }
    fun addComment(addCommentReq: AddCommentReq) = viewModelScope.launch {
        add_comment.value = NetworkResponse.Loading()
        try {
            val responseData = repository.addComment(addCommentReq)
            if(responseData.isSuccessful) {
                fetchComments(FetchCommentsReq(addCommentReq.post_id))
            }
            else
                add_comment.value = NetworkResponse.Error("Server error!")

        }catch (e:Exception){
            add_comment.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Add comment error: $e")
        }
    }

    fun fetchComments(fetchCommentReq: FetchCommentsReq) = viewModelScope.launch {
        fetch_comments.value = NetworkResponse.Loading()
        try {
            val responseData = repository.fetchComments(fetchCommentReq)
            if(responseData.isSuccessful)
                fetch_comments.value = NetworkResponse.Success(responseData.body())
            else
                fetch_comments.value = NetworkResponse.Error("Server error!")

        }catch (e:Exception){
            fetch_comments.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Fetch comments error: $e")
        }
    }


    fun addReport(addReportReq: AddReportReq) = viewModelScope.launch {
        add_report.value = NetworkResponse.Loading()
        try {
            val responseData = repository.addReport(addReportReq)
            if(responseData.isSuccessful) {
                add_report.value = NetworkResponse.Success(responseData.body())
            }
            else
                add_report.value = NetworkResponse.Error("Server error!")

        }catch (e:Exception){
            add_report.value = NetworkResponse.Error("Something went wrong")
            Log.d("RES", "Add report error: $e")
        }
    }
}
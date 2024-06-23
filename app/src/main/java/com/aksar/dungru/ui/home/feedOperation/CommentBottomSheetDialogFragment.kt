package com.aksar.dungru.ui.home.feedOperation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.CommentsAdapter
import com.aksar.dungru.databinding.FragmentCommentBottomSheetDialogBinding
import com.aksar.dungru.models.request.AddCommentReq
import com.aksar.dungru.models.request.FetchCommentsReq
import com.aksar.dungru.models.response.CommentData
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.ui.profile.ProfileActivity
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.utils.Constance
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FeedViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class CommentBottomSheetDialogFragment(private val postId: String) : BottomSheetDialogFragment(),
    CommentsAdapter.CommentClickListener {
    private lateinit var binding: FragmentCommentBottomSheetDialogBinding
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var feedViewModel: FeedViewModel
    private var commentList = ArrayList<CommentData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCommentBottomSheetDialogBinding.inflate(layoutInflater, container, false)
        feedViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FeedViewModel::class.java]

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            isCancelable = false
        }

        themeSetting()
        initObserver()

        commentsAdapter = CommentsAdapter(requireContext(), commentList, this)
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.commentRecyclerView.adapter = commentsAdapter
        feedViewModel.fetchComments(FetchCommentsReq(postId))

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSend.setOnClickListener {
            if (binding.etComment.text.toString().isNullOrBlank()) {
                Toast.makeText(requireContext(), "Write something....", Toast.LENGTH_SHORT).show()
            } else {
                val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
                val commentData = binding.etComment.text.toString().trim()
                // convert Base64 encode comment Data
                val commentBase64 = Base64.encodeToString(commentData.toByteArray(Charsets.UTF_8), Base64.DEFAULT)

                feedViewModel.addComment(
                    AddCommentReq(
                        commentBase64,
                        postId,
                        data?.user_id.toString()
                    )
                )
            }
        }

        return binding.root
    }

    private fun themeSetting() {
        if (Utils(requireContext()).isDarkTheme())
            binding.root.setBackgroundResource(R.drawable.bg_black_bottom_sheet_rounded_16dp)
        else
            binding.root.setBackgroundResource(R.drawable.bg_white_bottom_sheet_rounded_16dp)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        feedViewModel.fetchCommentResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.shimmerView.visibility = View.VISIBLE
                    binding.shimmerView.startShimmer()
                }

                is NetworkResponse.Success -> {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    if (response.data?.data != null && response.data.data.isNotEmpty()) {
                        binding.noCommentView.visibility = View.GONE
                        commentList.clear()
                        commentList.addAll(response.data.data)
                        commentsAdapter.notifyDataSetChanged()

                        //send button handling
                        binding.btnSend.isClickable = true
                        binding.btnSend.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        binding.etComment.text = null
                    } else binding.noCommentView.visibility = View.VISIBLE
                }

                is NetworkResponse.Error -> {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
        feedViewModel.addCommentResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.btnSend.isClickable = false
                    binding.btnSend.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {}
                is NetworkResponse.Error -> {
                    binding.btnSend.isClickable = true
                    binding.btnSend.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }

    override fun userAccountClicked(id: String) {
        val data = PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        if (id == data?.user_id) {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        } else {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra(Constance.DataPass, id)
            startActivity(intent)
        }
    }
}

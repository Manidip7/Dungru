package com.aksar.dungru.ui.follower.followerfragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.FollowingProfileAdapter
import com.aksar.dungru.databinding.FragmentFollowingListBinding
import com.aksar.dungru.databinding.UnfollowConfromDialogBinding
import com.aksar.dungru.models.request.AddOrRemoveFollowerReq
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.response.FollowerFollowingData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance.USER_ID
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FollowerFollowingsViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView

class FollowingListFragment : Fragment() {
    private lateinit var binding: FragmentFollowingListBinding
    private lateinit var viewModel: FollowerFollowingsViewModel
    private var removedItemLoader: ProgressBar? = null
    private var removedItemPosition: Int? = null
    private var removedItemBtnView: MaterialTextView? = null
    private var followingDataList = ArrayList<FollowerFollowingData>()
    private lateinit var followingProfileAdapter : FollowingProfileAdapter
    private lateinit var unfollowWarningDialog : BottomSheetDialog
    private var userId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowingListBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[FollowerFollowingsViewModel::class.java]
        initObserver()
        viewModel.fetchFollowingList(BaseUserIDReq(userId.toString()))

        unfollowWarningDialog = BottomSheetDialog(requireContext())

        val layoutManager = LinearLayoutManager(requireContext())
        binding.followingListRecycler.layoutManager = layoutManager
        followingProfileAdapter = FollowingProfileAdapter(
            requireContext(),
            followingDataList
        ) { position, item, loader, btnView ->
            removedItemPosition = position
            removedItemLoader = loader
            removedItemBtnView = btnView
            showUnfollowWarningDialog(item)
        }
        binding.followingListRecycler.adapter = followingProfileAdapter

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        viewModel.fetchFollowingResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.data != null && response.data.data.isNotEmpty()) {
                        binding.noDataView.visibility = View.GONE
                        followingDataList.clear()
                        followingDataList.addAll(response.data.data)
                        followingProfileAdapter.notifyDataSetChanged()
                    } else {
                        binding.noDataView.visibility = View.VISIBLE
                    }
                }

                is NetworkResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }

        viewModel.addRemoveFollowerResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    removedItemBtnView?.visibility = View.INVISIBLE
                    removedItemLoader?.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    unfollowWarningDialog.dismiss()
                    removedItemPosition?.let { followingDataList.removeAt(it) }
                    removedItemPosition?.let { followingProfileAdapter.notifyItemRemoved(it) }
                    followingProfileAdapter.notifyDataSetChanged()
                    binding.noDataView.visibility = if (followingDataList.isEmpty()) View.VISIBLE else View.GONE
                    removedItemBtnView?.visibility = View.VISIBLE
                    removedItemLoader?.visibility = View.GONE
                    showToast(response.data?.message.toString(), false)
                }

                is NetworkResponse.Error -> {
                    removedItemBtnView?.visibility = View.VISIBLE
                    removedItemLoader?.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }
    private fun showUnfollowWarningDialog(item: FollowerFollowingData) {
        val dialogBinding = UnfollowConfromDialogBinding.inflate(layoutInflater)
        unfollowWarningDialog.setContentView(dialogBinding.root)
        val formattedText = getString(R.string.unfollow_warning, "<font color=\"#EA0054\">@${item.username}</font>")
        val spannedText = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        dialogBinding.txtWarning.text = spannedText

        dialogBinding.btnUnfollowConfirm.setOnClickListener {
            viewModel.addOrRemoveFollower(AddOrRemoveFollowerReq(userId.toString(), item.user_id))
        }
        dialogBinding.btnUnfollowCancle.setOnClickListener {
            unfollowWarningDialog.cancel()
        }

        unfollowWarningDialog.setOnCancelListener {
            removedItemBtnView?.visibility = View.VISIBLE
            removedItemLoader?.visibility = View.GONE
            unfollowWarningDialog.dismiss()
        }

        unfollowWarningDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String) =
            FollowingListFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }

}
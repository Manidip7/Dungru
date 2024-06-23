package com.aksar.dungru.ui.setting.settingfragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.R
import com.aksar.dungru.adapters.BlockUserAdapter
import com.aksar.dungru.databinding.FragmentBlockUserBinding
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.models.request.BlockOrUnblockUserReq
import com.aksar.dungru.models.response.BlockUserData
import com.aksar.dungru.models.response.LoggedUserData
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.PrefManager
import com.aksar.dungru.utils.extensions.hideProgressDialog
import com.aksar.dungru.utils.extensions.showProgressDialog
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.SettingViewModel
import com.aksar.dungru.viewModel.ViewModelFactory
import com.google.android.material.textview.MaterialTextView

class BlockUserFragment : Fragment() {
    private lateinit var binding: FragmentBlockUserBinding
    private lateinit var settingViewModel: SettingViewModel
    private var blockDataList = ArrayList<BlockUserData>()
    private lateinit var blockUserAdapter: BlockUserAdapter
    private var data: LoggedUserData? = null
    private var removeItemPosition: Int? = null
    private var removeItemLoader: ProgressBar? = null
    private var removeItemBtnView: MaterialTextView? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBlockUserBinding.inflate(layoutInflater)
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(Repository(RetrofitClient.apiService))
        )[SettingViewModel::class.java]
        initObserver()
        data =
            data ?: PrefManager.get().getObject(PrefManager.USER_DATA, LoggedUserData::class.java)
        settingViewModel.fetchBlockUserList(BaseUserIDReq(data?.user_id.toString()))

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recBlockUser.layoutManager = layoutManager

        blockUserAdapter = BlockUserAdapter(requireContext(), blockDataList) { position, item, loader, btnView ->
            settingViewModel.blockOrUnblockUser(
                BlockOrUnblockUserReq(
                    data?.user_id.toString(),
                    item.user_id
                )
            )
            removeItemPosition = position
            removeItemLoader = loader
            removeItemBtnView = btnView
        }
        binding.recBlockUser.adapter = blockUserAdapter

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initObserver() {
        settingViewModel.fetchBlockUserListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if (response.data?.data != null && response.data.data.isNotEmpty()) {
                        blockDataList.clear()
                        blockDataList.addAll(response.data.data)
                        blockUserAdapter.notifyDataSetChanged()
                    } else binding.noDataView.visibility = View.VISIBLE
                }
                is NetworkResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }

        settingViewModel.blockUnblockUserResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {}
                is NetworkResponse.Success -> {
                    removeItemPosition?.let { blockDataList.removeAt(it) }
                    removeItemPosition?.let { blockUserAdapter.notifyItemRemoved(it) }
                    blockUserAdapter.notifyDataSetChanged()
                    binding.noDataView.visibility = if (blockDataList.isEmpty()) View.VISIBLE else View.GONE
                    removeItemBtnView?.visibility = View.VISIBLE
                    removeItemLoader?.visibility = View.GONE
                    showToast("User unblocked", false)
                }

                is NetworkResponse.Error -> {
                    removeItemBtnView?.visibility = View.VISIBLE
                    removeItemLoader?.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }


}
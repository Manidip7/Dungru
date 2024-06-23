package com.aksar.dungru.ui.follower.followerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aksar.dungru.adapters.FollowerProfileAdapter
import com.aksar.dungru.databinding.FragmentFollowerListBinding
import com.aksar.dungru.models.request.BaseUserIDReq
import com.aksar.dungru.network.NetworkResponse
import com.aksar.dungru.network.RetrofitClient
import com.aksar.dungru.repository.Repository
import com.aksar.dungru.utils.Constance.Flow
import com.aksar.dungru.utils.Constance.USER_ID
import com.aksar.dungru.utils.extensions.showToast
import com.aksar.dungru.viewModel.FollowerFollowingsViewModel
import com.aksar.dungru.viewModel.ViewModelFactory

class FollowerListFragment : Fragment() {
    private lateinit var binding: FragmentFollowerListBinding
    private lateinit var viewModel: FollowerFollowingsViewModel
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
        binding = FragmentFollowerListBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(RetrofitClient.apiService)))[FollowerFollowingsViewModel::class.java]
        initObserver()
        viewModel.fetchFollowerList(BaseUserIDReq(userId.toString()))

        return binding.root
    }
    private fun initObserver() {
        viewModel.fetchFollowerResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResponse.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResponse.Success -> {
                    binding.progressBar.visibility = View.GONE

                    if (response.data?.data != null && response.data.data.isNotEmpty()) {
                        binding.noDataView.visibility = View.GONE
                        val layoutManager = LinearLayoutManager(requireContext())
                        binding.followerListRecycler.layoutManager = layoutManager
                        val adapter = FollowerProfileAdapter(requireContext(), response.data.data) { item ->

                        }
                        binding.followerListRecycler.adapter = adapter
                    } else binding.noDataView.visibility = View.VISIBLE
                }
                is NetworkResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(response.message.toString(), false)
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(userId: String) =
            FollowerListFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }

}
package com.aksar.dungru.ui.home.homefragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.adapters.LiveStreamingAdapter
import com.aksar.dungru.databinding.FragmentLiveBinding
import com.aksar.dungru.models.LiveStreamContentModel
import com.aksar.dungru.ui.home.liveShow.LiveShowActivity
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.utils.Constance.DataPass

class LiveFragment : Fragment(), LiveStreamingAdapter.LiveClickListener {
    private lateinit var binding: FragmentLiveBinding
    private lateinit var liveStreamingAdapter: LiveStreamingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLiveBinding.inflate(layoutInflater, container, false)
        val liveContentRecycler = binding.liveStreamingRecycler

        val itemList = arrayListOf(
            LiveStreamContentModel("1",R.drawable.new1, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("2",R.drawable.new2, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("3",R.drawable.new3, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("4",R.drawable.new4, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("5",R.drawable.new2, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("6",R.drawable.new5, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("7",R.drawable.new3, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("8",R.drawable.new1, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("9",R.drawable.new4, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("10",R.drawable.new5, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("11",R.drawable.new1, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("12",R.drawable.new2, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("13",R.drawable.new3, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("14",R.drawable.new4, "Mugdho", "12K", R.drawable.demo_profile_img),
            LiveStreamContentModel("15",R.drawable.demo_content_image, "Mugdho", "12K", R.drawable.demo_profile_img),
        )

        liveStreamingAdapter = LiveStreamingAdapter(itemList, this)
        liveContentRecycler.adapter = liveStreamingAdapter

        binding.swipeToRefreshLayout.setOnRefreshListener {
            liveStreamingAdapter.notifyDataSetChanged()
            binding.swipeToRefreshLayout.isRefreshing = false
        }
        return binding.root
    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(requireContext(),LiveShowActivity::class.java))
    }

    override fun onAccountClick(id: String) {
        val intent = Intent(requireContext(), UserProfileActivity::class.java)
        intent.putExtra(DataPass, id)
        startActivity(intent)
    }
}
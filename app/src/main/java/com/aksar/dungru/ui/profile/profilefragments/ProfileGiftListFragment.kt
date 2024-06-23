package com.aksar.dungru.ui.profile.profilefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.adapters.ProfileGiftListAdapter
import com.aksar.dungru.databinding.FragmentProfileGiftListBinding
import com.aksar.dungru.models.ProfileGiftDataModel

class ProfileGiftListFragment : Fragment() {
    private lateinit var binding: FragmentProfileGiftListBinding
    private lateinit var adapter: ProfileGiftListAdapter
    private lateinit var giftItemList: ArrayList<ProfileGiftDataModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileGiftListBinding.inflate(layoutInflater, container, false)
        giftItemList = arrayListOf(
            ProfileGiftDataModel(R.drawable.demo_gift_heart, 5),
            ProfileGiftDataModel(R.drawable.demo_gift_flower, 6),
            ProfileGiftDataModel(R.drawable.demo_gift_heart, 5),
            ProfileGiftDataModel(R.drawable.demo_gift_star, 2),
            ProfileGiftDataModel(R.drawable.demo_gift_deer, 12),
        )

        adapter = ProfileGiftListAdapter(giftItemList){

        }

        val profileGiftListRecycler = binding.profileGiftListRecycler
        profileGiftListRecycler.adapter = adapter
        return binding.root
    }
}
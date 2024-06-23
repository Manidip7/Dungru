package com.aksar.dungru.ui.search.searchfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.adapters.SearchViewPagerAdapter
import com.aksar.dungru.databinding.FragmentSearchFilterBinding

class SearchFilterFragment : Fragment() {

    private lateinit var binding:FragmentSearchFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchFilterBinding.inflate(layoutInflater,container,false)

        //View pager set
        searchViewPagerSet()

        return binding.root
    }

    private fun searchViewPagerSet() {
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        val adapter = SearchViewPagerAdapter(childFragmentManager)
        adapter.addFragment(SearchAccountFragment(), "Accounts")
        adapter.addFragment(SearchLiveFragment(), "Live")
        adapter.addFragment(SearchFeedsFragment(), "Feeds")

        binding.viewPager.adapter = adapter
    }

}
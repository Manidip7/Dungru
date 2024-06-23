package com.aksar.dungru.ui.search.searchfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.adapters.SearchListAdapter
import com.aksar.dungru.databinding.FragmentSearchResultBinding
import com.aksar.dungru.models.SearchListDataModel


class SearchResultFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultBinding
    private lateinit var adapter: SearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchResultBinding.inflate(layoutInflater, container, false)
        val searchItemList = arrayListOf<SearchListDataModel>(
            SearchListDataModel(R.drawable.demo_profile_img, "Muggdho Chakraborrty"),
            SearchListDataModel(R.drawable.demo_profile_img, "Joyjit Bhandari"),
            SearchListDataModel(R.drawable.demo_profile_img, "Tapabrato Goswami"),
            SearchListDataModel(R.drawable.demo_profile_img, "Saif Ali")
        )

        val searchResultRecyler = binding.searchResultRecycler
        adapter = SearchListAdapter(searchItemList) {

        }
        searchResultRecyler.adapter = adapter
        return binding.root
    }

}
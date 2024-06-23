package com.aksar.dungru.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.adapters.LiveStreamingAdapter
import com.aksar.dungru.databinding.ActivitySearchBinding
import com.aksar.dungru.models.LiveStreamContentModel
import com.aksar.dungru.ui.home.liveShow.LiveShowActivity
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.utils.extensions.hideKeyboard
import com.aksar.dungru.utils.extensions.replaceFragment
import com.aksar.dungru.ui.notifications.NotificationActivity
import com.aksar.dungru.ui.profile.UserProfileActivity
import com.aksar.dungru.ui.search.searchfragments.SearchFilterFragment
import com.aksar.dungru.ui.search.searchfragments.SearchResultFragment
import com.aksar.dungru.utils.Constance.DataPass
import com.aksar.dungru.utils.Constance.SearchFilter
import com.aksar.dungru.utils.Constance.SearchResults
import com.aksar.dungru.utils.NetworkUtils

class SearchActivity : AppCompatActivity(), OnClickListener, LiveStreamingAdapter.LiveClickListener {

    private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        binding.btnBack.setOnClickListener(this)

        val liveStreamingRecyclerSearch = binding.liveStreamingRecyclerSearch
        val itemList = arrayListOf(
            LiveStreamContentModel(
                "1",
                R.drawable.demo_content_image,
                "Mugdho",
                "12K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "2",
                R.drawable.demo_content_image,
                "Joyjit",
                "12.5K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "3",
                R.drawable.demo_content_image,
                "Barnali",
                "12K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "4",
                R.drawable.demo_content_image,
                "Saif",
                "12K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "5",
                R.drawable.demo_content_image,
                "Tapabrata",
                "12K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "6",
                R.drawable.demo_content_image,
                "Mugdho",
                "12K",
                R.drawable.demo_profile_img
            ),
            LiveStreamContentModel(
                "7",
                R.drawable.demo_content_image,
                "Mugdho",
                "12K",
                R.drawable.demo_profile_img
            ),
        )
        liveStreamingRecyclerSearch.adapter = LiveStreamingAdapter(itemList, this)
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                if (searchText.isNullOrEmpty()) {
                    binding.categoryLayout.visibility = VISIBLE
                    binding.swipeSearch.visibility = VISIBLE
                    binding.searchFrame.visibility = GONE
                } else {
                    performSearch("TextChanged")
                }
            }
        })

        binding.searchBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = binding.searchBar.text.toString()
                    if (searchText.isNotEmpty()) {
                        binding.searchBar.hideKeyboard()
                        performSearch("Search")
                    }
                    return true
                }
                return false
            }
        })

        binding.searchBar

        val chipGroup = binding.selectOneChipGroup
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.btn_popular -> {

                }

                R.id.btn_nearby -> {


                }

                R.id.btn_new -> {


                }

                R.id.btn_folowing -> {

                }
            }
        }
    }

    private fun performSearch(tag: String) {
        binding.categoryLayout.visibility = GONE
        binding.swipeSearch.visibility = GONE
        binding.searchFrame.visibility = VISIBLE
        if (tag == "TextChanged") {
            addFragment(binding.searchFrame.id, SearchResultFragment(), false, SearchResults)
        } else {
            replaceFragment(binding.searchFrame.id, SearchFilterFragment(), false, SearchFilter)
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> {
                onBackPressed()
            }
            binding.btnNotification ->{
                startActivity(Intent(this, NotificationActivity::class.java))
            }
        }
    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(this, LiveShowActivity::class.java))
    }

    override fun onAccountClick(id: String) {
        val intent = Intent(this, UserProfileActivity::class.java)
        intent.putExtra(DataPass, id)
        startActivity(intent)
    }
}
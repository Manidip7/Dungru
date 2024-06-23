package com.aksar.dungru.ui.follower

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aksar.dungru.adapters.ViewPagerAdapter
import com.aksar.dungru.databinding.ActivityFollowerBinding
import com.aksar.dungru.ui.follower.followerfragments.FollowerListFragment
import com.aksar.dungru.ui.follower.followerfragments.FollowingListFragment
import com.aksar.dungru.utils.Constance.Flow
import com.aksar.dungru.utils.Constance.Follower
import com.aksar.dungru.utils.Constance.Following
import com.aksar.dungru.utils.Constance.USER_ID
import com.aksar.dungru.utils.NetworkUtils

class FollowerFollowingListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFollowerBinding
    private lateinit var flow : String
    private lateinit var userId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        flow = intent.getStringExtra(Flow).toString()
        userId = intent.getStringExtra(USER_ID).toString()

        NetworkUtils.checkConnection(this)

        binding.tabLayout.setupWithViewPager(binding.viewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(FollowerListFragment.newInstance(userId),"Follower")
        adapter.addFragment(FollowingListFragment.newInstance(userId),"Following")
        binding.viewPager.adapter = adapter

        when(flow){
            Follower -> binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))

            Following -> binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }
}
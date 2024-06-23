package com.aksar.dungru.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val fragmentList = ArrayList<Fragment>()
    private val iconList = ArrayList<Int>()
    private val titleList = ArrayList<String>()

    override fun getCount() = fragmentList.size
    override fun getItem(position: Int) = fragmentList[position]
    override fun getPageTitle(position: Int): CharSequence? {
        return if (titleList.isEmpty()) super.getPageTitle(position) else titleList[position]
    }
    fun getIconResource(position: Int) = iconList[position]
    fun addFragment(fragment: Fragment, icon: Int) {
        fragmentList.add(fragment)
        iconList.add(icon)
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }
}
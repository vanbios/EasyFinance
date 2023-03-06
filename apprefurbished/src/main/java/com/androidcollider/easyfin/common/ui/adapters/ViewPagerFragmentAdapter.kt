package com.androidcollider.easyfin.common.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @author Ihor Bilous
 */
class ViewPagerFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragments: MutableList<Fragment>
    private val mFragmentTitles: MutableList<String>

    init {
        mFragments = ArrayList()
        mFragmentTitles = ArrayList()
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragments.add(fragment)
        mFragmentTitles.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitles[position]
    }
}
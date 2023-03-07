package com.androidcollider.easyfin.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidcollider.easyfin.accounts.list.AccountsFragment
import com.androidcollider.easyfin.home.HomeFragment
import com.androidcollider.easyfin.transactions.list.TransactionsFragment

class MainViewPager2Adapter(parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {

    val fragments = arrayOf(HomeFragment(), TransactionsFragment(), AccountsFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
package com.androidcollider.easyfin.transaction_categories.root

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidcollider.easyfin.transaction_categories.nested.TransactionCategoriesNestedFragment

class TransactionCategoriesViewPager2Adapter(parentFragment: Fragment) :
    FragmentStateAdapter(parentFragment) {

    val fragments = arrayOf(getNestedFragment(false), getNestedFragment(true))

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    private fun getNestedFragment(isExpense: Boolean): TransactionCategoriesNestedFragment {
        val fragment = TransactionCategoriesNestedFragment()
        val args = Bundle()
        args.putInt(
            TransactionCategoriesNestedFragment.TYPE,
            if (isExpense) TransactionCategoriesNestedFragment.TYPE_EXPENSE
            else TransactionCategoriesNestedFragment.TYPE_INCOME
        )
        fragment.arguments = args
        return fragment
    }
}
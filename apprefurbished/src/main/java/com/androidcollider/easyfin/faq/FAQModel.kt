package com.androidcollider.easyfin.faq

import android.content.Context
import android.util.Pair
import com.androidcollider.easyfin.R

/**
 * @author Ihor Bilous
 */
internal class FAQModel(private val context: Context) : FAQMVP.Model {
    override val info: List<Pair<String, String>>
        get() {
            val list: MutableList<Pair<String, String>> = ArrayList()
            list.add(
                Pair(
                    context.getString(R.string.general_info),
                    context.getString(R.string.faq_about_app)
                )
            )
            list.add(
                Pair(
                    context.getString(R.string.tab_accounts),
                    context.getString(R.string.faq_about_accounts)
                )
            )
            list.add(
                Pair(
                    context.getString(R.string.tab_transactions),
                    context.getString(R.string.faq_about_transactions)
                )
            )
            list.add(
                Pair(
                    context.getString(R.string.debts),
                    context.getString(R.string.faq_about_debts)
                )
            )
            list.add(
                Pair(
                    context.getString(R.string.tab_home),
                    context.getString(R.string.faq_about_home)
                )
            )
            return list
        }
}
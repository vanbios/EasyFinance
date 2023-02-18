package com.androidcollider.easyfin.faq

import android.util.Pair

/**
 * @author Ihor Bilous
 */
interface FAQMVP {
    interface Model {
        val info: List<Pair<String, String>>
    }

    interface View {
        fun setInfo(list: List<Pair<String, String>>)
    }

    interface Presenter {
        fun setView(view: View?)
        fun loadInfo()
    }
}
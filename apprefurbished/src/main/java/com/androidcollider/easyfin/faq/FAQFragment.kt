package com.androidcollider.easyfin.faq

import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class FAQFragment : CommonFragment(), FAQMVP.View {

    lateinit var recyclerView: RecyclerView

    @JvmField
    @Inject
    var presenter: FAQMVP.Presenter? = null

    override val contentView: Int
        get() = R.layout.frg_faq

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter?.setView(this)
        presenter?.loadInfo()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.rvFAQ)
    }

    override fun setInfo(list: List<Pair<String, String>>) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = RecyclerFAQAdapter(list)
    }

    override val title: String
        get() = getString(R.string.app_faq)
}
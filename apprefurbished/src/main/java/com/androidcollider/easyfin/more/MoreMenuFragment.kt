package com.androidcollider.easyfin.more

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment
import javax.inject.Inject

class MoreMenuFragment : CommonFragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: RecyclerMoreMenuAdapter

    @Inject
    lateinit var dialogManager: DialogManager

    override val contentView: Int
        get() = R.layout.frg_more_menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)

        recyclerAdapter = RecyclerMoreMenuAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.rvMoreMenu)
        val items = listOf(
            MoreMenuItem(
                1,
                getString(R.string.transaction_categories),
                R.drawable.ic_more_blue_gray_48dp
            ),
            MoreMenuItem(
                2,
                getString(R.string.settings),
                R.drawable.ic_settings_blue_gray_24dp
            ),
            MoreMenuItem(
                3,
                getString(R.string.app_faq),
                R.drawable.ic_help_circle_blue_gray_24dp
            ),
            MoreMenuItem(
                4,
                getString(R.string.app_about),
                R.drawable.ic_information_blue_gray_24dp
            )
        )

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerAdapter.setData(items)
        recyclerAdapter.setItemSelectedListener(object : MoreMenuItemSelectedListener {
            override fun onItemSelected(id: Int) {
                goToScreen(id)
            }
        })
        recyclerView.adapter = recyclerAdapter
    }

    private fun goToScreen(id: Int) {
        when (id) {
            1 -> goToTransactionCategoriesScreen()
            2 -> goToSettingsScreen()
            3 -> goToFAQScreen()
            4 -> showAppAboutDialog()
        }
    }

    private fun goToTransactionCategoriesScreen() {
        getParentNavController()?.navigate(R.id.transactionCategoriesRootFragment)
    }

    private fun goToSettingsScreen() {
        getParentNavController()?.navigate(R.id.navigation_settings)
    }

    private fun goToFAQScreen() {
        getParentNavController()?.navigate(R.id.FAQFragment)
    }

    private fun getParentNavController(): NavController? {
        activity?.let {
            return (it.supportFragmentManager
                .findFragmentById(R.id.fragment_container) as NavHostFragment)
                .navController
        }
        return null
    }

    private fun showAppAboutDialog() {
        activity?.let {
            dialogManager.showAppAboutDialog(it)
        }
    }

    override val title: String
        get() = getString(R.string.settings)
}
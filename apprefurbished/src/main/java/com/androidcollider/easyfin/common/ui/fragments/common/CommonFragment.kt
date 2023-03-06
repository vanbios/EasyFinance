package com.androidcollider.easyfin.common.ui.fragments.common

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsManager
import com.androidcollider.easyfin.common.ui.MainActivity
import java.util.*
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
abstract class CommonFragment : AbstractBaseFragment() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        (activity?.application as App).component?.inject(this)
        analyticsManager.sendScreeName(realTag)
    }

    override fun onResume() {
        super.onResume()
        while (!pendingTransactions.isEmpty()) {
            pendingTransactions.pollFirst().run()
        }
    }

    protected fun addFragment(f: CommonFragment) {
        (activity as MainActivity?)?.addFragment(f)
    }

    protected fun finish() {
        tryExecuteTransaction { requireFragmentManager().popBackStack() }
    }

    protected fun popAll() {
        requireFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    abstract val title: String
    private val pendingTransactions = LinkedList<Runnable>()
    private fun tryExecuteTransaction(runnable: Runnable) {
        if (isResumed) runnable.run() else pendingTransactions.addLast(runnable)
    }

    private val realTag: String
        get() = this.javaClass.name
}
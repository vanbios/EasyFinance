package com.androidcollider.easyfin.common.ui.fragments.common

import android.os.Bundle
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsManager
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

    abstract val title: String

    private val realTag: String
        get() = this.javaClass.name
}
package com.androidcollider.easyfin.common.ui.fragments.common

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.accounts.list.AccountsFragment.Companion.MODE
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.Companion.INPUT_VALUE
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.Companion.NUMERIC_DIALOG_REQUEST_KEY
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment.Companion.OUTPUT_VALUE
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
abstract class CommonFragmentAddEdit : CommonFragment() {

    @Inject
    lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(NUMERIC_DIALOG_REQUEST_KEY) { _, bundle ->
            updateAmount(bundle.getString(OUTPUT_VALUE, "0,00"))
        }
    }

    override val title: String
        get() = getString(R.string.app_name)

    protected fun setToolbar() {
        activity?.let {
            val actionBar = (it as AppCompatActivity).supportActionBar
            if (actionBar != null) {
                val actionBarLayout = it.layoutInflater.inflate(
                    R.layout.save_close_buttons_toolbar, null
                ) as ViewGroup
                val layoutParams = ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT
                )
                actionBar.setDisplayShowTitleEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.setDisplayShowCustomEnabled(true)
                actionBar.setCustomView(actionBarLayout, layoutParams)
                val parent = actionBarLayout.parent as Toolbar
                parent.setContentInsetsAbsolute(0, 0)
                val btnSave = actionBarLayout.findViewById<Button>(R.id.btnToolbarSave)
                val btnClose = actionBarLayout.findViewById<Button>(R.id.btnToolbarClose)
                btnSave.setOnClickListener { handleSaveAction() }
                btnClose.setOnClickListener { findNavController().navigateUp() }
            }
        }
    }

    protected fun setTVTextSize(textView: TextView, s: String, min: Int, max: Int) {
        val length = s.length
        when {
            length in (min + 1)..max ->
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            length > max -> textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            else -> textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f)
        }
    }

    protected fun openNumericDialog(initialValue: String) {
        findNavController().navigate(
            R.id.numericDialogFragment,
            bundleOf(
                INPUT_VALUE to initialValue
            )
        )
    }

    protected fun showDialogNoAccount(message: String, withFinish: Boolean) {
        activity?.let {
            dialogManager.showNoAccountsDialog(
                it,
                message,
                { goToAddAccount(withFinish) }) { findNavController().navigateUp() }
        }
    }

    private fun goToAddAccount(withFinish: Boolean) {
        val navController = findNavController()
        if (withFinish) navController.navigateUp()
        navController.navigate(
            R.id.addAccountFragment,
            bundleOf(MODE to 0)
        )
    }

    protected abstract fun handleSaveAction()
    protected abstract fun updateAmount(amount: String)
}
package com.androidcollider.easyfin.common.managers.ui.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.BuildConfig
import com.androidcollider.easyfin.R
import java.util.*

class DialogManager {

    fun showDeleteDialog(context: Context,
                         content: String,
                         onPositive: DialogCallback) {
        showDialog(
                context,
                R.string.dialog_title_delete,
                content,
                R.string.delete,
                R.string.cancel,
                onPositive,
                null
        )
    }

    fun showNoAccountDialog(context: Context,
                            onPositive: DialogCallback) {
        showDialog(
                context,
                R.string.no_account,
                context.getString(R.string.dialog_text_main_no_accounts),
                R.string.new_account,
                R.string.later,
                onPositive,
                null
        )
    }

    fun showNoAccountsDialog(context: Context,
                             content: String,
                             onPositive: DialogCallback,
                             onNegative: DialogCallback) {
        showDialog(
                context,
                R.string.no_account,
                content,
                R.string.new_account,
                R.string.close,
                onPositive,
                onNegative
        )
    }

    fun showImportDBDialog(context: Context,
                           onPositive: DialogCallback) {
        showDialog(
                context,
                R.string.import_db,
                context.getString(R.string.import_dialog_warning),
                R.string.confirm,
                R.string.cancel,
                onPositive,
                null
        )
    }

    private fun showDialog(context: Context,
                           title: Int,
                           content: String,
                           positiveText: Int,
                           negativeText: Int,
                           onPositive: DialogCallback,
                           onNegative: DialogCallback?) {
        MaterialDialog(context)
                .title(title)
                .message(text = content)
                .positiveButton(positiveText, click = onPositive)
                .negativeButton(negativeText, click = onNegative)
                .cancelable(false)
                .show()
    }

    fun showAppAboutDialog(context: Context) {
        val appAboutDialog: MaterialDialog = MaterialDialog(context)
                .title(R.string.app_about)
                .customView(R.layout.app_about, dialogWrapContent = true)
                .positiveButton(R.string.ok)
        val appAboutLayout: View = appAboutDialog.getCustomView()
        val tvVersion = appAboutLayout.findViewById<TextView>(R.id.tv_about_app_version)
        tvVersion.text = String.format(
                "%1\$s %2\$s",
                context.getString(R.string.about_app_version),
                BuildConfig.VERSION_NAME
        )
        val tvDeveloper = appAboutLayout.findViewById<TextView>(R.id.tv_about_app_developer)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        tvDeveloper.text = context.getString(R.string.about_app_developer, year)
        appAboutDialog.show()
    }

    fun buildBalanceSettingsDialog(context: Context): MaterialDialog {
        return MaterialDialog(context)
                .title(R.string.settings)
                .customView(R.layout.item_main_balance_menu, dialogWrapContent = true)
                .positiveButton(R.string.done)
    }

    fun buildAddTransactionCategoryDialog(context: Context,
                                          onPositive: DialogCallback,
                                          onNegative: DialogCallback): MaterialDialog {
        return buildTransactionCategoryDialog(
                context,
                onPositive,
                onNegative,
                R.string.add_category
        )
    }

    fun buildUpdateTransactionCategoryDialog(context: Context,
                                             onPositive: DialogCallback,
                                             onNegative: DialogCallback): MaterialDialog {
        return buildTransactionCategoryDialog(
                context,
                onPositive,
                onNegative,
                R.string.edit_category
        )
    }

    private fun buildTransactionCategoryDialog(context: Context,
                                               onPositive: DialogCallback,
                                               onNegative: DialogCallback,
                                               titleRes: Int): MaterialDialog {
        return MaterialDialog(context)
                .title(titleRes)
                .customView(R.layout.layout_new_transaction_category, dialogWrapContent = true)
                .positiveButton(R.string.save, click = onPositive)
                .negativeButton(R.string.cancel, click = onNegative)
                .cancelable(false)
                .noAutoDismiss()
    }
}
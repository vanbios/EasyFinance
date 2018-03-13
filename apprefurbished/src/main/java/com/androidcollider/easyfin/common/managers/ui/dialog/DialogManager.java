package com.androidcollider.easyfin.common.managers.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.BuildConfig;
import com.androidcollider.easyfin.R;

/**
 * @author Ihor Bilous
 */

public class DialogManager {

    public void showDeleteDialog(Context context,
                                 String content,
                                 MaterialDialog.SingleButtonCallback onPositive) {
        showDialog(
                context,
                context.getString(R.string.dialog_title_delete),
                content,
                context.getString(R.string.delete),
                context.getString(R.string.cancel),
                onPositive,
                null
        );
    }

    public void showNoAccountDialog(Context context,
                                    MaterialDialog.SingleButtonCallback onPositive) {
        showDialog(
                context,
                context.getString(R.string.no_account),
                context.getString(R.string.dialog_text_main_no_accounts),
                context.getString(R.string.new_account),
                context.getString(R.string.later),
                onPositive,
                null
        );
    }

    public void showNoAccountsDialog(Context context,
                                     String content,
                                     MaterialDialog.SingleButtonCallback onPositive,
                                     MaterialDialog.SingleButtonCallback onNegative) {
        showDialog(
                context,
                context.getString(R.string.no_account),
                content,
                context.getString(R.string.new_account),
                context.getString(R.string.close),
                onPositive,
                onNegative
        );
    }

    public void showImportDBDialog(Context context,
                                   MaterialDialog.SingleButtonCallback onPositive) {
        showDialog(
                context,
                context.getString(R.string.import_db),
                context.getString(R.string.import_dialog_warning),
                context.getString(R.string.confirm),
                context.getString(R.string.cancel),
                onPositive,
                null
        );
    }

    private void showDialog(Context context,
                            String title,
                            String content,
                            String positiveText,
                            String negativeText,
                            MaterialDialog.SingleButtonCallback onPositive,
                            MaterialDialog.SingleButtonCallback onNegative) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .onPositive(onPositive)
                .onNegative(onNegative)
                .cancelable(false)
                .show();
    }

    public void showAppAboutDialog(Context context) {
        MaterialDialog appAboutDialog = new MaterialDialog.Builder(context)
                .title(R.string.app_about)
                .customView(R.layout.app_about, true)
                .positiveText(R.string.ok)
                .build();

        View appAboutLayout = appAboutDialog.getCustomView();
        if (appAboutLayout != null) {
            TextView tvVersion = appAboutLayout.findViewById(R.id.tvAboutAppVersion);
            tvVersion.setText(
                    String.format(
                            "%1$s %2$s",
                            context.getString(R.string.about_app_version),
                            BuildConfig.VERSION_NAME
                    )
            );
        }

        appAboutDialog.show();
    }

    public MaterialDialog buildBalanceSettingsDialog(Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.settings)
                .customView(R.layout.item_main_balance_menu, true)
                .positiveText(R.string.done)
                .build();
    }

    public MaterialDialog buildAddTransactionCategoryDialog(Context context,
                                                            MaterialDialog.SingleButtonCallback onPositive,
                                                            MaterialDialog.SingleButtonCallback onNegative) {
        return buildTransactionCategoryDialog(
                context,
                onPositive,
                onNegative,
                R.string.add_category
        );
    }

    public MaterialDialog buildUpdateTransactionCategoryDialog(Context context,
                                                               MaterialDialog.SingleButtonCallback onPositive,
                                                               MaterialDialog.SingleButtonCallback onNegative) {
        return buildTransactionCategoryDialog(
                context,
                onPositive,
                onNegative,
                R.string.edit_category
        );
    }

    private MaterialDialog buildTransactionCategoryDialog(Context context,
                                                          MaterialDialog.SingleButtonCallback onPositive,
                                                          MaterialDialog.SingleButtonCallback onNegative,
                                                          int titleRes) {
        return new MaterialDialog.Builder(context)
                .title(titleRes)
                .customView(R.layout.layout_new_transaction_category, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .onPositive(onPositive)
                .onNegative(onNegative)
                .cancelable(false)
                .autoDismiss(false)
                .build();
    }
}
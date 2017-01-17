package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;

/**
 * @author Ihor Bilous
 */

public abstract class CommonFragmentAddEdit extends CommonFragment {

    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }

    void setToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            ViewGroup actionBarLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.save_close_buttons_toolbar, null);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarLayout, layoutParams);

            Toolbar parent = (Toolbar) actionBarLayout.getParent();
            parent.setContentInsetsAbsolute(0, 0);

            Button btnSave = (Button) actionBarLayout.findViewById(R.id.btnToolbarSave);
            Button btnClose = (Button) actionBarLayout.findViewById(R.id.btnToolbarClose);

            btnSave.setOnClickListener(v -> handleSaveAction());

            btnClose.setOnClickListener(v -> finish());
        }
    }

    void setTVTextSize(TextView textView, String s, int min, int max) {
        int length = s.length();
        if (length > min && length <= max)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        else if (length > max)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        else
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
    }

    void openNumericDialog(String initialValue) {
        Bundle args = new Bundle();
        args.putString("value", initialValue);

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 1);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog");
    }

    void showDialogNoAccount(String message, boolean withFinish) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(message)
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .onPositive((dialog, which) -> goToAddAccount(withFinish))
                .onNegative((dialog, which) -> finish())
                .cancelable(false)
                .show();
    }

    private void goToAddAccount(boolean withFinish) {
        if (withFinish) finish();

        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }

    abstract void handleSaveAction();
}
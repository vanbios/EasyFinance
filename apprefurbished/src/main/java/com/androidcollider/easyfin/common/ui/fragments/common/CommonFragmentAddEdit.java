package com.androidcollider.easyfin.common.ui.fragments.common;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.FrgNumericDialog;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public abstract class CommonFragmentAddEdit extends CommonFragment {

    @Inject
    DialogManager dialogManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_name);
    }

    protected void setToolbar() {
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

    protected void setTVTextSize(TextView textView, String s, int min, int max) {
        int length = s.length();
        if (length > min && length <= max)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        else if (length > max)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        else
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
    }

    protected void openNumericDialog(String initialValue) {
        Bundle args = new Bundle();
        args.putString("value", initialValue);

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 1);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog");
    }

    protected void showDialogNoAccount(String message, boolean withFinish) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showNoAccountsDialog(
                    activity,
                    message,
                    (dialog, which) -> goToAddAccount(withFinish),
                    (dialog, which) -> finish()
            );
        }
    }

    private void goToAddAccount(boolean withFinish) {
        if (withFinish) finish();

        AddAccountFragment addAccountFragment = new AddAccountFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        addAccountFragment.setArguments(arguments);

        addFragment(addAccountFragment);
    }

    protected abstract void handleSaveAction();
}
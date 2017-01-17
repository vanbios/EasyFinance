package com.androidcollider.easyfin.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.acollider.numberkeyboardview.CalculatorView;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class FrgNumericDialog extends DialogFragment {

    @BindView(R.id.containerFrgNumericDialog)
    FrameLayout frameLayout;
    @BindView(R.id.btnFrgNumericDialogCommit)
    TextView tvCommit;
    @BindView(R.id.btnFrgNumericDialogCancel)
    TextView tvCancel;

    private CalculatorView calculatorView;

    private OnCommitAmountListener callback;
    private final boolean isApiHoneycombAndHigher = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    @Inject
    NumberFormatManager numberFormatManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_numeric_dialog, container, false);
        ButterKnife.bind(this, view);

        try {
            callback = (OnCommitAmountListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnCommitAmountListener");
        }

        calculatorView = new CalculatorView(getActivity());
        calculatorView.setShowSpaces(true);
        calculatorView.setShowSelectors(true);
        calculatorView.build();

        try {
            String inputValue = getArguments().getString("value");
            if (inputValue != null) {
                String str = numberFormatManager.prepareStringToSeparate(inputValue);
                String integers;
                String hundreds = "";

                if (str.contains(",")) {
                    int j = str.indexOf(",");
                    integers = str.substring(0, j);
                    String h = str.substring(j + 1);
                    if (!h.equals("00")) hundreds = h;
                } else integers = str;

                if (integers.equals("0")) integers = "";

                calculatorView.setIntegers(integers);
                calculatorView.setHundredths(hundreds);
                calculatorView.formatAndShow();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        frameLayout.addView(calculatorView);

        return view;
    }

    @OnClick({R.id.btnFrgNumericDialogCommit, R.id.btnFrgNumericDialogCancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFrgNumericDialogCommit:
                callback.onCommitAmountSubmit(calculatorView.getCalculatorValue());
                dismiss();
                break;
            case R.id.btnFrgNumericDialogCancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = (metrics.heightPixels * 4) / 5;
        int width = (metrics.widthPixels * 7) / 8;

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = isApiHoneycombAndHigher ?
                new Dialog(getActivity()) :
                new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }


    public interface OnCommitAmountListener {
        void onCommitAmountSubmit(String amount);
    }
}
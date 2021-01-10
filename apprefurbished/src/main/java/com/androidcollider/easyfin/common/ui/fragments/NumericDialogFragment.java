package com.androidcollider.easyfin.common.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acollider.numberkeyboardview.CalculatorView;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class NumericDialogFragment extends DialogFragment {

    FrameLayout frameLayout;
    TextView tvCommit;
    TextView tvCancel;

    private CalculatorView calculatorView;

    private OnCommitAmountListener callback;

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
        setupUI(view);

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

    private void setupUI(View view) {
        frameLayout = view.findViewById(R.id.containerFrgNumericDialog);
        tvCommit = view.findViewById(R.id.btnFrgNumericDialogCommit);
        tvCancel = view.findViewById(R.id.btnFrgNumericDialogCancel);

        tvCommit.setOnClickListener(v -> {
            callback.onCommitAmountSubmit(calculatorView.getCalculatorValue());
            dismiss();
        });
        tvCancel.setOnClickListener(v -> dismiss());
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
        Dialog dialog = new Dialog(getActivity());
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
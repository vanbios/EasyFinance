package com.androidcollider.easyfin.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.androidcollider.easyfin.utils.DoubleFormatUtils;

public class FrgNumericDialog extends DialogFragment {

    private OnCommitAmountListener callback;
    final private boolean isApiHoneycombAndHigher = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_numeric_dialog, container, false);

        try {
            callback = (OnCommitAmountListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnCommitAmountListener");
        }

        final CalculatorView calculatorView = new CalculatorView(getActivity());
        calculatorView.setShowSpaces(true);
        calculatorView.setShowSelectors(true);
        calculatorView.build();

        try {
            String inputValue = getArguments().getString("value");
            if (inputValue != null) {
                String str = DoubleFormatUtils.prepareStringToSeparate(inputValue);
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

        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.containerFrgNumericDialog);
        frameLayout.addView(calculatorView);

        TextView tvCommit = (TextView) view.findViewById(R.id.btnFrgNumericDialogCommit);
        TextView tvCancel = (TextView) view.findViewById(R.id.btnFrgNumericDialogCancel);

        tvCommit.setOnClickListener(v -> {
            callback.onCommitAmountSubmit(calculatorView.getCalculatorValue());
            dismiss();
        });

        tvCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) return;

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = (metrics.heightPixels * 4) / 5;
        int width = (metrics.widthPixels * 7) / 8;

        getDialog().getWindow().setLayout(width, height);
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

package com.androidcollider.easyfin.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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

                String s2 = DoubleFormatUtils.prepareStringToSeperate(inputValue);

                String integers;
                String hundreds = "";

                if (s2.contains(",")) {
                    int j = s2.indexOf(",");
                    integers = s2.substring(0, j);

                    String h = s2.substring(j + 1);

                    if (!h.equals("00")) {
                        hundreds = h;
                    }
                }

                else {
                    integers = s2;
                }

                if (integers.equals("0")) {
                    integers = "";
                }

                calculatorView.setIntegers(integers);
                calculatorView.setHundredths(hundreds);
                calculatorView.formatAndShow();

            }
        }

        catch (NullPointerException e) {e.printStackTrace();}


        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.containerFrgNumericDialog);
        frameLayout.addView(calculatorView);


        Button btnCommit = (Button) view.findViewById(R.id.btnFrgNumericDialogCommit);
        Button btnCancel = (Button) view.findViewById(R.id.btnFrgNumericDialogCancel);

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onCommitAmountSubmit(calculatorView.getCalculatorValue());
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        Dialog dialog;

        if (!isApiHoneycombAndHigher) {
            dialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar);
        }

        else {
            dialog = new Dialog(getActivity());
        }


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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

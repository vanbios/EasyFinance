package com.androidcollider.easyfin.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class EditTextAmountWatcher implements TextWatcher {

    private EditText et;
    private boolean isInWatcher;

    public EditTextAmountWatcher(EditText et) {
        this.et = et;
        isInWatcher = false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable e) {
        if (isInWatcher) return;
        isInWatcher = true;

        String input = et.getText().toString().replaceAll("\\s+", "");
        int j = input.length();
        if (j > 3) {
            String res;
            if (input.contains(","))
                input = input.replaceAll(",", ".");
            if (input.contains(".")) {
                j = input.indexOf(".");
                StringBuilder sb = new StringBuilder(input.substring(0, j));
                String append = input.substring(j, input.length());
                for (int k = sb.length() - 3; k > 0; k -= 3) {
                    sb.insert(k, " ");
                }
                res = sb.toString() + append;
            } else {
                StringBuilder sb = new StringBuilder(input);
                for (int k = sb.length() - 3; k > 0; k -= 3) {
                    sb.insert(k, " ");
                }
                res = sb.toString();
            }
            et.setText(res);
            et.setSelection(et.getText().length());
        } else {
            et.setText(input);
            et.setSelection(et.getText().length());
        }

        isInWatcher = false;
    }
}
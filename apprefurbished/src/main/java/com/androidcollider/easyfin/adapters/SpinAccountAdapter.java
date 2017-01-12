package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.models.Account;

import java.util.List;

import lombok.Getter;

@Getter
abstract class SpinAccountAdapter extends ArrayAdapter<Account> {

    private final TypedArray typeIconsArray;
    private final List<Account> accountList;
    private final String[] curArray, curLangArray;
    private LayoutInflater inflater;

    private NumberFormatManager numberFormatManager;


    SpinAccountAdapter(Context context, int headLayout, List<Account> accountL, NumberFormatManager numberFormatManager) {
        super(context, headLayout, accountL);
        accountList = accountL;
        typeIconsArray = context.getResources().obtainTypedArray(R.array.account_type_icons);
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.numberFormatManager = numberFormatManager;
    }

    @Override
    public View getDropDownView(int position, View view, @NonNull ViewGroup parent) {
        return getCustomDropView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        return getCustomHeadView(position, parent);
    }

    private View getCustomDropView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(R.layout.spin_account_for_trans_dropdown, parent, false);
        TextView name = (TextView) dropSpinner.findViewById(R.id.tvSpinDropdownAccountName);
        name.setText(accountList.get(position).getName());
        ImageView icon = (ImageView) dropSpinner.findViewById(R.id.ivSpinDropdownAccountType);
        icon.setImageResource(typeIconsArray.getResourceId(accountList.get(position).getType(), 0));
        TextView amountText = (TextView) dropSpinner.findViewById(R.id.tvSpinDropdownAccountAmount);

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        String amount = numberFormatManager.doubleToStringFormatter(accountList.get(position).getAmount(), FORMAT, PRECISE);
        String cur = accountList.get(position).getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        amountText.setText(String.format("%1$s %2$s", amount, curLang));

        return dropSpinner;
    }

    public abstract View getCustomHeadView(int position, ViewGroup parent);

    public Account getItem(int position) {
        return accountList.get(position);
    }
}
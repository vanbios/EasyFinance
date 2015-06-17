package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class SpinnerAccountForTransAdapter extends ArrayAdapter<Account> {

    final TypedArray typeIcons;
    final ArrayList<Account> accountsList;
    final String[] accountType;
    final String[] accountCurr;
    final String[] accountCurrLang;
    LayoutInflater inflater;


    public SpinnerAccountForTransAdapter(Context context, int txtViewResourceId, ArrayList<Account> accountsL) {

        super(context, txtViewResourceId, accountsL);
        accountsList = accountsL;

        typeIcons = context.getResources().obtainTypedArray(R.array.expense_type_icons);
        accountType = context.getResources().getStringArray(R.array.account_type_array);
        accountCurr = context.getResources().getStringArray(R.array.account_currency_array);
        accountCurrLang = context.getResources().getStringArray(R.array.account_currency_array_language);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomView(position, parent);}
    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomTopView(pos, parent);}

    public View getCustomView(int position, ViewGroup parent) {
        View mySpinner = inflater.inflate(R.layout.spin_account_for_trans_dropdown, parent, false);
        TextView mainText = (TextView) mySpinner.findViewById(R.id.tvSpinDropdownAccountName);
        mainText.setText(accountsList.get(position).getName());

        ImageView leftIcon = (ImageView) mySpinner.findViewById(R.id.ivSpinDropdownAccountType);

        String typeExp = accountsList.get(position).getType();

        for (int i = 0; i < accountType.length; i++) {
            if (accountType[i].equals(typeExp)) {
                leftIcon.setImageResource(typeIcons.getResourceId(i, 0));
            }
        }

        TextView amountText = (TextView) mySpinner.findViewById(R.id.tvSpinDropdownAccountAmount);

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        String amount = FormatUtils.doubleFormatter(accountsList.get(position).getAmount(), FORMAT, PRECISE);
        String cur = accountsList.get(position).getCurrency();

        String curLang = null;

        for (int i = 0; i < accountCurr.length; i++) {
            if (cur.equals(accountCurr[i])) {
                curLang = accountCurrLang[i];
            }
        }

        amountText.setText(amount + " " + curLang);


        return mySpinner;
    }

    public View getCustomTopView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(R.layout.spin_custom_item, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(R.id.tvSpinTopText);
        headText.setText(accountsList.get(position).getName());

        return headSpinner;
    }


    public Account getItem (int position) {
        return accountsList.get(position);
    }

}

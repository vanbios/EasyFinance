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
import com.androidcollider.easyfin.utils.DoubleFormatUtils;

import java.util.ArrayList;


public class SpinAccountForTransHeadIconAdapter extends ArrayAdapter<Account> {

    private final TypedArray typeIconsArray;
    private final ArrayList<Account> accountList;
    private final String[] curArray, curLangArray;
    private LayoutInflater inflater;


    public SpinAccountForTransHeadIconAdapter(Context context, int headLayout, ArrayList<Account> accountsL) {

        super(context, headLayout, accountsL);
        accountList = accountsL;

        typeIconsArray = context.getResources().obtainTypedArray(R.array.account_type_icons);
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomDropView(position, parent);}
    @Override
    public View getView(int position, View view, ViewGroup parent) {return getCustomHeadView(position, parent);}

    public View getCustomDropView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(R.layout.spin_account_for_trans_dropdown, parent, false);
        TextView name = (TextView) dropSpinner.findViewById(R.id.tvSpinDropdownAccountName);
        name.setText(accountList.get(position).getName());

        ImageView icon = (ImageView) dropSpinner.findViewById(R.id.ivSpinDropdownAccountType);


        icon.setImageResource(typeIconsArray.getResourceId(accountList.get(position).getType(), 0));

        TextView amountText = (TextView) dropSpinner.findViewById(R.id.tvSpinDropdownAccountAmount);

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        String amount = DoubleFormatUtils.doubleToStringFormatter(accountList.get(position).getAmount(), FORMAT, PRECISE);
        String cur = accountList.get(position).getCurrency();

        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        amountText.setText(amount + " " + curLang);

        return dropSpinner;
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(R.layout.spin_head_icon_text, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(R.id.tvSpinHeadIconText);
        headText.setText(accountList.get(position).getName());

        ImageView headIcon = (ImageView) headSpinner.findViewById(R.id.ivSpinHeadIconText);


        headIcon.setImageResource(typeIconsArray.getResourceId(accountList.get(position).getType(), 0));

        return headSpinner;
    }

    public Account getItem (int position) {
        return accountList.get(position);
    }

}

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

import java.util.List;

public class SpinnerAccountForTransAdapter extends ArrayAdapter<String> {

    //final TypedArray flags;
    final TypedArray type_icons;
    final List<Account> accountsList;
    final String[] account_type;
    final String[] account_curr;
    final String[] account_curr_lang;
    LayoutInflater inflater;

    public SpinnerAccountForTransAdapter(Context context, int txtViewResourceId,
                                         List<String> accounts, List<Account> accountsL) {
        super(context, txtViewResourceId, accounts);
        accountsList = accountsL;
        //flags = context.getResources().obtainTypedArray(R.array.flags);
        type_icons = context.getResources().obtainTypedArray(R.array.expense_type_icons);
        account_type = context.getResources().getStringArray(R.array.account_type_array);
        account_curr = context.getResources().getStringArray(R.array.account_currency_array);
        account_curr_lang = context.getResources().getStringArray(R.array.account_currency_array_language);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomView(position, view, parent);}
    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomTopView(pos, view, parent);}

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View mySpinner = inflater.inflate(R.layout.spin_account_for_trans_dropdown, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.tvSpinDropdownAccountName);
        main_text.setText(accountsList.get(position).getName());

        ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.ivSpinDropdownAccountType);

        String typeExp = accountsList.get(position).getType();

        for (int i = 0; i < account_type.length; i++) {
            if (account_type[i].equals(typeExp)) {
                left_icon.setImageResource(type_icons.getResourceId(i, 0));
            }
        }

        TextView amount_text = (TextView) mySpinner.findViewById(R.id.tvSpinDropdownAccountAmount);

        int PRECISE = 100;
        String FORMAT = "0.00";

        String amount = FormatUtils.doubleFormatter(accountsList.get(position).getAmount(), FORMAT, PRECISE);
        String cur = accountsList.get(position).getCurrency();

        String cur_lang = null;

        for (int i = 0; i < account_curr.length; i++) {
            if (cur.equals(account_curr[i])) {
                cur_lang = account_curr_lang[i];
            }
        }

        amount_text.setText(amount + " " + cur_lang);


        return mySpinner;
    }

    public View getCustomTopView(int position, View convertView, ViewGroup parent) {
        View topSpinner = inflater.inflate(R.layout.spin_custom_item, parent, false);
        TextView top_text = (TextView) topSpinner.findViewById(R.id.tvSpinTopText);
        top_text.setText(accountsList.get(position).getName());

        return topSpinner;
    }

}

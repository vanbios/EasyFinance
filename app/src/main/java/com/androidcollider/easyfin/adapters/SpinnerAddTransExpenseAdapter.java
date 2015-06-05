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

public class SpinnerAddTransExpenseAdapter extends ArrayAdapter<String> {

    //final TypedArray flags;
    final TypedArray type;
    final List<Account> accountsList;
    final String[] exp_type;
    final String[] exp_curr;
    final String[] exp_curr_lang;
    LayoutInflater inflater;

    public SpinnerAddTransExpenseAdapter(Context context, int txtViewResourceId,
                                         List<String> accounts, List<Account> accountsL) {
        super(context, txtViewResourceId, accounts);
        accountsList = accountsL;
        //flags = context.getResources().obtainTypedArray(R.array.flags);
        type = context.getResources().obtainTypedArray(R.array.expense_type_48);
        exp_type = context.getResources().getStringArray(R.array.expense_type_array);
        exp_curr = context.getResources().getStringArray(R.array.expense_currency_array);
        exp_curr_lang = context.getResources().getStringArray(R.array.expense_currency_array_language);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomView(position, view, parent);}
    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomTopView(pos, view, parent);}

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View mySpinner = inflater.inflate(R.layout.spinner_add_trans_expense_dropdown, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.tvSpinExpenseNameCustom);
        main_text.setText(accountsList.get(position).getName());

        ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.ivSpinExpenseTypeCustom);

        String typeExp = accountsList.get(position).getType();

        for (int i = 0; i < exp_type.length; i++) {
            if (exp_type[i].equals(typeExp)) {
                left_icon.setImageResource(type.getResourceId(i, 0));
            }
        }

        TextView amount_text = (TextView) mySpinner.findViewById(R.id.tvSpinExpenseAmountCustom);

        int PRECISE = 100;
        String FORMAT = "0.00";

        String amount = FormatUtils.doubleFormatter(accountsList.get(position).getAmount(), FORMAT, PRECISE);
        String cur = accountsList.get(position).getCurrency();

        String cur_lang = null;

        for (int i = 0; i < exp_curr.length; i++) {
            if (cur.equals(exp_curr[i])) {
                cur_lang = exp_curr_lang[i];
            }
        }

        amount_text.setText(amount + " " + cur_lang);


        /*ImageView right_icon = (ImageView) mySpinner.findViewById(R.id.ivSpinExpenseCurrencyCustom);

        String currExp = accountsList.get(position).getCurrency();

        for (int i = 0; i < exp_curr.length; i++) {
            if (exp_curr[i].equals(currExp)) {
                right_icon.setImageResource(flags.getResourceId(position, 0));
            }
        }*/

        return mySpinner;
    }

    public View getCustomTopView(int position, View convertView, ViewGroup parent) {
        View topSpinner = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView top_text = (TextView) topSpinner.findViewById(R.id.tvTopSpinCatTrans);
        top_text.setText(accountsList.get(position).getName());

        return topSpinner;
    }

}

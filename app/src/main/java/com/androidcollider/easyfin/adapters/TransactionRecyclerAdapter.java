package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Transaction> transactionArrayList;

    private final TypedArray icons;
    private final TypedArray accountTypeIcons;

    private final String[] categories;
    private final String[] currency;

    private final String[] currencyLanguage;
    private final String[] accountType;



    public TransactionRecyclerAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;

        icons = context.getResources().obtainTypedArray(R.array.trans_categories_icons);
        categories = context.getResources().getStringArray(R.array.cat_transaction_array);
        currency = context.getResources().getStringArray(R.array.account_currency_array);
        currencyLanguage = context.getResources().getStringArray(R.array.account_currency_array_language);
        accountType = context.getResources().getStringArray(R.array.account_type_array);
        accountTypeIcons = context.getResources().obtainTypedArray(R.array.account_type_icons);
    }


    @Override
    public int getItemCount() {return transactionArrayList.size();}

    @Override
    public long getItemId(int position) {return position;}

    Transaction getTransaction(int position) {
        return transactionArrayList.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final int PRECISE = 100;
        final String FORMAT = "0.00";
        final String DATEFORMAT = "dd.MM.yyyy";

        Transaction transaction = getTransaction(position);

        holder.tvTransAccountName.setText(transaction.getAccountName());
        holder.tvTransDate.setText(DateFormat.longToDateString(transaction.getDate(), DATEFORMAT));

        String amount = FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE);

        String cur = transaction.getCurrency();

        String curLang = null;

        for (int i = 0; i < currency.length; i++) {
            if (cur.equals(currency[i])) {
                curLang = currencyLanguage[i];
            }
        }


        if (amount.contains("-")) {
            holder.tvTransAmount.setText(amount + " " + curLang);
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.custom_red));
        }
        else {
            holder.tvTransAmount.setText("+" + amount + " " + curLang);
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.custom_green));
        }

        String cat = transaction.getCategory();

        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(cat)) {
                holder.ivTransCategory.setImageDrawable(icons.getDrawable(i));
            }
        }

        String type = transaction.getAccountType();

        for (int i = 0; i < accountType.length; i++) {
            if (accountType[i].equals(type)) {
                holder.ivTransAccountType.setImageDrawable(accountTypeIcons.getDrawable(i));
            }
        }
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        //private final View mView;
        private final TextView tvTransAmount;
        private final TextView tvTransAccountName;
        private final TextView tvTransDate;
        private final ImageView ivTransCategory;
        private final ImageView ivTransAccountType;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvTransAmount = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAmount);
            tvTransAccountName = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAccountName);
            tvTransDate = (TextView) view.findViewById(R.id.tvItemFragmentTransactionDate);
            ivTransCategory = (ImageView) view.findViewById(R.id.ivItemFragmentTransactionCategory);
            ivTransAccountType = (ImageView) view.findViewById(R.id.ivItemFragmentTransactionAccountType);
        }
    }
}

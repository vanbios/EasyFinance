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
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Transaction> transactionArrayList;

    private final TypedArray icons;
    private final TypedArray expense_type_icons;
    //private final TypedArray flags;
    private final String[] categories;
    private final String[] currency;

    private final String[] currency_language;
    private final String[] expense_type;

    DataSource dataSource;


    public TransactionRecyclerAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;

        icons = context.getResources().obtainTypedArray(R.array.icons);
        //flags = context.getResources().obtainTypedArray(R.array.flags);
        categories = context.getResources().getStringArray(R.array.cat_transaction_array);
        currency = context.getResources().getStringArray(R.array.expense_currency_array);
        currency_language = context.getResources().getStringArray(R.array.expense_currency_array_language);
        expense_type = context.getResources().getStringArray(R.array.expense_type_array);
        expense_type_icons = context.getResources().obtainTypedArray(R.array.expense_type_48);

        dataSource = new DataSource(context);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final int PRECISE = 100;
        final String FORMAT = "0.00";
        final String DATEFORMAT = "dd-MM-yyyy";

        Transaction transaction = getTransaction(position);

        String name = transaction.getAccount_name();

        holder.tvItemFragmentTransactionAccountName.setText(name);
        holder.tvItemFragmentTransactionDate.setText(DateFormat.longToDateString(transaction.getDate(), DATEFORMAT));

        String amount = FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE);
        String cur = transaction.getAccount_currency();
        String cur_lang = null;

        for (int i = 0; i < currency.length; i++) {
            if (cur.equals(currency[i])) {
                cur_lang = currency_language[i];
            }
        }


        if (amount.contains("-")) {
            holder.tvItemFragmentTransactionAmount.setText(amount + " " + cur_lang);
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_red));
        }
        else {
            holder.tvItemFragmentTransactionAmount.setText("+" + amount + " " + cur_lang);
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_green));
        }

        //Context catcont = holder.ivItemFragmentTransactionCategory.getContext();
        //Context curcont = holder.ivItemFragmentTransactionCurrency.getContext();

        String cat = transaction.getCategory();
        //String cur = transaction.getAccount_currency();



        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(cat)) {
                holder.ivItemFragmentTransactionCategory.setImageDrawable(icons.getDrawable(i));
            }
        }

        String type = dataSource.getAccountTypeByName(name);

        for (int i = 0; i < expense_type.length; i++) {
            if (expense_type[i].equals(type)) {
                holder.ivItemFragmentTransactionExpenseType.setImageDrawable(expense_type_icons.getDrawable(i));
            }
        }

        /*for (int i = 0; i < currency.length; i++) {
            if (currency[i].equals(cur)) {
                Glide.with(curcont)
                        .load(flags.getResourceId(i, 0))
                        .fitCenter()
                        .into(holder.ivItemFragmentTransactionCurrency);
            }
        }*/


    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        //private final View mView;
        private final TextView tvItemFragmentTransactionAmount;
        private final TextView tvItemFragmentTransactionAccountName;
        private final TextView tvItemFragmentTransactionDate;
        private final ImageView ivItemFragmentTransactionCategory;
        private final ImageView ivItemFragmentTransactionExpenseType;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvItemFragmentTransactionAmount = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAmount);
            tvItemFragmentTransactionAccountName = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAccountName);
            tvItemFragmentTransactionDate = (TextView) view.findViewById(R.id.tvItemFragmentTransactionDate);
            ivItemFragmentTransactionCategory = (ImageView) view.findViewById(R.id.ivItemFragmentTransactionCategory);
            ivItemFragmentTransactionExpenseType = (ImageView) view.findViewById(R.id.ivItemFragmentTransactionExpenseType);
        }
    }
}

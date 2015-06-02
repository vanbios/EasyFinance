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
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Transaction> transactionArrayList;

    final TypedArray icons;
    final String[] categories;

    final int PRECISE;
    final String FORMAT;
    final String DATEFORMAT;

    public TransactionRecyclerAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;

        icons = context.getResources().obtainTypedArray(R.array.icons);
        categories = context.getResources().getStringArray(R.array.cat_transaction_array);

        PRECISE = 100;
        FORMAT = "0.00";
        DATEFORMAT = "dd-MM-yyyy";
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

        Transaction transaction = getTransaction(position);



        holder.tvItemFragmentTransactionAccountName.setText(transaction.getAccount_name());
        holder.tvItemFragmentTransactionDate.setText(DateFormat.longToDateString(transaction.getDate(), DATEFORMAT));
        holder.tvItemFragmentTransactionAmount.setText(FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE)
                + " " + transaction.getAccount_currency());

        if (FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE).contains("-")) {
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_red));
        }
        else {
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_green));
        }



        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(transaction.getCategory())) {
                Glide.with(holder.ivItemFragmentTransactionCategory.getContext())
                        .load(icons.getResourceId(i, 0))
                        .fitCenter()
                        .into(holder.ivItemFragmentTransactionCategory);
                //holder.ivItemFragmentTransactionCategory.setImageDrawable(icons.getDrawable(i));
            }
        }
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        //private final View mView;
        private final TextView tvItemFragmentTransactionAmount;
        private final TextView tvItemFragmentTransactionAccountName;
        private final TextView tvItemFragmentTransactionDate;
        private final ImageView ivItemFragmentTransactionCategory;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvItemFragmentTransactionAmount = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAmount);
            tvItemFragmentTransactionAccountName = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAccountName);
            tvItemFragmentTransactionDate = (TextView) view.findViewById(R.id.tvItemFragmentTransactionDate);
            ivItemFragmentTransactionCategory = (ImageView) view.findViewById(R.id.ivItemFragmentTransactionCategory);
        }
    }
}

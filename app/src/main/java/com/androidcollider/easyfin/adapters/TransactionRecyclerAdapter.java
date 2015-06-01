package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Transaction> transactionArrayList;

    public TransactionRecyclerAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
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

        final int PRECISE = 100;
        final String FORMAT = "0.00";
        final String DATEFORMAT = "dd-MM-yyyy";

        holder.tvItemFragmentTransactionAccountName.setText(transaction.getAccount_name());
        holder.tvItemFragmentTransactionDate.setText(DateFormat.longToDateString(transaction.getDate(), DATEFORMAT));
        holder.tvItemFragmentTransactionCategory.setText(transaction.getCategory());
        holder.tvItemFragmentTransactionAmount.setText(FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE)
                + " " + transaction.getAccount_currency());

        if (FormatUtils.doubleFormatter(transaction.getAmount(), FORMAT, PRECISE).contains("-")) {
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_red));
        }
        else {
            holder.tvItemFragmentTransactionAmount.setTextColor(context.getResources().getColor(R.color.custom_green));
        }
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        //private final View mView;
        private final TextView tvItemFragmentTransactionAmount;
        private final TextView tvItemFragmentTransactionAccountName;
        private final TextView tvItemFragmentTransactionCategory;
        private final TextView tvItemFragmentTransactionDate;


        public ViewHolder(View view) {
            super(view);
            //mView = view;
            tvItemFragmentTransactionAmount = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAmount);
            tvItemFragmentTransactionAccountName = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAccountName);
            tvItemFragmentTransactionCategory = (TextView) view.findViewById(R.id.tvItemFragmentTransactionCategory);
            tvItemFragmentTransactionDate = (TextView) view.findViewById(R.id.tvItemFragmentTransactionDate);
        }
    }
}

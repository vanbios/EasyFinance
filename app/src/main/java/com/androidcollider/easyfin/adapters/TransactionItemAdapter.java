package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class TransactionItemAdapter  extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Transaction> transactionArrayList;

    public TransactionItemAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {return transactionArrayList.size();}

    @Override
    public Object getItem(int position) {return transactionArrayList.get(position);}

    @Override
    public long getItemId(int position) {return position;}


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        ViewHolder holder;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_fragment_transaction, parent, false);

            holder = new ViewHolder();
            holder.tvItemFragmentTransactionAmount = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAmount);
            holder.tvItemFragmentTransactionAccountName = (TextView) view.findViewById(R.id.tvItemFragmentTransactionAccountName);
            holder.tvItemFragmentTransactionCategory = (TextView) view.findViewById(R.id.tvItemFragmentTransactionCategory);
            holder.tvItemFragmentTransactionDate = (TextView) view.findViewById(R.id.tvItemFragmentTransactionDate);

            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

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

        return view;
    }


    Transaction getTransaction(int position) {
        return (Transaction) getItem(position);
    }


    static class ViewHolder {
        private TextView tvItemFragmentTransactionAmount;
        private TextView tvItemFragmentTransactionAccountName;
        private TextView tvItemFragmentTransactionCategory;
        private TextView tvItemFragmentTransactionDate;
    }
}

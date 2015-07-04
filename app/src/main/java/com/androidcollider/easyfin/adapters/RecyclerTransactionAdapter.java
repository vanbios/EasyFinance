package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.ViewHolder> {

    private long pos;

    private Context context;
    private ArrayList<Transaction> transactionArrayList;

    private final TypedArray catIconsArray;
    private final TypedArray typeIconsArray;

    private final String[] catArray;
    private final String[] curArray;

    private final String[] curLangArray;
    private final String[] typeArray;



    public RecyclerTransactionAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;

        catIconsArray = context.getResources().obtainTypedArray(R.array.transaction_categories_icons);
        catArray = context.getResources().getStringArray(R.array.transaction_category_array);
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
        typeArray = context.getResources().getStringArray(R.array.account_type_array);
        typeIconsArray = context.getResources().obtainTypedArray(R.array.account_type_icons);
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
        final String FORMAT = "###,##0.00";
        final String DATEFORMAT = "dd.MM.yyyy";

        Transaction transaction = getTransaction(position);

        holder.tvTransAccountName.setText(transaction.getAccountName());
        holder.tvTransDate.setText(DateFormat.longToDateString(transaction.getDate(), DATEFORMAT));

        String amount = FormatUtils.doubleToStringFormatter(transaction.getAmount(), FORMAT, PRECISE);

        String cur = transaction.getCurrency();

        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
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

        for (int i = 0; i < catArray.length; i++) {
            if (catArray[i].equals(cat)) {
                holder.ivTransCategory.setImageDrawable(catIconsArray.getDrawable(i));
            }
        }

        String type = transaction.getAccountType();

        for (int i = 0; i < typeArray.length; i++) {
            if (typeArray[i].equals(type)) {
                holder.ivTransAccountType.setImageDrawable(typeIconsArray.getDrawable(i));
            }
        }


        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(position);
                return false;
            }
        });
    }


    public long getPosition() {
        return pos;
    }

    public void setPosition(long pos) {
        this.pos = pos;
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final TextView tvTransAmount;
        private final TextView tvTransAccountName;
        private final TextView tvTransDate;
        private final ImageView ivTransCategory;
        private final ImageView ivTransAccountType;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvTransAmount = (TextView) view.findViewById(R.id.tvItemTransactionAmount);
            tvTransAccountName = (TextView) view.findViewById(R.id.tvItemTransactionAccountName);
            tvTransDate = (TextView) view.findViewById(R.id.tvItemTransactionDate);
            ivTransCategory = (ImageView) view.findViewById(R.id.ivItemTransactionCategory);
            ivTransAccountType = (ImageView) view.findViewById(R.id.ivItemTransactionAccountType);

            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction, Menu.NONE, R.string.delete);
        }
    }
}

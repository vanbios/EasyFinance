package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.ChangeExpenseActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;

public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Account> accountArrayList;

    final TypedArray expense_type_icons;
    final String[] expense_type;

    final String[] currency;
    final String[] currency_language;

    public ExpenseRecyclerAdapter(Context context, ArrayList<Account> accountArrayList) {
        this.context = context;
        this.accountArrayList = accountArrayList;

        expense_type_icons = context.getResources().obtainTypedArray(R.array.expense_type_48);
        expense_type = context.getResources().getStringArray(R.array.expense_type_array);

        currency = context.getResources().getStringArray(R.array.expense_currency_array);
        currency_language = context.getResources().getStringArray(R.array.expense_currency_array_language);
    }

    @Override
    public int getItemCount() {return accountArrayList.size();}

    @Override
    public long getItemId(int position) {return position;}

    public Account getAccount(int position) {return accountArrayList.get(position);}


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Account account = getAccount(position);

        final int PRECISE = 100;
        final String FORMAT = "0.00";

        String cur = account.getCurrency();
        String cur_lang = null;

        for (int i = 0; i < currency.length; i++) {
            if (cur.equals(currency[i])) {
                cur_lang = currency_language[i];
            }
        }

        holder.tvItemFragmentExpenseName.setText(account.getName());
        holder.tvItemFragmentExpenseAmount.setText(FormatUtils.doubleFormatter(account.getAmount(), FORMAT, PRECISE) +
                " " + cur_lang);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, ChangeExpenseActivity.class);
                Account account = getAccount(position);

                intent.putExtra("name", account.getName());
                intent.putExtra("type", account.getType());
                intent.putExtra("amount", account.getAmount());
                intent.putExtra("currency", account.getCurrency());
                context.startActivity(intent);
                return true;
            }
        });

        String type = account.getType();

        for (int i = 0; i < expense_type.length; i++) {
            if (expense_type[i].equals(type)) {
                holder.ivItemFragmentExpenseType.setImageDrawable(expense_type_icons.getDrawable(i));
            }
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView ivItemFragmentExpenseType;
        public final TextView tvItemFragmentExpenseName;
        public final TextView tvItemFragmentExpenseAmount;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivItemFragmentExpenseType = (ImageView) view.findViewById(R.id.ivItemFragmentExpenseType);
            tvItemFragmentExpenseName = (TextView) view.findViewById(R.id.tvItemFragmentExpenseName);
            tvItemFragmentExpenseAmount = (TextView) view.findViewById(R.id.tvItemFragmentExpenseAmount);
        }
    }
}

package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.ChangeExpenseActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;

public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Account> accountArrayList;

    public ExpenseRecyclerAdapter(Context context, ArrayList<Account> accountArrayList) {
        this.context = context;
        this.accountArrayList = accountArrayList;
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

        holder.tvItemFragmentExpenseName.setText(account.getName());
        holder.tvItemFragmentExpenseType.setText(account.getType());
        holder.tvItemFragmentExpenseAmount.setText(FormatUtils.doubleFormatter(account.getAmount(), FORMAT, PRECISE) +
                " " + account.getCurrency());

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
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvItemFragmentExpenseName;
        public final TextView tvItemFragmentExpenseType;
        public final TextView tvItemFragmentExpenseAmount;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvItemFragmentExpenseName = (TextView) view.findViewById(R.id.tvItemFragmentExpenseName);
            tvItemFragmentExpenseType = (TextView) view.findViewById(R.id.tvItemFragmentExpenseType);
            tvItemFragmentExpenseAmount = (TextView) view.findViewById(R.id.tvItemFragmentExpenseAmount);
        }
    }
}

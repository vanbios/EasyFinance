package com.androidcollider.easyfin.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class RecyclerDebtAdapter extends RecyclerView.Adapter<RecyclerDebtAdapter.ViewHolder> {

    private ArrayList<Debt> debtList;


    public RecyclerDebtAdapter(ArrayList<Debt> debtList) {

        this.debtList = debtList;
    }



    @Override
    public int getItemCount() {return debtList.size();}

    @Override
    public long getItemId(int position) {return position;}

    public Debt getDebt(int position) {return debtList.get(position);}



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_debt, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Debt debt = getDebt(position);

        final int PRECISE = 100;
        final String FORMAT = "0.00";
        final String DATEFORMAT = "dd.MM.yyyy";

        holder.tvDebtName.setText(debt.getName());
        holder.tvAmount.setText(FormatUtils.doubleFormatter(debt.getAmount(), FORMAT, PRECISE));
        holder.tvAccountName.setText(debt.getAccountName());
        holder.tvDate.setText(DateFormat.longToDateString(debt.getDate(), DATEFORMAT));
    }














    static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvDebtName;
        public final TextView tvAmount;
        public final TextView tvAccountName;
        public final TextView tvDate;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDebtName = (TextView) view.findViewById(R.id.tvItemDebtName);
            tvAmount = (TextView) view.findViewById(R.id.tvItemDebtAmount);
            tvAccountName = (TextView) view.findViewById(R.id.tvItemDebtAccountName);
            tvDate = (TextView) view.findViewById(R.id.tvItemDebtDate);
        }
    }
}

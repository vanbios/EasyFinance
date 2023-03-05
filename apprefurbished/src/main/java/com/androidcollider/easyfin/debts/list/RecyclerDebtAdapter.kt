package com.androidcollider.easyfin.debts.list;

import android.graphics.LightingColorFilter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class RecyclerDebtAdapter extends RecyclerView.Adapter<RecyclerDebtAdapter.ViewHolder> {

    private int currentId;
    private final List<DebtViewModel> debtList;


    RecyclerDebtAdapter() {
        this.debtList = new ArrayList<>();
    }

    void setItems(List<DebtViewModel> items) {
        debtList.clear();
        debtList.addAll(items);
        notifyDataSetChanged();
    }

    void deleteItem(int position) {
        debtList.remove(position);
        notifyItemRemoved(position);
    }

    int getCurrentId() {
        return currentId;
    }

    private void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private DebtViewModel getDebt(int position) {
        return debtList.get(position);
    }

    int getPositionById(int id) {
        for (int i = 0; i < debtList.size(); i++) {
            if (debtList.get(i).getId() == id) return i;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_debt, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DebtViewModel debt = getDebt(position);
        holder.tvDebtName.setText(debt.getName());
        holder.tvAmount.setText(debt.getAmount());
        holder.tvAccountName.setText(debt.getAccountName());
        holder.tvDate.setText(debt.getDate());
        holder.prgBar.setProgress(debt.getProgress());
        holder.tvProgress.setText(debt.getProgressPercents());

        int color = debt.getColorRes();
        holder.tvAmount.setTextColor(color);
        holder.prgBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, color));
        holder.tvProgress.setTextColor(color);

        holder.mView.setOnLongClickListener(view -> {
            setCurrentId(debt.getId());
            return false;
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final TextView tvDebtName;
        private final TextView tvAmount;
        private final TextView tvAccountName;
        private final TextView tvDate;
        private final ProgressBar prgBar;
        private final TextView tvProgress;


        ViewHolder(View view) {
            super(view);
            mView = view;
            tvDebtName = view.findViewById(R.id.tvItemDebtName);
            tvAmount = view.findViewById(R.id.tvItemDebtAmount);
            tvAccountName = view.findViewById(R.id.tvItemDebtAccountName);
            tvDate = view.findViewById(R.id.tvItemDebtDate);
            prgBar = view.findViewById(R.id.progressBarItemDebt);
            tvProgress = view.findViewById(R.id.tvItemDebtProgress);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_pay_all_debt, 1, R.string.pay_all_debt);
            menu.add(Menu.NONE, R.id.ctx_menu_pay_part_debt, 2, R.string.pay_part_debt);
            menu.add(Menu.NONE, R.id.ctx_menu_take_more_debt, 3, R.string.take_more_debt);
            menu.add(Menu.NONE, R.id.ctx_menu_edit_debt, 4, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_debt, 5, R.string.delete);
        }
    }
}
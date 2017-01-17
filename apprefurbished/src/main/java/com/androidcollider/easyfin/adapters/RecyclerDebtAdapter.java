package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.models.Debt;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class RecyclerDebtAdapter extends RecyclerView.Adapter<RecyclerDebtAdapter.ViewHolder> {

    @Getter
    @Setter
    private long position;
    private Context context;
    private List<Debt> debtList;
    private final String[] curArray, curLangArray;

    private DateFormatManager dateFormatManager;
    private NumberFormatManager numberFormatManager;


    public RecyclerDebtAdapter(Context context, List<Debt> debtList,
                               DateFormatManager dateFormatManager, NumberFormatManager numberFormatManager) {
        this.context = context;
        this.debtList = debtList;
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
        this.dateFormatManager = dateFormatManager;
        this.numberFormatManager = numberFormatManager;
    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Debt getDebt(int position) {
        return debtList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_debt, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Debt debt = getDebt(position);
        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";
        final String DATEFORMAT = "dd.MM.yyyy";

        holder.tvDebtName.setText(debt.getName());

        String cur = debt.getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        double amountCurrent = debt.getAmountCurrent();
        double amountAll = debt.getAmountAll();

        holder.tvAmount.setText(String.format("%1$s %2$s",
                numberFormatManager.doubleToStringFormatter(amountCurrent, FORMAT, PRECISE), curLang));
        holder.tvAccountName.setText(debt.getAccountName());
        holder.tvDate.setText(dateFormatManager.longToDateString(debt.getDate(), DATEFORMAT));

        int progress = (int) (amountCurrent / amountAll * 100);
        holder.prgBar.setProgress(progress);
        holder.tvProgress.setText(String.format("%s%%", progress));

        int color = ContextCompat.getColor(context,
                debt.getType() == 1 ? R.color.custom_red : R.color.custom_green);
        holder.tvAmount.setTextColor(color);
        holder.prgBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, color));
        holder.tvProgress.setTextColor(color);

        /*switch (debt.getType()) {
            case 0:
                int green = ContextCompat.getColor(context, R.color.custom_green);
                holder.tvAmount.setTextColor(green);
                holder.prgBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, green));
                holder.tvProgress.setTextColor(green);
                break;
            case 1:
                int red = ContextCompat.getColor(context, R.color.custom_red);
                holder.tvAmount.setTextColor(red);
                holder.prgBar.getProgressDrawable().setColorFilter(new LightingColorFilter(0xFF000000, red));
                holder.tvProgress.setTextColor(red);
                break;
        }*/

        holder.mView.setOnLongClickListener(view -> {
            setPosition(position);
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
            tvDebtName = findById(view, R.id.tvItemDebtName);
            tvAmount = findById(view, R.id.tvItemDebtAmount);
            tvAccountName = findById(view, R.id.tvItemDebtAccountName);
            tvDate = findById(view, R.id.tvItemDebtDate);
            prgBar = findById(view, R.id.progressBarItemDebt);
            tvProgress = findById(view, R.id.tvItemDebtProgress);
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
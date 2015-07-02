package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.utils.DateFormat;
import com.androidcollider.easyfin.utils.FormatUtils;

import java.util.ArrayList;


public class RecyclerDebtAdapter extends RecyclerView.Adapter<RecyclerDebtAdapter.ViewHolder> {

    private long pos;

    private Context context;
    private ArrayList<Debt> debtList;

    private final String[] curArray;
    private final String[] curLangArray;


    public RecyclerDebtAdapter(Context context, ArrayList<Debt> debtList) {
        this.context = context;
        this.debtList = debtList;

        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
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
        final String FORMAT = "###,##0.00";
        final String DATEFORMAT = "dd.MM.yyyy";

        holder.tvDebtName.setText(debt.getName());

        String cur = debt.getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
            }
        }

        holder.tvAmount.setText(FormatUtils.doubleFormatter(debt.getAmount(), FORMAT, PRECISE)
        + " " + curLang);
        holder.tvAccountName.setText(debt.getAccountName());
        holder.tvDate.setText(DateFormat.longToDateString(debt.getDate(), DATEFORMAT));

        switch (debt.getType()) {
            case 0: {
                holder.tvAmount.setTextColor(context.getResources().getColor(R.color.custom_green));
                break;
            }
            case 1: {
                holder.tvAmount.setTextColor(context.getResources().getColor(R.color.custom_red));
                break;
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

            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_pay_all_debt, 1, R.string.pay_all_debt);
            menu.add(Menu.NONE, R.id.ctx_menu_pay_part_debt, 2, R.string.pay_part_debt);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_debt, 3, R.string.delete);
        }
    }
}

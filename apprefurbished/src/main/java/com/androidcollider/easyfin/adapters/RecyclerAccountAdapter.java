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
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;

import java.util.List;

public class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.ViewHolder> {

    private long pos;
    private List<Account> accountsList;
    private final TypedArray typeIconsArray;
    private final String[] curArray, curLangArray;


    public RecyclerAccountAdapter(Context context, List<Account> accountsList) {
        this.accountsList = accountsList;
        typeIconsArray = context.getResources().obtainTypedArray(R.array.account_type_icons);
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Account getAccount(int position) {
        return accountsList.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Account account = getAccount(position);
        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";

        String cur = account.getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        holder.tvAccountName.setText(account.getName());
        holder.tvAccountAmount.setText(String.format("%1$s %2$s",
                DoubleFormatUtils.doubleToStringFormatter(account.getAmount(), FORMAT, PRECISE), curLang));

        holder.ivAccountType.setImageDrawable(typeIconsArray.getDrawable(account.getType()));

        holder.mView.setOnLongClickListener(view -> {
            setPosition(position);
            return false;
        });
    }

    public long getPosition() {
        return pos;
    }

    public void setPosition(long pos) {
        this.pos = pos;
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final View mView;
        final ImageView ivAccountType;
        final TextView tvAccountName;
        final TextView tvAccountAmount;


        ViewHolder(View view) {
            super(view);
            mView = view;
            ivAccountType = (ImageView) view.findViewById(R.id.ivItemFragmentAccountType);
            tvAccountName = (TextView) view.findViewById(R.id.tvItemFragmentAccountName);
            tvAccountAmount = (TextView) view.findViewById(R.id.tvItemFragmentAccountAmount);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_account, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_account, 2, R.string.delete);
        }
    }

}

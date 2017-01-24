package com.androidcollider.easyfin.accounts.list;

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
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.ViewHolder> {

    @Getter
    @Setter
    private long position;
    private List<Account> accountList;
    private final TypedArray typeIconsArray;
    private final String[] curArray, curLangArray;

    private NumberFormatManager numberFormatManager;


    public RecyclerAccountAdapter(NumberFormatManager numberFormatManager, ResourcesManager resourcesManager) {
        this.accountList = new ArrayList<>();
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE);
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
        this.numberFormatManager = numberFormatManager;
    }

    public void addItems(List<Account> items) {
        accountList.clear();
        accountList.addAll(items);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        accountList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Account getAccount(int position) {
        return accountList.get(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_account, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Account account = getAccount(position);
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (account.getCurrency().equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        holder.tvAccountName.setText(account.getName());
        holder.tvAccountAmount.setText(
                String.format("%1$s %2$s",
                        numberFormatManager.doubleToStringFormatter(
                                account.getAmount(),
                                NumberFormatManager.FORMAT_1,
                                NumberFormatManager.PRECISE_1
                        ),
                        curLang));

        holder.ivAccountType.setImageDrawable(typeIconsArray.getDrawable(account.getType()));

        holder.mView.setOnLongClickListener(view -> {
            setPosition(position);
            return false;
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final ImageView ivAccountType;
        private final TextView tvAccountName;
        private final TextView tvAccountAmount;


        ViewHolder(View view) {
            super(view);
            mView = view;
            ivAccountType = findById(view, R.id.ivItemFragmentAccountType);
            tvAccountName = findById(view, R.id.tvItemFragmentAccountName);
            tvAccountAmount = findById(view, R.id.tvItemFragmentAccountAmount);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_account, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_account, 2, R.string.delete);
        }
    }
}
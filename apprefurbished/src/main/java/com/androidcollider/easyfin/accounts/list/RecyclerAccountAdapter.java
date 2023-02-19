package com.androidcollider.easyfin.accounts.list;

import android.content.res.TypedArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class RecyclerAccountAdapter extends RecyclerView.Adapter<RecyclerAccountAdapter.ViewHolder> {

    private int currentId;
    private final List<AccountViewModel> accountList;
    private final TypedArray typeIconsArray;


    RecyclerAccountAdapter(ResourcesManager resourcesManager) {
        this.accountList = new ArrayList<>();
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE);
    }

    void setItems(List<AccountViewModel> items) {
        accountList.clear();
        accountList.addAll(items);
        notifyDataSetChanged();
    }

    void deleteItem(int position) {
        accountList.remove(position);
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
        return accountList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private AccountViewModel getAccount(int position) {
        return accountList.get(position);
    }

    int getPositionById(int id) {
        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getId() == id) return i;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_account, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AccountViewModel account = getAccount(position);

        holder.tvAccountName.setText(account.getName());
        holder.tvAccountAmount.setText(account.getAmount());
        holder.ivAccountType.setImageDrawable(typeIconsArray.getDrawable(account.getType()));

        holder.mView.setOnLongClickListener(view -> {
            setCurrentId(account.getId());
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
            ivAccountType = view.findViewById(R.id.ivItemFragmentAccountType);
            tvAccountName = view.findViewById(R.id.tvItemFragmentAccountName);
            tvAccountAmount = view.findViewById(R.id.tvItemFragmentAccountAmount);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_account, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_account, 2, R.string.delete);
        }
    }
}
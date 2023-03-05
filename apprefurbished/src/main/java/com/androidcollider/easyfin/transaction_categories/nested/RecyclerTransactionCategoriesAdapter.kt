package com.androidcollider.easyfin.transaction_categories.nested;

import android.content.res.TypedArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager;
import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class RecyclerTransactionCategoriesAdapter extends RecyclerView.Adapter<RecyclerTransactionCategoriesAdapter.ViewHolderItem> {

    private int currentId;
    private final List<TransactionCategory> transactionList;
    private TypedArray catIconsArray;
    private final LetterTileManager letterTileManager;


    RecyclerTransactionCategoriesAdapter(LetterTileManager letterTileManager) {
        this.transactionList = new ArrayList<>();
        this.letterTileManager = letterTileManager;
    }

    void setItems(List<TransactionCategory> items, TypedArray typedArray) {
        transactionList.clear();
        transactionList.addAll(items);
        catIconsArray = typedArray;
        notifyDataSetChanged();
    }

    void deleteItem(int position) {
        transactionList.remove(position);
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
        return transactionList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private TransactionCategory getTransactionCategory(int position) {
        return transactionList.get(position);
    }

    int getPositionById(int id) {
        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).getId() == id) return i;
        }
        return 0;
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolderItem holder, final int position) {
        TransactionCategory transactionCategory = getTransactionCategory(position);
        int categoryId = transactionCategory.getId();
        String categoryName = transactionCategory.getName();

        holder.tvCategory.setText(categoryName);

        if (categoryId < catIconsArray.length()) {
            holder.ivCategory.setImageDrawable(catIconsArray.getDrawable(categoryId));
            holder.mView.setOnCreateContextMenuListener(null);
        } else {
            holder.ivCategory.setImageBitmap(letterTileManager.getLetterTile(categoryName));
        }

        holder.mView.setOnLongClickListener(view -> {
            setCurrentId(transactionCategory.getId());
            return false;
        });
    }


    static class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final TextView tvCategory;
        private final ImageView ivCategory;

        ViewHolderItem(View view) {
            super(view);
            mView = view;
            tvCategory = view.findViewById(R.id.tv_category_name);
            ivCategory = view.findViewById(R.id.iv_category_icon);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_transaction_category, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction_category, 2, R.string.delete);
        }
    }
}
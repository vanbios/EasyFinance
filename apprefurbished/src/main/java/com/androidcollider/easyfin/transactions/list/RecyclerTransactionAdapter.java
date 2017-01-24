package com.androidcollider.easyfin.transactions.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.MainViewHolder> {

    @Getter
    @Setter
    private long position;
    private Context context;
    private List<Transaction> transactionList;
    private final TypedArray catExpenseIconsArray, catIncomeIconsArray, typeIconsArray;
    private final String[] curArray, curLangArray;
    private final int CONTENT_TYPE = 1, BUTTON_TYPE = 2;
    private static int itemCount, maxCount = 30;
    private static boolean showButton;

    private DateFormatManager dateFormatManager;
    private NumberFormatManager numberFormatManager;


    public RecyclerTransactionAdapter(Context context,
                                      DateFormatManager dateFormatManager,
                                      NumberFormatManager numberFormatManager,
                                      ResourcesManager resourcesManager) {
        this.context = context;
        this.transactionList = new ArrayList<>();
        catExpenseIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE);
        catIncomeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME);
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE);
        this.dateFormatManager = dateFormatManager;
        this.numberFormatManager = numberFormatManager;
    }

    public void addItems(List<Transaction> items) {
        transactionList.clear();
        transactionList.addAll(items);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        transactionList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        return showButton && position == itemCount ? BUTTON_TYPE : CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        int arraySize = transactionList.size();
        showButton = arraySize > maxCount;
        itemCount = showButton ? maxCount : arraySize;
        return showButton ? itemCount + 1 : itemCount;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Transaction getTransaction(int position) {
        return transactionList.get(position);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case CONTENT_TYPE:
                return new ViewHolderItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_frg_transaction, parent, false));
            case BUTTON_TYPE:
                return new ViewHolderButton(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_button_show_more, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        if (getItemViewType(position) == CONTENT_TYPE) {
            ViewHolderItem holderItem = (ViewHolderItem) holder;

            final int PRECISE = 100;
            final String FORMAT = "###,##0.00";

            Transaction transaction = getTransaction(position);

            holderItem.tvTransAccountName.setText(transaction.getAccountName());
            holderItem.tvTransDate.setText(dateFormatManager.longToDateString(transaction.getDate(), DateFormatManager.DAY_MONTH_YEAR_DOTS));

            String amount = numberFormatManager.doubleToStringFormatter(
                    transaction.getAmount(),
                    NumberFormatManager.FORMAT_1,
                    NumberFormatManager.PRECISE_1
            );
            String curLang = null;

            for (int i = 0; i < curArray.length; i++) {
                if (transaction.getCurrency().equals(curArray[i])) {
                    curLang = curLangArray[i];
                    break;
                }
            }

            if (amount.contains("-")) {
                holderItem.tvTransAmount.setText(String.format("- %1$s %2$s", amount.substring(1), curLang));
                holderItem.tvTransAmount.setTextColor(ContextCompat.getColor(context, R.color.custom_red));
                holderItem.ivTransCategory.setImageDrawable(catExpenseIconsArray.getDrawable(transaction.getCategory()));
            } else {
                holderItem.tvTransAmount.setText(String.format("+ %1$s %2$s", amount, curLang));
                holderItem.tvTransAmount.setTextColor(ContextCompat.getColor(context, R.color.custom_green));
                holderItem.ivTransCategory.setImageDrawable(catIncomeIconsArray.getDrawable(transaction.getCategory()));
            }

            holderItem.ivTransAccountType.setImageDrawable(typeIconsArray.getDrawable(transaction.getAccountType()));
            holderItem.mView.setOnLongClickListener(view -> {
                setPosition(position);
                return false;
            });

        } else if (getItemViewType(position) == BUTTON_TYPE) {
            ViewHolderButton holderButton = (ViewHolderButton) holder;
            holderButton.tvShowMore.setOnClickListener(v -> {
                maxCount += 30;
                notifyDataSetChanged();
            });
        }
    }


    static class MainViewHolder extends RecyclerView.ViewHolder {
        MainViewHolder(View view) {
            super(view);
        }
    }

    private static class ViewHolderItem extends MainViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final TextView tvTransAmount;
        private final TextView tvTransAccountName;
        private final TextView tvTransDate;
        private final ImageView ivTransCategory;
        private final ImageView ivTransAccountType;

        ViewHolderItem(View view) {
            super(view);
            mView = view;
            tvTransAmount = findById(view, R.id.tvItemTransactionAmount);
            tvTransAccountName = findById(view, R.id.tvItemTransactionAccountName);
            tvTransDate = findById(view, R.id.tvItemTransactionDate);
            ivTransCategory = findById(view, R.id.ivItemTransactionCategory);
            ivTransAccountType = findById(view, R.id.ivItemTransactionAccountType);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_transaction, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction, 2, R.string.delete);
        }
    }


    private static class ViewHolderButton extends MainViewHolder {
        private final TextView tvShowMore;

        ViewHolderButton(View view) {
            super(view);
            tvShowMore = (TextView) view.findViewById(R.id.tvItemShowMore);
        }
    }
}
package com.androidcollider.easyfin.adapters;

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
import com.androidcollider.easyfin.objects.Transaction;
import com.androidcollider.easyfin.utils.DateFormatUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;

import java.util.ArrayList;

public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.MainViewHolder> {

    private long pos;
    private Context context;
    private ArrayList<Transaction> transactionArrayList;
    private final TypedArray catExpenseIconsArray, catIncomeIconsArray, typeIconsArray;
    private final String[] curArray, curLangArray;
    public final int CONTENT_TYPE = 1, BUTTON_TYPE = 2;
    private static int itemCount, maxCount = 30;
    private static boolean showButton;


    public RecyclerTransactionAdapter(Context context, ArrayList<Transaction> transactionArrayList) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
        catExpenseIconsArray = context.getResources().obtainTypedArray(R.array.transaction_category_expense_icons);
        catIncomeIconsArray = context.getResources().obtainTypedArray(R.array.transaction_category_income_icons);
        curArray = context.getResources().getStringArray(R.array.account_currency_array);
        curLangArray = context.getResources().getStringArray(R.array.account_currency_array_language);
        typeIconsArray = context.getResources().obtainTypedArray(R.array.account_type_icons);
    }

    @Override
    public int getItemViewType(int position) {
        return showButton && position == itemCount ? BUTTON_TYPE : CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        int arraySize = transactionArrayList.size();
        showButton = arraySize > maxCount;
        itemCount = showButton ? maxCount : arraySize;
        return showButton ? itemCount + 1 : itemCount;
    }

    @Override
    public long getItemId(int position) {return position;}

    Transaction getTransaction(int position) {
        return transactionArrayList.get(position);
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
        if (holder instanceof ViewHolderItem) {
            ViewHolderItem holderItem = (ViewHolderItem) holder;

            final int PRECISE = 100;
            final String FORMAT = "###,##0.00";
            final String DATEFORMAT = "dd.MM.yyyy";

            Transaction transaction = getTransaction(position);

            holderItem.tvTransAccountName.setText(transaction.getAccountName());
            holderItem.tvTransDate.setText(DateFormatUtils.longToDateString(transaction.getDate(), DATEFORMAT));

            String amount = DoubleFormatUtils.doubleToStringFormatter(transaction.getAmount(), FORMAT, PRECISE);
            String cur = transaction.getCurrency();
            String curLang = null;

            for (int i = 0; i < curArray.length; i++) {
                if (cur.equals(curArray[i])) {
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
            holderItem.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setPosition(position);
                    return false;
                }
            });
        }

        else if (holder instanceof ViewHolderButton){
            ViewHolderButton holderButton = (ViewHolderButton) holder;
            holderButton.tvShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    maxCount += 30;
                    notifyDataSetChanged();
                }
            });
        }
    }

    public long getPosition() {
        return pos;
    }

    public void setPosition(long pos) {
        this.pos = pos;
    }


    static class MainViewHolder extends RecyclerView.ViewHolder {
        public MainViewHolder (View view) {
            super (view);
        }
    }

    static class ViewHolderItem extends MainViewHolder implements View.OnCreateContextMenuListener {
        private final View mView;
        private final TextView tvTransAmount;
        private final TextView tvTransAccountName;
        private final TextView tvTransDate;
        private final ImageView ivTransCategory;
        private final ImageView ivTransAccountType;

        public ViewHolderItem(View view) {
            super(view);
            mView = view;
            tvTransAmount = (TextView) view.findViewById(R.id.tvItemTransactionAmount);
            tvTransAccountName = (TextView) view.findViewById(R.id.tvItemTransactionAccountName);
            tvTransDate = (TextView) view.findViewById(R.id.tvItemTransactionDate);
            ivTransCategory = (ImageView) view.findViewById(R.id.ivItemTransactionCategory);
            ivTransAccountType = (ImageView) view.findViewById(R.id.ivItemTransactionAccountType);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.ctx_menu_edit_transaction, 1, R.string.edit);
            menu.add(Menu.NONE, R.id.ctx_menu_delete_transaction, 2, R.string.delete);
        }
    }


    static class ViewHolderButton extends MainViewHolder {
        private final TextView tvShowMore;

        public ViewHolderButton(View view) {
            super(view);
            tvShowMore = (TextView) view.findViewById(R.id.tvItemShowMore);
        }
    }

}

package com.androidcollider.easyfin.transactions.add_edit.income_expense;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager;
import com.androidcollider.easyfin.common.models.TransactionCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class TransactionCategoryAdapter extends ArrayAdapter<TransactionCategory> {

    private final TypedArray iconsArray;
    private final List<TransactionCategory> transactionCategoryList;
    private final int headLayout, headTvId, headIvId, dropLayout, dropTvId, dropIvId;
    private final LayoutInflater inflater;
    private final LetterTileManager letterTileManager;

    TransactionCategoryAdapter(Context context,
                               int headLayout, int headTvId, int headIvId,
                               int dropLayout, int dropTvId, int dropIvId,
                               List<TransactionCategory> transactionCategoryList,
                               TypedArray icons,
                               LetterTileManager letterTileManager) {
        super(context, headLayout, transactionCategoryList);
        this.transactionCategoryList = new ArrayList<>();
        this.transactionCategoryList.addAll(transactionCategoryList);
        iconsArray = icons;
        this.headLayout = headLayout;
        this.headTvId = headTvId;
        this.headIvId = headIvId;
        this.dropLayout = dropLayout;
        this.dropTvId = dropTvId;
        this.dropIvId = dropIvId;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.letterTileManager = letterTileManager;
    }

    @Override
    public View getDropDownView(int position, View view, @NonNull ViewGroup parent) {
        return getCustomDropView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent) {
        return getCustomHeadView(pos, parent);
    }

    private View getCustomDropView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(dropLayout, parent, false);
        TextView text = dropSpinner.findViewById(dropTvId);
        String name = transactionCategoryList.get(position).getName();
        text.setText(name);
        ImageView icon = dropSpinner.findViewById(dropIvId);
        if (position < iconsArray.length()) {
            icon.setImageResource(iconsArray.getResourceId(position, 0));
        } else {
            icon.setImageBitmap(letterTileManager.getLetterTile(name));
        }
        return dropSpinner;
    }

    private View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(headLayout, parent, false);
        TextView headText = headSpinner.findViewById(headTvId);
        String name = transactionCategoryList.get(position).getName();
        headText.setText(name);
        ImageView icon = headSpinner.findViewById(headIvId);
        if (position < iconsArray.length()) {
            icon.setImageResource(iconsArray.getResourceId(position, 0));
        } else {
            icon.setImageBitmap(letterTileManager.getLetterTile(name));
        }
        return headSpinner;
    }
}
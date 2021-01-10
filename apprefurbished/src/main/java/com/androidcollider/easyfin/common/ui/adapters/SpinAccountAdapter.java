package com.androidcollider.easyfin.common.ui.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

/**
 * @author Ihor Bilous
 */

abstract class SpinAccountAdapter extends ArrayAdapter<SpinAccountViewModel> {

    private final TypedArray typeIconsArray;
    private final List<SpinAccountViewModel> accountList;
    private final String[] curArray, curLangArray;
    private LayoutInflater inflater;


    SpinAccountAdapter(Context context,
                       int headLayout,
                       List<SpinAccountViewModel> accountL,
                       ResourcesManager resourcesManager) {
        super(context, headLayout, accountL);
        accountList = accountL;
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE);
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, @NonNull ViewGroup parent) {
        return getCustomDropView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        return getCustomHeadView(position, parent);
    }

    private View getCustomDropView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(R.layout.spin_account_for_trans_dropdown, parent, false);
        SpinAccountViewModel account = getItem(position);
        if (account != null) {
            TextView name = dropSpinner.findViewById(R.id.tvSpinDropdownAccountName);
            name.setText(account.getName());
            ImageView icon = dropSpinner.findViewById(R.id.ivSpinDropdownAccountType);
            icon.setImageResource(typeIconsArray.getResourceId(account.getType(), 0));
            TextView amountText = dropSpinner.findViewById(R.id.tvSpinDropdownAccountAmount);
            amountText.setText(account.getAmountString());
        }

        return dropSpinner;
    }

    public TypedArray getTypeIconsArray() {
        return typeIconsArray;
    }

    public List<SpinAccountViewModel> getAccountList() {
        return accountList;
    }

    public String[] getCurArray() {
        return curArray;
    }

    public String[] getCurLangArray() {
        return curLangArray;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public abstract View getCustomHeadView(int position, ViewGroup parent);

    public SpinAccountViewModel getItem(int position) {
        return accountList.get(position);
    }
}
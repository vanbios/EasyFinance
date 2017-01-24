package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.models.Account;

import java.util.List;

import lombok.Getter;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

@Getter
abstract class SpinAccountAdapter extends ArrayAdapter<Account> {

    private final TypedArray typeIconsArray;
    private final List<Account> accountList;
    private final String[] curArray, curLangArray;
    private LayoutInflater inflater;

    private NumberFormatManager numberFormatManager;


    SpinAccountAdapter(Context context,
                       int headLayout,
                       List<Account> accountL,
                       NumberFormatManager numberFormatManager,
                       ResourcesManager resourcesManager) {
        super(context, headLayout, accountL);
        accountList = accountL;
        typeIconsArray = resourcesManager.getIconArray(ResourcesManager.ICON_ACCOUNT_TYPE);
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.numberFormatManager = numberFormatManager;
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
        TextView name = findById(dropSpinner, R.id.tvSpinDropdownAccountName);
        name.setText(accountList.get(position).getName());
        ImageView icon = findById(dropSpinner, R.id.ivSpinDropdownAccountType);
        icon.setImageResource(typeIconsArray.getResourceId(accountList.get(position).getType(), 0));
        TextView amountText = findById(dropSpinner, R.id.tvSpinDropdownAccountAmount);

        String amount = numberFormatManager.doubleToStringFormatter(
                accountList.get(position).getAmount(),
                NumberFormatManager.FORMAT_2,
                NumberFormatManager.PRECISE_1
        );
        String cur = accountList.get(position).getCurrency();
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (cur.equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        amountText.setText(String.format("%1$s %2$s", amount, curLang));

        return dropSpinner;
    }

    public abstract View getCustomHeadView(int position, ViewGroup parent);

    public Account getItem(int position) {
        return accountList.get(position);
    }
}
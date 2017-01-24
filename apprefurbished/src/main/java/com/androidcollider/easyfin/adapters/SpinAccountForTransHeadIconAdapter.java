package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.models.Account;

import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class SpinAccountForTransHeadIconAdapter extends SpinAccountAdapter {

    public SpinAccountForTransHeadIconAdapter(Context context,
                                              int headLayout,
                                              List<Account> accountsL,
                                              NumberFormatManager numberFormatManager,
                                              ResourcesManager resourcesManager) {
        super(context, headLayout, accountsL, numberFormatManager, resourcesManager);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_icon_text, parent, false);
        TextView headText = findById(headSpinner, R.id.tvSpinHeadIconText);
        headText.setText(getAccountList().get(position).getName());
        ImageView headIcon = findById(headSpinner, R.id.ivSpinHeadIconText);
        headIcon.setImageResource(getTypeIconsArray().getResourceId(getAccountList().get(position).getType(), 0));
        return headSpinner;
    }
}
package com.androidcollider.easyfin.common.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Account;

import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class SpinAccountForTransAdapter extends SpinAccountAdapter {

    public SpinAccountForTransAdapter(Context context,
                                      int headLayout,
                                      List<Account> accountL,
                                      NumberFormatManager numberFormatManager,
                                      ResourcesManager resourcesManager) {
        super(context, headLayout, accountL, numberFormatManager, resourcesManager);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_text, parent, false);
        TextView headText = findById(headSpinner, R.id.tvSpinHeadText);
        headText.setText(getAccountList().get(position).getName());
        return headSpinner;
    }
}
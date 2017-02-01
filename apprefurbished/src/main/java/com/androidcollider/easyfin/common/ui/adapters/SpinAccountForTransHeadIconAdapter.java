package com.androidcollider.easyfin.common.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class SpinAccountForTransHeadIconAdapter extends SpinAccountAdapter {

    public SpinAccountForTransHeadIconAdapter(Context context,
                                              int headLayout,
                                              List<SpinAccountViewModel> accountsL,
                                              ResourcesManager resourcesManager) {
        super(context, headLayout, accountsL, resourcesManager);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_icon_text, parent, false);
        SpinAccountViewModel account = getItem(position);
        if (account != null) {
            TextView headText = findById(headSpinner, R.id.tvSpinHeadIconText);
            headText.setText(account.getName());
            ImageView headIcon = findById(headSpinner, R.id.ivSpinHeadIconText);
            headIcon.setImageResource(getTypeIconsArray().getResourceId(account.getType(), 0));
        }
        return headSpinner;
    }
}
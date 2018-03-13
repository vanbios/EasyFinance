package com.androidcollider.easyfin.common.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import java.util.List;

/**
 * @author Ihor Bilous
 */

public class SpinAccountForTransAdapter extends SpinAccountAdapter {

    public SpinAccountForTransAdapter(Context context,
                                      int headLayout,
                                      List<SpinAccountViewModel> accountL,
                                      ResourcesManager resourcesManager) {
        super(context, headLayout, accountL, resourcesManager);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_text, parent, false);
        SpinAccountViewModel account = getItem(position);
        if (account != null) {
            TextView headText = headSpinner.findViewById(R.id.tvSpinHeadText);
            headText.setText(account.getName());
        }
        return headSpinner;
    }
}
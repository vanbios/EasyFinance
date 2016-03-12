package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;

import java.util.ArrayList;

public class SpinAccountForTransHeadIconAdapter extends SpinAccountAdapter {

    public SpinAccountForTransHeadIconAdapter(Context context, int headLayout, ArrayList<Account> accountsL) {
        super(context, headLayout, accountsL);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_icon_text, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(R.id.tvSpinHeadIconText);
        headText.setText(getAccountList().get(position).getName());
        ImageView headIcon = (ImageView) headSpinner.findViewById(R.id.ivSpinHeadIconText);
        headIcon.setImageResource(getTypeIconsArray().getResourceId(getAccountList().get(position).getType(), 0));
        return headSpinner;
    }

}

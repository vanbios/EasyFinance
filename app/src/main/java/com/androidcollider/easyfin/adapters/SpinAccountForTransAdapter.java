package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.objects.Account;

import java.util.ArrayList;

public class SpinAccountForTransAdapter extends SpinAccountAdapter {

    public SpinAccountForTransAdapter(Context context, int headLayout, ArrayList<Account> accountL) {
        super(context, headLayout, accountL);
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = getInflater().inflate(R.layout.spin_head_text, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(R.id.tvSpinHeadText);
        headText.setText(getAccountList().get(position).getName());
        return headSpinner;
    }

}

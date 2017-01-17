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

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class SpinIconTextHeadAdapter extends ArrayAdapter<String> {

    private final TypedArray iconsArray;
    private final String[] textArray;
    private final int headLayout, headTvId, headIvId, dropLayout, dropTvId, dropIvId;
    private LayoutInflater inflater;

    public SpinIconTextHeadAdapter(Context context,
                                   int headLayout, int headTvId, int headIvId,
                                   int dropLayout, int dropTvId, int dropIvId,
                                   String[] text, TypedArray icons) {
        super(context, headLayout, text);
        textArray = text;
        iconsArray = icons;
        this.headLayout = headLayout;
        this.headTvId = headTvId;
        this.headIvId = headIvId;
        this.dropLayout = dropLayout;
        this.dropTvId = dropTvId;
        this.dropIvId = dropIvId;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView text = findById(dropSpinner, dropTvId);
        text.setText(textArray[position]);
        ImageView icon = findById(dropSpinner, dropIvId);
        icon.setImageResource(iconsArray.getResourceId(position, 0));
        return dropSpinner;
    }

    private View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(headLayout, parent, false);
        TextView headText = findById(headSpinner, headTvId);
        headText.setText(textArray[position]);
        ImageView icon = findById(headSpinner, headIvId);
        icon.setImageResource(iconsArray.getResourceId(position, 0));
        return headSpinner;
    }
}
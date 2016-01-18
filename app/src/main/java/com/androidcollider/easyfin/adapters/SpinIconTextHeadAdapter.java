package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


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
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomDropView(position, parent);}

    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomHeadView(pos, parent);}

    public View getCustomDropView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(dropLayout, parent, false);
        TextView text = (TextView) dropSpinner.findViewById(dropTvId);
        text.setText(textArray[position]);
        ImageView icon = (ImageView) dropSpinner.findViewById(dropIvId);
        icon.setImageResource(iconsArray.getResourceId(position, 0));
        return dropSpinner;
    }

    public View getCustomHeadView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(headLayout, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(headTvId);
        headText.setText(textArray[position]);
        ImageView icon = (ImageView) headSpinner.findViewById(headIvId);
        icon.setImageResource(iconsArray.getResourceId(position, 0));
        return headSpinner;
    }

}

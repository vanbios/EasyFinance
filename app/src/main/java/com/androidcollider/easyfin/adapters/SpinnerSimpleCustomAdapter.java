package com.androidcollider.easyfin.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;



public class SpinnerSimpleCustomAdapter extends ArrayAdapter<String>{

    final TypedArray iconsArray;
    final String[] textArray;
    LayoutInflater inflater;

    public SpinnerSimpleCustomAdapter(Context context, int tvResId, String[] text, TypedArray icons) {
        super(context, tvResId, text);
        textArray = text;
        iconsArray = icons;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomView(position, parent);}
    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomTopView(pos, parent);}

    public View getCustomView(int position, ViewGroup parent) {
        View dropSpinner = inflater.inflate(R.layout.spin_custom_dropdown_item, parent, false);
        TextView text = (TextView) dropSpinner.findViewById(R.id.tvSpinCustomSimpleDropdown);
        text.setText(textArray[position]);

        ImageView icon = (ImageView) dropSpinner.findViewById(R.id.ivSpinCustomSimpleDropdown);
        icon.setImageResource(iconsArray.getResourceId(position, 0));

        return dropSpinner;
    }

    public View getCustomTopView(int position, ViewGroup parent) {
        View headSpinner = inflater.inflate(R.layout.spin_custom_item, parent, false);
        TextView headText = (TextView) headSpinner.findViewById(R.id.tvSpinHeadText);
        headText.setText(textArray[position]);

        return headSpinner;
    }
}

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

public class SpinnerAccountTypeAdapter extends ArrayAdapter<String> {

    final TypedArray account_type_icons;
    final String[] account_type;
    LayoutInflater inflater;

    public SpinnerAccountTypeAdapter(Context context, int txtViewResourceId, String[] objects) {
        super(context, txtViewResourceId, objects);
        account_type = objects;
        account_type_icons = context.getResources().obtainTypedArray(R.array.expense_type_icons);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {return getCustomView(position, view, parent);}
    @Override
    public View getView(int pos, View view, ViewGroup parent) {return getCustomTopView(pos, view, parent);}

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View mySpinner = inflater.inflate(R.layout.spin_custom_dropdown_item, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.tvSpinDropdownCategory);
        main_text.setText(account_type[position]);

        ImageView left_icon = (ImageView) mySpinner .findViewById(R.id.ivSpinDropdownCategory);
        left_icon.setImageResource(account_type_icons.getResourceId(position, 0));

        return mySpinner;
    }

    public View getCustomTopView(int position, View convertView, ViewGroup parent) {
        View topSpinner = inflater.inflate(R.layout.spin_custom_item, parent, false);
        TextView top_text = (TextView) topSpinner.findViewById(R.id.tvSpinTopText);
        top_text.setText(account_type[position]);

        return topSpinner;
    }

}

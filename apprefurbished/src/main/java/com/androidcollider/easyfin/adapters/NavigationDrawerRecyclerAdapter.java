package com.androidcollider.easyfin.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidcollider.easyfin.R;

public class NavigationDrawerRecyclerAdapter extends RecyclerView.Adapter<NavigationDrawerRecyclerAdapter.ViewHolder> {

    private final String navTitles[];
    private final TypedArray navIcons;
    private final static int TYPE_HEADER = 0, TYPE_ITEM = 1, TYPE_DIVIDER = 2;


    public NavigationDrawerRecyclerAdapter(Context context) {
        navTitles = context.getResources().getStringArray(R.array.navigation_drawer_string_array);
        navIcons = context.getResources().obtainTypedArray(R.array.navigation_drawer_icons_array);
    }

    @Override
    public NavigationDrawerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_row, parent, false), TYPE_ITEM);
            case TYPE_HEADER:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_header, parent, false), TYPE_HEADER);
            case TYPE_DIVIDER:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_divider, parent, false), TYPE_DIVIDER);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerRecyclerAdapter.ViewHolder holder, int position) {
        if (holder.holderId == TYPE_ITEM) {
            int arrayPos = 0;
            if (position < 5) arrayPos = position - 1;
            else if (position > 5) arrayPos = position - 2;
            holder.tvNavItem.setText(navTitles[arrayPos]);
            holder.ivNavItem.setImageDrawable(navIcons.getDrawable(arrayPos));
        }
    }

    @Override
    public int getItemCount() {
        return navTitles.length + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) return TYPE_HEADER;
        if (isPositionDivider(position)) return TYPE_DIVIDER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionDivider(int position) {
        return position == 5;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;
        private TextView tvNavItem;
        private ImageView ivNavItem;

        ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_ITEM) {
                tvNavItem = (TextView) itemView.findViewById(R.id.tvItemNavRow);
                ivNavItem = (ImageView) itemView.findViewById(R.id.ivItemNavRow);
                holderId = TYPE_ITEM;
            } else {
                holderId = viewType == TYPE_HEADER ? TYPE_HEADER : TYPE_DIVIDER;
            }
        }
    }

}

package com.androidcollider.easyfin.faq;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;

import java.util.List;

/**
 * @author Ihor Bilous
 */

class RecyclerFAQAdapter extends RecyclerView.Adapter<RecyclerFAQAdapter.ViewHolder> {

    private List<Pair<String, String>> itemsList;


    RecyclerFAQAdapter(List<Pair<String, String>> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public RecyclerFAQAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerFAQAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false));
    }

    private Pair<String, String> getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerFAQAdapter.ViewHolder holder, final int position) {
        Pair<String, String> pair = getItem(position);
        holder.tvHead.setText(pair.first);
        holder.tvBody.setText(pair.second);
        holder.card.setOnClickListener(v ->
                holder.tvBody.setVisibility(
                        holder.tvBody.getVisibility() == View.GONE ?
                                View.VISIBLE : View.GONE)
        );
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView tvHead;
        private final TextView tvBody;


        ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.cardFAQ);
            tvHead = view.findViewById(R.id.tvFAQHead);
            tvBody = view.findViewById(R.id.tvFAQBody);
        }
    }
}
package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidcollider.easyfin.R;

public class FrgFAQ extends CommonFragment implements View.OnClickListener {

    private TextView tvAppBody, tvAccountsBody, tvTransactionsBody, tvDebtsBody, tvHomeBody;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_faq, container, false);
        initializeViews();
        return view;
    }

    private void initializeViews() {
        CardView cardApp = (CardView) view.findViewById(R.id.cardFAQApp);
        CardView cardAccounts = (CardView) view.findViewById(R.id.cardFAQAccounts);
        CardView cardTransactions = (CardView) view.findViewById(R.id.cardFAQTransactions);
        CardView cardDebts = (CardView) view.findViewById(R.id.cardFAQDebts);
        CardView cardHome = (CardView) view.findViewById(R.id.cardFAQHome);

        cardApp.setOnClickListener(this);
        cardAccounts.setOnClickListener(this);
        cardTransactions.setOnClickListener(this);
        cardDebts.setOnClickListener(this);
        cardHome.setOnClickListener(this);

        tvAppBody = (TextView) view.findViewById(R.id.tvFAQAppBody);
        tvAccountsBody = (TextView) view.findViewById(R.id.tvFAQAccountsBody);
        tvTransactionsBody = (TextView) view.findViewById(R.id.tvFAQTransactionsBody);
        tvDebtsBody = (TextView) view.findViewById(R.id.tvFAQDebtsBody);
        tvHomeBody = (TextView) view.findViewById(R.id.tvFAQHomeBody);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.cardFAQApp: {
                setVisibility(tvAppBody);
                break;
            }
            case R.id.cardFAQAccounts: {
                setVisibility(tvAccountsBody);
                break;
            }
            case R.id.cardFAQTransactions: {
                setVisibility(tvTransactionsBody);
                break;
            }
            case R.id.cardFAQDebts: {
                setVisibility(tvDebtsBody);
                break;
            }
            case R.id.cardFAQHome: {
                setVisibility(tvHomeBody);
                break;
            }
        }
    }

    private void setVisibility(TextView textView) {
        textView.setVisibility(textView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_faq);
    }

}

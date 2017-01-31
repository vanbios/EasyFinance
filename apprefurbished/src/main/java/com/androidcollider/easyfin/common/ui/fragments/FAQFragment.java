package com.androidcollider.easyfin.common.ui.fragments;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class FAQFragment extends CommonFragment {

    @BindView(R.id.tvFAQAppBody)
    TextView tvAppBody;
    @BindView(R.id.tvFAQAccountsBody)
    TextView tvAccountsBody;
    @BindView(R.id.tvFAQTransactionsBody)
    TextView tvTransactionsBody;
    @BindView(R.id.tvFAQDebtsBody)
    TextView tvDebtsBody;
    @BindView(R.id.tvFAQHomeBody)
    TextView tvHomeBody;
    @BindView(R.id.cardFAQApp)
    CardView cardApp;
    @BindView(R.id.cardFAQAccounts)
    CardView cardAccounts;
    @BindView(R.id.cardFAQTransactions)
    CardView cardTransactions;
    @BindView(R.id.cardFAQDebts)
    CardView cardDebts;
    @BindView(R.id.cardFAQHome)
    CardView cardHome;


    @Override
    public int getContentView() {
        return R.layout.frg_faq;
    }

    @OnClick({R.id.cardFAQApp, R.id.cardFAQAccounts, R.id.cardFAQTransactions, R.id.cardFAQDebts, R.id.cardFAQHome})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardFAQApp:
                setVisibility(tvAppBody);
                break;
            case R.id.cardFAQAccounts:
                setVisibility(tvAccountsBody);
                break;
            case R.id.cardFAQTransactions:
                setVisibility(tvTransactionsBody);
                break;
            case R.id.cardFAQDebts:
                setVisibility(tvDebtsBody);
                break;
            case R.id.cardFAQHome:
                setVisibility(tvHomeBody);
                break;
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
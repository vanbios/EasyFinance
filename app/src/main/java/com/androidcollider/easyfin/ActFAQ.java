package com.androidcollider.easyfin;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ActFAQ extends AppCompatActivity implements View.OnClickListener{

    private TextView tvAppBody, tvAccountsBody, tvTransactionsBody, tvDebtsBody, tvHomeBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_faq);

        setToolbar(R.string.app_faq);


        CardView cardApp = (CardView) findViewById(R.id.cardFAQApp);
        CardView cardAccounts = (CardView) findViewById(R.id.cardFAQAccounts);
        CardView cardTransactions = (CardView) findViewById(R.id.cardFAQTransactions);
        CardView cardDebts = (CardView) findViewById(R.id.cardFAQDebts);
        CardView cardHome = (CardView) findViewById(R.id.cardFAQHome);

        cardApp.setOnClickListener(this);
        cardAccounts.setOnClickListener(this);
        cardTransactions.setOnClickListener(this);
        cardDebts.setOnClickListener(this);
        cardHome.setOnClickListener(this);

        tvAppBody = (TextView) findViewById(R.id.tvFAQAppBody);
        tvAccountsBody = (TextView) findViewById(R.id.tvFAQAccountsBody);
        tvTransactionsBody = (TextView) findViewById(R.id.tvFAQTransactionsBody);
        tvDebtsBody = (TextView) findViewById(R.id.tvFAQDebtsBody);
        tvHomeBody = (TextView) findViewById(R.id.tvFAQHomeBody);
    }

    @Override
    public void onClick (View v) {

        switch (v.getId()) {

            case R.id.cardFAQApp: {
                setVisibility(tvAppBody);
                break;}
            case R.id.cardFAQAccounts: {
                setVisibility(tvAccountsBody);
                break;}
            case R.id.cardFAQTransactions: {
                setVisibility(tvTransactionsBody);
                break;}
            case R.id.cardFAQDebts: {
                setVisibility(tvDebtsBody);
                break;}
            case R.id.cardFAQHome: {
                setVisibility(tvHomeBody);
                break;}
        }
    }

    private void setVisibility(TextView textView) {

        if (textView.getVisibility() == View.GONE) {
            textView.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setToolbar(int id) {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(id);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolBar.inflateMenu(R.menu.toolbar_account_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;}
        }
        return false;
    }
}

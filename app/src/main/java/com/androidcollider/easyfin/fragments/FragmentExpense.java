package com.androidcollider.easyfin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.AccountInfo;

import java.util.ArrayList;


public class FragmentExpense extends Fragment{
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;

    View view;

    DataSource dataSource;

    public static FragmentExpense newInstance(int page) {
        FragmentExpense fragmentExpense = new FragmentExpense();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentExpense.setArguments(arguments);
        return fragmentExpense;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_expense, null);

        dataSource = new DataSource(getActivity());

        setItemExpense();

        return view;
    }

    private void setItemExpense() {
        int[] colors = new int[2];
        colors[0] = getResources().getColor(R.color.silver);
        colors[1] = getResources().getColor(R.color.gray);

        ArrayList<AccountInfo> accountInfoArrayList = dataSource.getAllAccountsInfo();



        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linLayoutFragmentExpense);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        int i = 0;

        for(AccountInfo accountInfo : accountInfoArrayList) {
            View item = layoutInflater.inflate(R.layout.item_fragment_expense, linearLayout, false);

            TextView tvItemFragmentExpenseName = (TextView) item.findViewById(R.id.tvItemFragmentExpenseName);
            TextView tvItemFragmentExpenseType = (TextView) item.findViewById(R.id.tvItemFragmentExpenseType);
            TextView tvItemFragmentExpenseAmount = (TextView) item.findViewById(R.id.tvItemFragmentExpenseAmount);

            String name = accountInfo.getName();
            String type = accountInfo.getType();
            double amount = accountInfo.getAmount();
            String currency = accountInfo.getCurrency();

            tvItemFragmentExpenseName.setText(name);
            tvItemFragmentExpenseType.setText(type);
            tvItemFragmentExpenseAmount.setText(Double.toString(amount) + " " + currency);

            item.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            item.setBackgroundColor(colors[i % 2]);
            linearLayout.addView(item);

            i++;
        }
    }
}

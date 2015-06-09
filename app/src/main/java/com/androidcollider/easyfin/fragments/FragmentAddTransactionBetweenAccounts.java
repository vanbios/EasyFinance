package com.androidcollider.easyfin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DataSource;

public class FragmentAddTransactionBetweenAccounts extends Fragment {


    private static final String ARGUMENT_PAGE_NUMBER = "argument_page_number";
    int pageNumber;
    private View view;
    private DataSource dataSource;



    public static FragmentTransaction newInstance(int page) {
        FragmentTransaction fragmentTransaction = new FragmentTransaction();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        fragmentTransaction.setArguments(arguments);
        return fragmentTransaction;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_transaction_between_accounts, null);

        dataSource = new DataSource(getActivity());


        return view;
    }
}

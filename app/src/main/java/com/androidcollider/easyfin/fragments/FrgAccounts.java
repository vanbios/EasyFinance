package com.androidcollider.easyfin.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;

import com.androidcollider.easyfin.adapters.RecyclerAccountAdapter;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.objects.InfoFromDB;

import java.util.ArrayList;


public class FrgAccounts extends Fragment {

    public final static String BROADCAST_FRG_ACCOUNT_ACTION = "com.androidcollider.easyfin.frgaccount.broadcast";
    public final static String PARAM_STATUS_FRG_ACCOUNT = "update_frg_account";
    public final static int STATUS_UPDATE_FRG_ACCOUNT = 4;

    private RecyclerView recyclerView;

    private TextView tvEmpty;

    private RecyclerAccountAdapter recyclerAdapter;
    private ArrayList<Account> accountList = null;

    private BroadcastReceiver broadcastReceiver;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_accounts, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerAccount);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyAccounts);

        setItemAccount();

        makeBroadcastReceiver();

        return view;
    }

    private void setItemAccount() {

        accountList = InfoFromDB.getInstance().getAccountList();

        setVisibility();

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerAccountAdapter(getActivity(), accountList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(PARAM_STATUS_FRG_ACCOUNT, 0);

                if (status == STATUS_UPDATE_FRG_ACCOUNT) {

                    accountList.clear();
                    accountList.addAll(InfoFromDB.getInstance().getAccountList());

                    setVisibility();

                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRG_ACCOUNT_ACTION);

        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void setVisibility() {
        if (accountList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }

        else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }



    public boolean onContextItemSelected(MenuItem item) {
        int pos;
        try {
            pos = (int) recyclerAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {

            case R.id.ctx_menu_edit_account:

            {
                goToEditAccount(pos);
                break;
            }

            case R.id.ctx_menu_delete_account:

            {
                showDialogDeleteAccount(pos);
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteAccount(final int pos) {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.dialog_title_delete_account))
                .content(getString(R.string.dialog_text_delete_account))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteAccount(pos);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                })
                .cancelable(false)
                .show();
    }

    private void goToEditAccount(int pos){
        Account account = accountList.get(pos);

        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 1);
        arguments.putSerializable("account", account);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }

    private void deleteAccount(int pos) {
        Account account = accountList.get(pos);
        int idAccount = account.getId();

        if (InfoFromDB.getInstance().getDataSource().checkAccountForTransactionOrDebtExist(idAccount)) {
            InfoFromDB.getInstance().getDataSource().makeAccountInvisible(idAccount);
        }
        else {
            InfoFromDB.getInstance().getDataSource().deleteAccount(idAccount);}

        accountList.remove(pos);

        setVisibility();

        recyclerAdapter.notifyDataSetChanged();

        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();

    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFrgMain);
    }


    public void addFragment(Fragment f){
        treatFragment(f, true, false);
    }

    public Fragment getTopFragment(){
        return getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void treatFragment(Fragment f, boolean addToBackStack, boolean replace){
        String tag = f.getClass().getName();
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();

        if (replace) {

            ft.replace(R.id.fragment_container, f, tag);

        } else {

            Fragment currentTop = getTopFragment();

            if (currentTop != null) ft.hide(currentTop);

            ft.add(R.id.fragment_container, f, tag);
        }

        if (addToBackStack) ft.addToBackStack(tag);

        ft.commitAllowingStateLoss();
    }

}

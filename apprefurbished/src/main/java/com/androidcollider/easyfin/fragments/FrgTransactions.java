package com.androidcollider.easyfin.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.MainActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.RecyclerTransactionAdapter;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.objects.Transaction;

import java.util.ArrayList;

public class FrgTransactions extends Fragment {

    public final static String BROADCAST_FRG_TRANSACTION_ACTION = "com.androidcollider.easyfin.frgtransaction.broadcast";
    public final static String PARAM_STATUS_FRG_TRANSACTION = "update_frg_transaction";
    public final static int STATUS_UPDATE_FRG_TRANSACTION = 3;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Transaction> transactionList = null;
    private RecyclerTransactionAdapter recyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_transactions, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerTransaction);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyTransactions);
        setupRecyclerView();
        registerForContextMenu(recyclerView);
        makeBroadcastReceiver();
        return view;
    }

    private void setupRecyclerView() {
        transactionList = InfoFromDB.getInstance().getDataSource().getAllTransactionsInfo();
        setVisibility();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerTransactionAdapter(getActivity(), transactionList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FrgMain parentFragment = (FrgMain) getParentFragment();
                if (parentFragment != null) {
                    if (dy > 0) {
                        parentFragment.hideMenu();
                    } else if (dy < 0) {
                        parentFragment.showMenu();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(PARAM_STATUS_FRG_TRANSACTION, 0) == STATUS_UPDATE_FRG_TRANSACTION) {
                    transactionList.clear();
                    transactionList.addAll(InfoFromDB.getInstance().getDataSource().getAllTransactionsInfo());
                    setVisibility();
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_FRG_TRANSACTION_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void setVisibility() {
        recyclerView.setVisibility(transactionList.isEmpty() ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(transactionList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int pos;
        try {
            pos = (int) recyclerAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.ctx_menu_edit_transaction:
                goToEditTransaction(pos);
                break;
            case R.id.ctx_menu_delete_transaction:
                showDialogDeleteTransaction(pos);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteTransaction(final int pos) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.dialog_title_delete))
                .content(getString(R.string.transaction_delete_warning))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> deleteTransaction(pos))
                .cancelable(false)
                .show();
    }

    private void goToEditTransaction(int pos) {
        Transaction transaction = transactionList.get(pos);

        FrgAddTransactionDefault frgAddTransDef = new FrgAddTransactionDefault();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 1);
        arguments.putSerializable("transaction", transaction);
        frgAddTransDef.setArguments(arguments);
        ((MainActivity) getActivity()).addFragment(frgAddTransDef);
    }

    private void deleteTransaction(int pos) {
        Transaction transaction = transactionList.get(pos);
        int idAccount = transaction.getIdAccount();
        int idTrans = transaction.getId();
        double amount = transaction.getAmount();

        InfoFromDB.getInstance().getDataSource().deleteTransaction(idAccount, idTrans, amount);

        transactionList.remove(pos);
        setVisibility();
        recyclerAdapter.notifyDataSetChanged();
        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();
    }

    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
    }

}

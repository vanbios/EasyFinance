package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.RecyclerTransactionAdapter;
import com.androidcollider.easyfin.common.MainActivity;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.models.Transaction;
import com.androidcollider.easyfin.repository.Repository;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class FrgTransactions extends CommonFragmentWithEvents {

    @BindView(R.id.recyclerTransaction)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmptyTransactions)
    TextView tvEmpty;

    private List<Transaction> transactionList;
    private RecyclerTransactionAdapter recyclerAdapter;

    @Inject
    Repository repository;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    NumberFormatManager numberFormatManager;


    @Override
    public int getContentView() {
        return R.layout.frg_transactions;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        //registerForContextMenu(recyclerView);
    }

    private void setupRecyclerView() {
        repository.getAllTransactions()
                .subscribe(new Subscriber<List<Transaction>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Transaction> transactionList) {
                        FrgTransactions.this.transactionList = new ArrayList<>();
                        FrgTransactions.this.transactionList.addAll(transactionList);
                        setVisibility();
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerAdapter = new RecyclerTransactionAdapter(getActivity(), FrgTransactions.this.transactionList,
                                dateFormatManager, numberFormatManager);
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
                });
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
        FrgAddTransactionDefault frgAddTransDef = new FrgAddTransactionDefault();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 1);
        arguments.putSerializable("transaction", transactionList.get(pos));
        frgAddTransDef.setArguments(arguments);
        ((MainActivity) getActivity()).addFragment(frgAddTransDef);
    }

    private void deleteTransaction(int pos) {
        Transaction transaction = transactionList.get(pos);
        int idAccount = transaction.getIdAccount();
        int idTrans = transaction.getId();
        double amount = transaction.getAmount();

        //InMemoryRepository.getInstance().getDataSource().deleteTransaction(idAccount, idTrans, amount);

        repository.deleteTransaction(idAccount, idTrans, amount)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        transactionList.remove(pos);
                        setVisibility();
                        recyclerAdapter.notifyDataSetChanged();
                        //InMemoryRepository.getInstance().updateAccountList();
                        pushBroadcast();
                    }
                });
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgTransactions event) {
        repository.getAllTransactions()
                .subscribe(new Subscriber<List<Transaction>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Transaction> transactionList) {
                        FrgTransactions.this.transactionList.clear();
                        FrgTransactions.this.transactionList.addAll(transactionList);
                        setVisibility();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }
}
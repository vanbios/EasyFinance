package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
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
import com.androidcollider.easyfin.common.MainActivity;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.repository.memory.InMemoryRepository;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class FrgAccounts extends CommonFragmentWithEvents {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private RecyclerAccountAdapter recyclerAdapter;
    private List<Account> accountList = null;

    @Inject
    Repository repository;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_accounts, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerAccount);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyAccounts);
        setupRecyclerView();
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    private void setupRecyclerView() {
        repository.getAllAccounts()
                .subscribe(new Subscriber<List<Account>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Account> accountList) {
                        FrgAccounts.this.accountList = new ArrayList<>();
                        FrgAccounts.this.accountList.addAll(accountList);
                        setVisibility();
                        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                        recyclerAdapter = new RecyclerAccountAdapter(getActivity(), FrgAccounts.this.accountList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgAccounts event) {
        repository.getAllAccounts()
                .subscribe(new Subscriber<List<Account>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Account> accountList) {
                        FrgAccounts.this.accountList.clear();
                        FrgAccounts.this.accountList.addAll(accountList);
                        setVisibility();
                        recyclerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setVisibility() {
        recyclerView.setVisibility(accountList.isEmpty() ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(accountList.isEmpty() ? View.VISIBLE : View.GONE);
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
                goToEditAccount(pos);
                break;
            case R.id.ctx_menu_delete_account:
                showDialogDeleteAccount(pos);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteAccount(final int pos) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.dialog_title_delete))
                .content(getString(R.string.dialog_text_delete_account))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> deleteAccount(pos))
                .cancelable(false)
                .show();
    }

    private void goToEditAccount(int pos) {
        Account account = accountList.get(pos);
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 1);
        arguments.putSerializable("account", account);
        frgAddAccount.setArguments(arguments);
        ((MainActivity) getActivity()).addFragment(frgAddAccount);
    }

    private void deleteAccount(int pos) {
        InMemoryRepository.getInstance().getDataSource().deleteAccount(accountList.get(pos).getId());

        accountList.remove(pos);
        setVisibility();
        recyclerAdapter.notifyDataSetChanged();
        InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
    }
}
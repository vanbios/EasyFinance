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
import com.androidcollider.easyfin.common.MainActivity;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.RecyclerAccountAdapter;
import com.androidcollider.easyfin.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.MemoryRepository;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class FrgAccounts extends CommonFragmentWithEvents {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private RecyclerAccountAdapter recyclerAdapter;
    private ArrayList<Account> accountList = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_accounts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerAccount);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyAccounts);
        setupRecyclerView();
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    private void setupRecyclerView() {
        accountList = MemoryRepository.getInstance().getAccountList();
        setVisibility();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerAccountAdapter(getActivity(), accountList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgAccounts event) {
        accountList.clear();
        accountList.addAll(MemoryRepository.getInstance().getAccountList());
        setVisibility();
        recyclerAdapter.notifyDataSetChanged();
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
        MemoryRepository.getInstance().getDataSource().deleteAccount(accountList.get(pos).getId());

        accountList.remove(pos);
        setVisibility();
        recyclerAdapter.notifyDataSetChanged();
        MemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
    }
}
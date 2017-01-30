package com.androidcollider.easyfin.accounts.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.accounts.add_edit.AddAccountFragment;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents;
import com.androidcollider.easyfin.main.MainFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Ihor Bilous
 */

public class AccountsFragment extends CommonFragmentWithEvents implements AccountsMVP.View {

    @BindView(R.id.recyclerAccount)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmptyAccounts)
    TextView tvEmpty;
    private RecyclerAccountAdapter recyclerAdapter;

    public static final String ACCOUNT = "account", MODE = "mode";

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    AccountsMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_accounts;
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
        presenter.setView(this);
        presenter.loadData();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerAccountAdapter(resourcesManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                MainFragment parentFragment = (MainFragment) getParentFragment();
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
        presenter.loadData();
    }

    private void setVisibility() {
        recyclerView.setVisibility(recyclerAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(recyclerAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id;
        try {
            id = recyclerAdapter.getCurrentId();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.ctx_menu_edit_account:
                presenter.getAccountById(id);
                break;
            case R.id.ctx_menu_delete_account:
                showDialogDeleteAccount(id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteAccount(final int id) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showDeleteDialog(
                    activity,
                    getString(R.string.dialog_text_delete_account),
                    (dialog, which) -> presenter.deleteAccountById(id)
            );
        }
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
    }

    @Override
    public void setAccountList(List<AccountViewModel> accountList) {
        recyclerAdapter.setItems(accountList);
        setVisibility();
    }

    @Override
    public void goToEditAccount(Account account) {
        AddAccountFragment addAccountFragment = new AddAccountFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MODE, 1);
        arguments.putSerializable(ACCOUNT, account);
        addAccountFragment.setArguments(arguments);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.addFragment(addAccountFragment);
        }
    }

    @Override
    public void deleteAccount() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.getCurrentId()));
        setVisibility();
        pushBroadcast();
    }
}
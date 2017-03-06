package com.androidcollider.easyfin.transactions.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents;
import com.androidcollider.easyfin.main.MainFragment;
import com.androidcollider.easyfin.transactions.add_edit.income_expense.AddTransactionIncomeExpenseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Ihor Bilous
 */

public class TransactionsFragment extends CommonFragmentWithEvents implements TransactionsMVP.View {

    @BindView(R.id.recyclerTransaction)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmptyTransactions)
    TextView tvEmpty;

    private RecyclerTransactionAdapter recyclerAdapter;

    public static final String TRANSACTION = "transaction", MODE = "mode", TYPE = "type";
    public static final int MODE_ADD = 0, MODE_EDIT = 1, TYPE_EXPENSE = 0, TYPE_INCOME = 1;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    LetterTileManager letterTileManager;

    @Inject
    TransactionsMVP.Presenter presenter;


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
        presenter.setView(this);
        presenter.loadData();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerTransactionAdapter(resourcesManager, letterTileManager);
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

    private void setVisibility() {
        recyclerView.setVisibility(recyclerAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(recyclerAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id = recyclerAdapter.getCurrentId();
        switch (item.getItemId()) {
            case R.id.ctx_menu_edit_transaction:
                presenter.getTransactionById(id);
                break;
            case R.id.ctx_menu_delete_transaction:
                showDialogDeleteTransaction(id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteTransaction(final int id) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showDeleteDialog(
                    activity,
                    getString(R.string.transaction_delete_warning),
                    (dialog, which) -> presenter.deleteTransactionById(id)
            );
        }
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgTransactions event) {
        presenter.loadData();
    }


    @Override
    public void setTransactionAndTransactionCategoriesLists(List<TransactionViewModel> transactionList,
                                                            List<TransactionCategory> transactionCategoryIncomeList,
                                                            List<TransactionCategory> transactionCategoryExpenseList) {
        recyclerAdapter.setTransactionCategories(transactionCategoryIncomeList, transactionCategoryExpenseList);
        recyclerAdapter.setItems(transactionList);
        setVisibility();
    }

    @Override
    public void goToEditTransaction(Transaction transaction) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            AddTransactionIncomeExpenseFragment frgAddTransDef = new AddTransactionIncomeExpenseFragment();
            Bundle arguments = new Bundle();
            arguments.putInt(MODE, MODE_EDIT);
            arguments.putSerializable(TRANSACTION, transaction);
            frgAddTransDef.setArguments(arguments);
            activity.addFragment(frgAddTransDef);
        }
    }

    @Override
    public void deleteTransaction() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.getCurrentId()));
        setVisibility();
        pushBroadcast();
    }
}
package com.androidcollider.easyfin.transaction_categories.nested;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.letter_tile.LetterTileManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents;
import com.androidcollider.easyfin.transaction_categories.root.TransactionCategoriesRootFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class TransactionCategoriesNestedFragment extends CommonFragmentWithEvents
        implements TransactionCategoriesNestedMVP.View {

    @BindView(R.id.rv_transaction_categories)
    RecyclerView recyclerView;

    public static final String TYPE = "type";
    public static final int TYPE_EXPENSE = 1, TYPE_INCOME = 0;

    private RecyclerTransactionCategoriesAdapter recyclerAdapter;

    private EditText etTransCategoryName;
    private MaterialDialog updateTransactionCategoryDialog;

    private int type;

    @Inject
    LetterTileManager letterTileManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    ToastManager toastManager;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    TransactionCategoriesNestedMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_transaction_categories_nested;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();

        type = getArguments().getInt(TYPE);

        presenter.setView(this);
        presenter.setArguments(getArguments());
        presenter.loadData();
    }

    private void setupUI() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerTransactionCategoriesAdapter(letterTileManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                TransactionCategoriesRootFragment parentFragment = (TransactionCategoriesRootFragment) getParentFragment();
                if (parentFragment != null) {
                    if (dy > 0) {
                        parentFragment.hideFab();
                    } else if (dy < 0) {
                        parentFragment.showFab();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public boolean onContextItemSelected(MenuItem item) {
        int currentType = getCurrentParentType();
        if (currentType == type) {
            int id = recyclerAdapter.getCurrentId();
            switch (item.getItemId()) {
                case R.id.ctx_menu_edit_transaction_category:
                    showUpdateTransactionCategoryDialog(id);
                    break;
                case R.id.ctx_menu_delete_transaction_category:
                    showDeleteTransactionCategoryDialog(id);
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }

    private int getCurrentParentType() {
        TransactionCategoriesRootFragment parentFragment = (TransactionCategoriesRootFragment) getParentFragment();
        if (parentFragment != null) {
            return parentFragment.getCurrentType();
        }
        return -1;
    }

    private void showDeleteTransactionCategoryDialog(final int id) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showDeleteDialog(
                    activity,
                    getString(R.string.category_delete_warning),
                    (dialog, which) -> presenter.deleteTransactionCategoryById(id)
            );
        }
    }

    private void showUpdateTransactionCategoryDialog(int id) {
        updateTransactionCategoryDialog = dialogManager.buildUpdateTransactionCategoryDialog(getActivity(),
                (dialog, which) -> {
                    if (etTransCategoryName != null) {
                        presenter.updateTransactionCategory(id, etTransCategoryName.getText().toString().trim());
                    }
                },
                (dialog, which) -> {
                    dialog.dismiss();
                    etTransCategoryName.getText().clear();
                });

        View root = updateTransactionCategoryDialog.getCustomView();
        if (root != null) {
            etTransCategoryName = findById(root, R.id.et_transaction_category_name);
            etTransCategoryName.setText(presenter.getCategoryNameById(id));
            etTransCategoryName.setSelection(etTransCategoryName.getText().toString().length());
        }

        updateTransactionCategoryDialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgTransactionCategories event) {
        presenter.loadTransactionCategories();
    }

    @Override
    public void setTransactionCategoryList(List<TransactionCategory> transactionCategoryList, TypedArray iconsArray) {
        recyclerAdapter.setItems(transactionCategoryList, iconsArray);
    }

    @Override
    public void showMessage(String message) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toastManager.showClosableToast(activity, message, ToastManager.SHORT);
        }
    }

    @Override
    public void shakeDialogUpdateTransactionCategoryField() {
        if (etTransCategoryName != null) {
            shakeEditTextManager.highlightEditText(etTransCategoryName);
        }
    }

    @Override
    public void dismissDialogUpdateTransactionCategory() {
        if (updateTransactionCategoryDialog != null && updateTransactionCategoryDialog.isShowing()) {
            updateTransactionCategoryDialog.dismiss();
            etTransCategoryName.getText().clear();
        }
    }

    @Override
    public void handleTransactionCategoryUpdated() {
        presenter.loadTransactionCategories();
        EventBus.getDefault().post(new UpdateFrgTransactions());
    }

    @Override
    public void deleteTransactionCategory() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.getCurrentId()));
    }
}
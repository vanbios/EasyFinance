package com.androidcollider.easyfin.transaction_categories.root;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactionCategories;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.ViewPagerFragmentAdapter;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;
import com.androidcollider.easyfin.transaction_categories.nested.TransactionCategoriesNestedFragment;
import com.github.clans.fab.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class TransactionCategoriesRootFragment extends CommonFragment
        implements TransactionCategoriesRootMVP.View {

    @BindView(R.id.vp_transaction_categories)
    ViewPager pager;
    @BindView(R.id.tabs_transaction_categories)
    TabLayout tabLayout;
    @BindView(R.id.fab_add_transaction_category)
    FloatingActionButton fabAddNew;

    private EditText etNewTransCategoryName;
    private MaterialDialog transactionCategoryDialog;

    @Inject
    ToastManager toastManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    TransactionCategoriesRootMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_transaction_categories_root;
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

        fabAddNew.hide(false);
        new Handler().postDelayed(() -> fabAddNew.show(true), 1000);

        presenter.setView(this);
        presenter.loadData();
    }

    private void setupUI() {
        setupViewPager();
        buildTransactionCategoryDialog();
    }

    private void setupViewPager() {
        ViewPagerFragmentAdapter adapterPager = new ViewPagerFragmentAdapter(getChildFragmentManager());

        adapterPager.addFragment(getNestedFragment(false), getResources().getString(R.string.income).toUpperCase());
        adapterPager.addFragment(getNestedFragment(true), getResources().getString(R.string.cost).toUpperCase());

        pager.setAdapter(adapterPager);
        pager.setOffscreenPageLimit(2);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showFab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout.setupWithViewPager(pager);
    }

    private TransactionCategoriesNestedFragment getNestedFragment(boolean isExpense) {
        TransactionCategoriesNestedFragment fragment = new TransactionCategoriesNestedFragment();
        Bundle args = new Bundle();
        args.putInt(TransactionCategoriesNestedFragment.TYPE,
                isExpense ?
                        TransactionCategoriesNestedFragment.TYPE_EXPENSE :
                        TransactionCategoriesNestedFragment.TYPE_INCOME
        );
        fragment.setArguments(args);
        return fragment;
    }

    private void buildTransactionCategoryDialog() {
        transactionCategoryDialog = dialogManager.buildAddTransactionCategoryDialog(getActivity(),
                (dialog, which) -> {
                    if (etNewTransCategoryName != null) {
                        presenter.addNewCategory(etNewTransCategoryName.getText().toString().trim(), checkCategoryIsExpense());
                    }
                },
                (dialog, which) -> dialog.dismiss());

        View root = transactionCategoryDialog.getCustomView();
        if (root != null) {
            etNewTransCategoryName = root.findViewById(R.id.et_transaction_category_name);
        }
    }

    private boolean checkCategoryIsExpense() {
        return pager.getCurrentItem() == 1;
    }

    public int getCurrentType() {
        return pager.getCurrentItem();
    }

    public void hideFab() {
        if (!fabAddNew.isHidden()) {
            fabAddNew.hide(true);
        }
    }

    public void showFab() {
        if (fabAddNew.isHidden()) {
            fabAddNew.show(true);
        }
    }

    @OnClick({R.id.fab_add_transaction_category})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_transaction_category:
                transactionCategoryDialog.show();
                break;
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.transaction_categories);
    }

    @Override
    public void showMessage(String message) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            toastManager.showClosableToast(activity, message, ToastManager.SHORT);
        }
    }

    @Override
    public void shakeDialogNewTransactionCategoryField() {
        if (etNewTransCategoryName != null) {
            shakeEditTextManager.highlightEditText(etNewTransCategoryName);
        }
    }

    @Override
    public void dismissDialogNewTransactionCategory() {
        if (transactionCategoryDialog != null && transactionCategoryDialog.isShowing()) {
            transactionCategoryDialog.dismiss();
            etNewTransCategoryName.getText().clear();
        }
    }

    @Override
    public void handleNewTransactionCategoryAdded() {
        EventBus.getDefault().post(new UpdateFrgTransactionCategories());
    }
}
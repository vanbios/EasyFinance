package com.androidcollider.easyfin.debts.list;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.models.Debt;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;
import com.androidcollider.easyfin.debts.add_edit.AddDebtFragment;
import com.androidcollider.easyfin.debts.pay.PayDebtFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class DebtsFragment extends CommonFragment implements DebtsMVP.View {

    @BindView(R.id.recyclerDebt)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmptyDebt)
    TextView tvEmpty;
    @BindView(R.id.btnFloatDebts)
    FloatingActionMenu fabMenu;
    @BindView(R.id.btnFloatAddDebtTake)
    FloatingActionButton faButtonTake;
    @BindView(R.id.btnFloatAddDebtGive)
    FloatingActionButton faButtonGive;
    @BindView(R.id.debts_content)
    RelativeLayout mainContent;

    public static final int PAY_ALL = 1, PAY_PART = 2, TAKE_MORE = 3, ADD = 0, EDIT = 1;
    static final int ACTION_EDIT = 1, ACTION_PAY = 2;
    public static final int TYPE_GIVE = 0, TYPE_TAKE = 1;
    public static final String DEBT = "debt", TYPE = "type", MODE = "mode";

    private RecyclerDebtAdapter recyclerAdapter;

    @Inject
    DialogManager dialogManager;

    @Inject
    DebtsMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_debts;
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
        presenter.setView(this);
        presenter.loadData();
        EventBus.getDefault().register(this);
        fabMenu.hideMenu(false);
        new Handler().postDelayed(() -> fabMenu.showMenu(true), 300);
    }

    private void setupUI() {
        setupRecyclerView();
        addNonFabTouchListener(mainContent);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerDebtAdapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    if (!fabMenu.isMenuHidden()) {
                        fabMenu.hideMenu(true);
                    }
                } else if (dy < 0) {
                    if (fabMenu.isMenuHidden()) {
                        fabMenu.showMenu(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int id = recyclerAdapter.getCurrentId();
        switch (item.getItemId()) {
            case R.id.ctx_menu_pay_all_debt:
                presenter.getDebtById(id, PAY_ALL, ACTION_PAY);
                break;
            case R.id.ctx_menu_pay_part_debt:
                presenter.getDebtById(id, PAY_PART, ACTION_PAY);
                break;
            case R.id.ctx_menu_take_more_debt:
                presenter.getDebtById(id, TAKE_MORE, ACTION_PAY);
                break;
            case R.id.ctx_menu_edit_debt:
                presenter.getDebtById(id, EDIT, ACTION_EDIT);
                break;
            case R.id.ctx_menu_delete_debt:
                showDialogDeleteDebt(id);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteDebt(final int id) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showDeleteDialog(
                    activity,
                    getString(R.string.debt_delete_warning),
                    (dialog, which) -> presenter.deleteDebtById(id)
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgDebts event) {
        presenter.loadData();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    private void goToAddDebt(int type) {
        AddDebtFragment addDebtFragment = new AddDebtFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MODE, ADD);
        arguments.putInt(TYPE, type);
        addDebtFragment.setArguments(arguments);

        addFragment(addDebtFragment);
    }

    private void collapseFloatingMenu(boolean withAnim) {
        if (fabMenu.isOpened()) {
            fabMenu.close(withAnim);
        }
    }

    @OnClick({R.id.btnFloatAddDebtTake, R.id.btnFloatAddDebtGive})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFloatAddDebtTake:
                goToAddDebt(TYPE_TAKE);
                collapseFloatingMenu(false);
                break;
            case R.id.btnFloatAddDebtGive:
                goToAddDebt(TYPE_GIVE);
                collapseFloatingMenu(false);
                break;
        }
    }

    private void addNonFabTouchListener(View view) {
        if (view instanceof RelativeLayout
                || view instanceof RecyclerView
                || view instanceof TextView) {
            view.setOnTouchListener((v, event) -> {
                collapseFloatingMenu(true);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                addNonFabTouchListener(innerView);
            }
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.debts);
    }

    @Override
    public void setDebtList(List<DebtViewModel> debtList) {
        recyclerAdapter.setItems(debtList);
        setVisibility();
    }

    @Override
    public void goToEditDebt(Debt debt, int mode) {
        AddDebtFragment addDebtFragment = new AddDebtFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MODE, mode);
        arguments.putSerializable(DEBT, debt);
        addDebtFragment.setArguments(arguments);

        addFragment(addDebtFragment);
    }

    @Override
    public void goToPayDebt(Debt debt, int mode) {
        PayDebtFragment payDebtFragment = new PayDebtFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MODE, mode);
        arguments.putSerializable(DEBT, debt);
        payDebtFragment.setArguments(arguments);

        addFragment(payDebtFragment);
    }

    @Override
    public void deleteDebt() {
        recyclerAdapter.deleteItem(recyclerAdapter.getPositionById(recyclerAdapter.getCurrentId()));
        setVisibility();
        pushBroadcast();
    }
}
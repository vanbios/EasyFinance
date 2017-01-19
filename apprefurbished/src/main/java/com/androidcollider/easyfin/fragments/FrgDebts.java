package com.androidcollider.easyfin.fragments;

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.RecyclerDebtAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.repository.Repository;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class FrgDebts extends CommonFragment {

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

    private List<Debt> debtList;
    private RecyclerDebtAdapter recyclerAdapter;

    @Inject
    Repository repository;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    NumberFormatManager numberFormatManager;


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

        debtList = new ArrayList<>();
        setupUI();
        loadData();
        EventBus.getDefault().register(this);
        fabMenu.hideMenu(false);
        new Handler().postDelayed(() -> fabMenu.showMenu(true), 300);
    }

    private void setupUI() {
        setupRecyclerView();
        addNonFabTouchListener(mainContent);
    }

    private void loadData() {
        repository.getAllDebts()
                .subscribe(new Subscriber<List<Debt>>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Debt> debtList) {
                        FrgDebts.this.debtList.clear();
                        FrgDebts.this.debtList.addAll(debtList);
                        recyclerAdapter.addItems(FrgDebts.this.debtList);
                        setVisibility();
                    }
                });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerDebtAdapter(getActivity(), dateFormatManager, numberFormatManager);
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
        recyclerView.setVisibility(debtList.isEmpty() ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(debtList.isEmpty() ? View.VISIBLE : View.GONE);
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

    private void goToAddDebt(int type) {
        FrgAddDebt frgAddDebt = new FrgAddDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        arguments.putInt("type", type);
        frgAddDebt.setArguments(arguments);

        addFragment(frgAddDebt);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int pos;
        try {
            pos = (int) recyclerAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.ctx_menu_pay_all_debt:
                goToPayDebt(pos, 1);
                break;
            case R.id.ctx_menu_pay_part_debt:
                goToPayDebt(pos, 2);
                break;
            case R.id.ctx_menu_take_more_debt:
                goToPayDebt(pos, 3);
                break;
            case R.id.ctx_menu_edit_debt:
                goToEditDebt(pos, 1);
                break;
            case R.id.ctx_menu_delete_debt:
                showDialogDeleteDebt(pos);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteDebt(final int pos) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.dialog_title_delete))
                .content(getString(R.string.debt_delete_warning))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> deleteDebt(pos))
                .cancelable(false)
                .show();
    }

    private void deleteDebt(int pos) {
        Debt debt = debtList.get(pos);
        repository.deleteDebt(debt.getIdAccount(), debt.getId(), debt.getAmountCurrent(), debt.getType())
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        debtList.remove(pos);
                        recyclerAdapter.deleteItem(pos);
                        setVisibility();
                        pushBroadcast();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgDebts event) {
        loadData();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    private void goToPayDebt(int pos, int mode) {
        FrgPayDebt frgPayDebt = new FrgPayDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", mode);
        arguments.putSerializable("debt", debtList.get(pos));
        frgPayDebt.setArguments(arguments);

        addFragment(frgPayDebt);
    }

    public void goToEditDebt(int pos, int mode) {
        FrgAddDebt frgAddDebt = new FrgAddDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", mode);
        arguments.putSerializable("debt", debtList.get(pos));
        frgAddDebt.setArguments(arguments);

        addFragment(frgAddDebt);
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
                goToAddDebt(1);
                collapseFloatingMenu(false);
                break;
            case R.id.btnFloatAddDebtGive:
                goToAddDebt(0);
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
        //If a layout container, iterate over children and seed recursion.
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
}
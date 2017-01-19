package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.repository.Repository;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class FrgPayDebt extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    @BindView(R.id.tvPayDebtName)
    TextView tvDebtName;
    @BindView(R.id.tvPayDebtAmount)
    TextView tvAmount;
    @BindView(R.id.spinPayDebtAccount)
    Spinner spinAccount;
    @BindView(R.id.cardPayDebtElements)
    CardView cardView;
    @BindView(R.id.layoutActPayDebtParent)
    RelativeLayout mainContent;

    private Debt debt;
    private List<Account> accountsAvailableList;
    private int mode;

    @Inject
    Repository repository;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;


    @Override
    public int getContentView() {
        return R.layout.frg_pay_debt;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mode = getArguments().getInt("mode", 0);
        debt = (Debt) getArguments().getSerializable("debt");

        setToolbar();
        fillAvailableAccountsList();

        if (accountsAvailableList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount(getString(R.string.debt_no_available_accounts_warning), true);
        } else {
            cardView.setVisibility(View.VISIBLE);
            setViews();
            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());
        }
    }

    private void setViews() {
        tvDebtName.setText(debt.getName());
        if (mode == 1 || mode == 2) {
            String amount = numberFormatManager.doubleToStringFormatterForEdit(
                    debt.getAmountCurrent(),
                    NumberFormatManager.FORMAT_1,
                    NumberFormatManager.PRECISE_1
            );
            setTVTextSize(tvAmount, amount, 10, 15);
            tvAmount.setText(amount);
        } else {
            tvAmount.setText("0,00");
            openNumericDialog(tvAmount.getText().toString());
        }
        if (mode == 1) tvAmount.setClickable(false);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountsAvailableList,
                numberFormatManager,
                resourcesManager
        ));

        int idAccount = debt.getIdAccount();
        int pos = 0;
        for (int i = 0; i < accountsAvailableList.size(); i++) {
            if (idAccount == accountsAvailableList.get(i).getId()) {
                pos = i;
                break;
            }
        }

        spinAccount.setSelection(pos);
    }

    private void fillAvailableAccountsList() {
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
                        accountsAvailableList = new ArrayList<>();
                        String currency = debt.getCurrency();
                        double amount = debt.getAmountCurrent();
                        int type = debt.getType();

                        Stream.of(accountList)
                                .filter(account ->
                                        mode == 1 && type == 1 ?
                                                account.getCurrency().equals(currency) && account.getAmount() >= amount :
                                                account.getCurrency().equals(currency))
                                .forEach(accountsAvailableList::add);
                    }
                });
    }

    private void payAllDebt() {
        double amountDebt = debt.getAmountCurrent();
        int type = debt.getType();

        Account account = (Account) spinAccount.getSelectedItem();

        double amountAccount = account.getAmount();

        if (type == 1) {
            amountAccount -= amountDebt;
        } else {
            amountAccount += amountDebt;
        }

        repository.payFullDebt(account.getId(), amountAccount, debt.getId())
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        lastActions();
                    }
                });
    }

    private void payPartDebt() {
        String sum = numberFormatManager.prepareStringToParse(tvAmount.getText().toString());
        if (checkForFillSumField(sum)) {
            double amountDebt = Double.parseDouble(sum);
            double amountAllDebt = debt.getAmountCurrent();

            if (amountDebt > amountAllDebt)
                toastManager.showClosableToast(getActivity(), getString(R.string.debt_sum_more_then_amount), ToastManager.SHORT);
            else {
                int type = debt.getType();
                Account account = (Account) spinAccount.getSelectedItem();

                double amountAccount = account.getAmount();

                if (type == 1 && amountDebt > amountAccount) {
                    toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
                } else {
                    int idDebt = debt.getId();
                    int idAccount = account.getId();

                    if (type == 1) {
                        amountAccount -= amountDebt;
                    } else {
                        amountAccount += amountDebt;
                    }

                    if (amountDebt == amountAllDebt) {
                        repository.payFullDebt(idAccount, amountAccount, idDebt)
                                .subscribe(new Subscriber<Boolean>() {

                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        lastActions();
                                    }
                                });
                    } else {
                        double newDebtAmount = amountAllDebt - amountDebt;
                        repository.payPartOfDebt(idAccount, amountAccount, idDebt, newDebtAmount)
                                .subscribe(new Subscriber<Boolean>() {

                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        lastActions();
                                    }
                                });
                    }
                }
            }
        }
    }

    private void takeMoreDebt() {
        String sum = numberFormatManager.prepareStringToParse(tvAmount.getText().toString());
        if (checkForFillSumField(sum)) {
            double amountDebt = Double.parseDouble(sum);
            double amountDebtCurrent = debt.getAmountCurrent();
            double amountDebtAll = debt.getAmountAll();

            int type = debt.getType();

            Account account = (Account) spinAccount.getSelectedItem();

            double amountAccount = account.getAmount();

            if (type == 0 && amountDebt > amountAccount) {
                toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
            } else {

                switch (type) {
                    case 0:
                        amountAccount -= amountDebt;
                        break;
                    case 1:
                        amountAccount += amountDebt;
                        break;
                }

                double newDebtCurrentAmount = amountDebtCurrent + amountDebt;
                double newDebtAllAmount = amountDebtAll + amountDebt;

                repository.takeMoreDebt(account.getId(), amountAccount,
                        debt.getId(), newDebtCurrentAmount, newDebtAllAmount)
                        .subscribe(new Subscriber<Boolean>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                lastActions();
                            }
                        });
            }
        }
    }

    private boolean checkForFillSumField(String s) {
        if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            toastManager.showClosableToast(getActivity(), getString(R.string.empty_amount_field), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private void lastActions() {
        pushBroadcast();
        this.finish();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
    }

    @OnClick({R.id.tvPayDebtAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPayDebtAmount:
                openNumericDialog(tvAmount.getText().toString());
                break;
        }
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    protected void handleSaveAction() {
        switch (mode) {
            case 1:
                payAllDebt();
                break;
            case 2:
                payPartDebt();
                break;
            case 3:
                takeMoreDebt();
                break;
        }
    }
}
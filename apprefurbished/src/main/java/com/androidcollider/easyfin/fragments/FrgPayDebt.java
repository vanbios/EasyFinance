package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class FrgPayDebt extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private View view;
    private TextView tvDebtName, tvAmount;
    private Spinner spinAccount;
    private Debt debt;
    private List<Account> accountsAvailableList;
    private int mode;

    @Inject
    Repository repository;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_pay_debt, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        mode = getArguments().getInt("mode", 0);
        debt = (Debt) getArguments().getSerializable("debt");

        setToolbar();
        fillAvailableAccountsList();

        CardView cardView = (CardView) view.findViewById(R.id.cardPayDebtElements);

        if (accountsAvailableList.isEmpty()) {
            cardView.setVisibility(View.GONE);
            showDialogNoAccount();
        } else {
            cardView.setVisibility(View.VISIBLE);
            initializeView();
            setViews();
            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(view.findViewById(R.id.layoutActPayDebtParent), getActivity());
        }

        return view;
    }

    private void initializeView() {
        tvDebtName = (TextView) view.findViewById(R.id.tvPayDebtName);
        tvAmount = (TextView) view.findViewById(R.id.tvPayDebtAmount);
        tvAmount.setOnClickListener(v -> openNumericDialog());
        spinAccount = (Spinner) view.findViewById(R.id.spinPayDebtAccount);
    }


    private void setViews() {
        tvDebtName.setText(debt.getName());
        if (mode == 1 || mode == 2) {

            final int PRECISE = 100;
            final String FORMAT = "###,##0.00";

            String amount = DoubleFormatUtils.doubleToStringFormatterForEdit(debt.getAmountCurrent(), FORMAT, PRECISE);
            setTVTextSize(amount);
            tvAmount.setText(amount);
        } else {
            tvAmount.setText("0,00");
            openNumericDialog();
        }
        if (mode == 1) tvAmount.setClickable(false);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountsAvailableList));

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
        //List<Account> accountList = InMemoryRepository.getInstance().getAccountList();
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
        int idDebt = debt.getId();
        double amountDebt = debt.getAmountCurrent();
        int type = debt.getType();

        Account account = (Account) spinAccount.getSelectedItem();

        int idAccount = account.getId();
        double amountAccount = account.getAmount();

        if (type == 1) {
            amountAccount -= amountDebt;
        } else {
            amountAccount += amountDebt;
        }

        //InMemoryRepository.getInstance().getDataSource().payAllDebt(idAccount, amountAccount, idDebt);

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
    }

    private void payPartDebt() {
        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
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
                        //InMemoryRepository.getInstance().getDataSource().payAllDebt(idAccount, amountAccount, idDebt);
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
                        //InMemoryRepository.getInstance().getDataSource().payPartDebt(idAccount, amountAccount, idDebt, newDebtAmount);
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
        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
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
                int idDebt = debt.getId();
                int idAccount = account.getId();

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

                /*InMemoryRepository.getInstance().getDataSource().takeMoreDebt(idAccount, amountAccount,
                        idDebt, newDebtCurrentAmount, newDebtAllAmount);*/

                repository.takeMoreDebt(idAccount, amountAccount,
                        idDebt, newDebtCurrentAmount, newDebtAllAmount)
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
        //InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
        this.finish();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
    }

    private void showDialogNoAccount() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.debt_no_available_accounts_warning))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .onPositive((dialog, which) -> goToAddAccount())
                .onNegative((dialog, which) -> finish())
                .cancelable(false)
                .show();
    }

    private void goToAddAccount() {
        finish();
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }

    private void setToolbar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            ViewGroup actionBarLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(
                    R.layout.save_close_buttons_toolbar, null);

            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarLayout, layoutParams);

            Toolbar parent = (Toolbar) actionBarLayout.getParent();
            parent.setContentInsetsAbsolute(0, 0);

            Button btnSave = (Button) actionBarLayout.findViewById(R.id.btnToolbarSave);
            Button btnClose = (Button) actionBarLayout.findViewById(R.id.btnToolbarClose);

            btnSave.setOnClickListener(v -> {
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
            });

            btnClose.setOnClickListener(v -> finish());
        }
    }

    private void openNumericDialog() {
        Bundle args = new Bundle();
        args.putString("value", tvAmount.getText().toString());

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 5);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog5");
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(amount);
        tvAmount.setText(amount);
    }

    private void setTVTextSize(String s) {
        int length = s.length();
        if (length > 10 && length <= 15)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        else if (length > 15)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        else
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
    }
}
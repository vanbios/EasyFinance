package com.androidcollider.easyfin.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Debt;
import com.androidcollider.easyfin.repository.Repository;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * @author Ihor Bilous
 */

public class FrgAddDebt extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    @BindView(R.id.tvAddDebtDate)
    TextView tvDate;
    @BindView(R.id.tvAddDebtAmount)
    TextView tvAmount;
    @BindView(R.id.editTextDebtName)
    EditText etName;
    @BindView(R.id.spinAddDebtAccount)
    Spinner spinAccount;
    @BindView(R.id.cardAddDebtElements)
    CardView cardView;
    @BindView(R.id.layoutActAddDebtParent)
    RelativeLayout mainContent;

    private DatePickerDialog datePickerDialog;
    private final String DATEFORMAT = "dd MMMM yyyy";
    private List<Account> accountList;
    private int mode, debtType;
    private Debt debtFrIntent;

    @Inject
    Repository repository;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;


    @Override
    public int getContentView() {
        return R.layout.frg_add_debt;
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
        if (mode == 1) debtFrIntent = (Debt) getArguments().getSerializable("debt");
        else debtType = getArguments().getInt("type", 0);

        setToolbar();

        accountList = new ArrayList<>();
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
                        FrgAddDebt.this.accountList.clear();
                        FrgAddDebt.this.accountList.addAll(accountList);

                        if (accountList.isEmpty()) {
                            cardView.setVisibility(View.GONE);
                            showDialogNoAccount(getString(R.string.dialog_text_debt_no_account), true);
                        } else {
                            cardView.setVisibility(View.VISIBLE);
                            initializeFields();
                            setDateTimeField();
                            setSpinner();
                            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(mainContent, getActivity());

                            if (mode == 1) setViewsToEdit();

                            tvAmount.setTextColor(ContextCompat.getColor(getContext(), debtType == 1 ? R.color.custom_red : R.color.custom_green));
                        }
                    }
                });
    }

    private void initializeFields() {
        if (mode == 0) {
            tvAmount.setText("0,00");
            openNumericDialog(tvAmount.getText().toString());
        }
    }

    private void setViewsToEdit() {
        etName.setText(debtFrIntent.getName());
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";

        String amount = numberFormatManager.doubleToStringFormatterForEdit(debtFrIntent.getAmountCurrent(), FORMAT, PRECISE);
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);

        debtType = debtFrIntent.getType();
    }

    private void setSpinner() {
        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                numberFormatManager,
                resourcesManager
        ));

        if (mode == 1) {
            int pos = 0;
            for (int i = 0; i < accountList.size(); i++) {
                if (debtFrIntent.getIdAccount() == accountList.get(i).getId()) {
                    pos = i;
                    break;
                }
            }

            spinAccount.setSelection(pos);
        }
    }

    private void addDebt() {
        if (checkForFillNameField()) {
            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();
            double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));

            if (checkIsEnoughCosts(debtType, amount, accountAmount)) {
                switch (debtType) {
                    case 0:
                        accountAmount -= amount;
                        break;
                    case 1:
                        accountAmount += amount;
                        break;
                }

                Debt debt = Debt.builder()
                        .name(etName.getText().toString())
                        .amountCurrent(amount)
                        .type(debtType)
                        .idAccount(account.getId())
                        .date(dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime())
                        .accountAmount(accountAmount)
                        .currency(account.getCurrency())
                        .accountName(account.getName())
                        .amountAll(amount)
                        .build();

                repository.addNewDebt(debt)
                        .subscribe(new Subscriber<Debt>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Debt debt) {
                                lastActions();
                            }
                        });
            }
        }
    }

    private void editDebt() {
        if (checkForFillNameField()) {
            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();
            int type = debtType;
            double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));

            int accountId = account.getId();
            int oldAccountId = debtFrIntent.getIdAccount();

            boolean isAccountsTheSame = accountId == oldAccountId;

            double oldAmount = debtFrIntent.getAmountCurrent();
            double oldAccountAmount = 0;
            int oldType = debtFrIntent.getType();

            if (isAccountsTheSame) {
                switch (oldType) {
                    case 0:
                        accountAmount += oldAmount;
                        break;
                    case 1:
                        accountAmount -= oldAmount;
                        break;
                }
            } else {
                for (int i = 0; i < accountList.size(); i++) {
                    if (oldAccountId == accountList.get(i).getId()) {
                        oldAccountAmount = accountList.get(i).getAmount();
                        break;
                    }
                }

                switch (oldType) {
                    case 0:
                        oldAccountAmount += oldAmount;
                        break;
                    case 1:
                        oldAccountAmount -= oldAmount;
                        break;
                }
            }

            if (checkIsEnoughCosts(type, amount, accountAmount)) {
                switch (type) {
                    case 0:
                        accountAmount -= amount;
                        break;
                    case 1:
                        accountAmount += amount;
                        break;
                }

                Debt debt = Debt.builder()
                        .name(etName.getText().toString())
                        .amountCurrent(amount)
                        .type(type)
                        .idAccount(accountId)
                        .date(dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime())
                        .accountAmount(accountAmount)
                        .id(debtFrIntent.getId())
                        .currency(account.getCurrency())
                        .accountName(account.getName())
                        .amountAll(amount)
                        .build();

                if (isAccountsTheSame) {
                    repository.updateDebt(debt)
                            .subscribe(new Subscriber<Debt>() {

                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Debt debt) {
                                    lastActions();
                                }
                            });
                } else {
                    repository.updateDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId)
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

    private boolean checkIsEnoughCosts(int type, double amount, double accountAmount) {
        if (type == 0 && Math.abs(amount) > accountAmount) {
            toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
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

    private boolean checkForFillNameField() {
        if (etName.getText().toString().replaceAll("\\s+", "").isEmpty()) {
            shakeEditTextManager.highlightEditText(etName);
            toastManager.showClosableToast(getActivity(), getString(R.string.empty_name_field), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private void setDateTimeField() {
        final Calendar newCalendar = Calendar.getInstance();
        final long initTime = newCalendar.getTimeInMillis();
        if (mode == 1)
            newCalendar.setTime(new Date(debtFrIntent.getDate()));
        tvDate.setText(dateFormatManager.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            if (newDate.getTimeInMillis() < initTime) {
                toastManager.showClosableToast(getActivity(), getString(R.string.debt_deadline_past), ToastManager.SHORT);
            } else {
                tvDate.setText(dateFormatManager.dateToString(newDate.getTime(), DATEFORMAT));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @OnClick({R.id.tvAddDebtAmount, R.id.tvAddDebtDate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddDebtAmount:
                openNumericDialog(tvAmount.getText().toString());
                break;
            case R.id.tvAddDebtDate:
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    void handleSaveAction() {
        switch (mode) {
            case 0:
                addDebt();
                break;
            case 1:
                editDebt();
                break;
        }
    }
}
package com.androidcollider.easyfin.fragments;

import android.app.DatePickerDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Transaction;
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

public class FrgAddTransactionDefault extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    @BindView(R.id.tvTransactionDate)
    TextView tvDate;
    @BindView(R.id.tvAddTransDefAmount)
    TextView tvAmount;
    @BindView(R.id.spinAddTransCategory)
    Spinner spinCategory;
    @BindView(R.id.spinAddTransDefAccount)
    Spinner spinAccount;
    @BindView(R.id.scrollAddTransDef)
    ScrollView scrollView;

    private DatePickerDialog datePickerDialog;
    private final String DATEFORMAT = "dd MMMM yyyy";
    private List<Account> accountList;
    private int mode, transType;
    private Transaction transFromIntent;

    @Inject
    Repository repository;

    @Inject
    ToastManager toastManager;

    @Inject
    DateFormatManager dateFormatManager;

    @Inject
    NumberFormatManager numberFormatManager;

    @Inject
    ResourcesManager resourcesManager;


    @Override
    public int getContentView() {
        return R.layout.frg_add_trans_def;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                        FrgAddTransactionDefault.this.accountList.clear();
                        FrgAddTransactionDefault.this.accountList.addAll(accountList);

                        setupUI();
                    }
                });
    }

    private void setupUI() {
        if (accountList.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            showDialogNoAccount(getString(R.string.dialog_text_transaction_no_account), false);
        } else {
            scrollView.setVisibility(View.VISIBLE);
            mode = getArguments().getInt("mode", 0);

            switch (mode) {
                case 0: {
                    transType = getArguments().getInt("type", 0);
                    setAmountValue("0,00");
                    openNumericDialog(tvAmount.getText().toString());
                    break;
                }
                case 1: {
                    transFromIntent = (Transaction) getArguments().getSerializable("transaction");
                    final int PRECISE = 100;
                    final String FORMAT = "###,##0.00";

                    if (transFromIntent != null) {
                        double amount = transFromIntent.getAmount();
                        transType = numberFormatManager.isDoubleNegative(amount) ? 0 : 1;
                        setAmountValue(numberFormatManager.doubleToStringFormatterForEdit(
                                transType == 1 ? amount : Math.abs(amount), FORMAT, PRECISE));
                    }
                    break;
                }
            }

            setDateTimeField();
            setSpinner();

            tvAmount.setTextColor(ContextCompat.getColor(getActivity(), transType == 1 ? R.color.custom_green : R.color.custom_red));
        }
    }

    private void setSpinner() {
        String[] categoryArray = resourcesManager.getStringArray(
                transType == 1 ?
                        ResourcesManager.STRING_TRANSACTION_CATEGORY_INCOME :
                        ResourcesManager.STRING_TRANSACTION_CATEGORY_EXPENSE
        );
        TypedArray categoryIcons = resourcesManager.getIconArray(
                transType == 1 ?
                        ResourcesManager.ICON_TRANSACTION_CATEGORY_INCOME :
                        ResourcesManager.ICON_TRANSACTION_CATEGORY_EXPENSE)
                ;

        spinCategory.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                R.id.tvSpinHeadIconText,
                R.id.ivSpinHeadIconText,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                categoryArray,
                categoryIcons));

        spinCategory.setSelection(categoryArray.length - 1);

        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                numberFormatManager,
                resourcesManager
        ));

        if (mode == 1) {
            String accountName = transFromIntent.getAccountName();
            for (int i = 0; i < accountList.size(); i++) {
                if (accountList.get(i).getName().equals(accountName)) {
                    spinAccount.setSelection(i);
                    break;
                }
            }

            spinCategory.setSelection(transFromIntent.getCategory());
        }
    }

    public void addTransaction() {
        String sum = numberFormatManager.prepareStringToParse(tvAmount.getText().toString());
        if (checkSumField(sum)) {
            double amount = Double.parseDouble(sum);
            boolean isExpense = transType == 0;
            if (isExpense) amount *= -1;

            Account account = (Account) spinAccount.getSelectedItem();

            double accountAmount = account.getAmount();

            if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                accountAmount += amount;

                Transaction transaction = Transaction.builder()
                        .date(dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime())
                        .amount(amount)
                        .category(spinCategory.getSelectedItemPosition())
                        .idAccount(account.getId())
                        .accountAmount(accountAmount)
                        .accountName(account.getName())
                        .accountType(account.getType())
                        .currency(account.getCurrency())
                        .build();

                repository.addNewTransaction(transaction)
                        .subscribe(new Subscriber<Transaction>() {

                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Transaction transaction) {
                                lastActions();
                            }
                        });
            }
        }
    }

    private void editTransaction() {
        String sum = numberFormatManager.prepareStringToParse(tvAmount.getText().toString());
        if (checkSumField(sum)) {
            double amount = Double.parseDouble(sum);
            boolean isExpense = transType == 0;
            if (isExpense) amount *= -1;

            Account account = (Account) spinAccount.getSelectedItem();
            double accountAmount = account.getAmount();
            int accountId = account.getId();

            int oldAccountId = transFromIntent.getIdAccount();
            boolean isAccountTheSame = accountId == oldAccountId;
            double oldAmount = transFromIntent.getAmount();
            double oldAccountAmount = 0;

            if (isAccountTheSame) accountAmount -= oldAmount;
            else {
                for (Account account1 : accountList) {
                    if (oldAccountId == account1.getId()) {
                        oldAccountAmount = account1.getAmount() - oldAmount;
                        break;
                    }
                }
            }

            if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                accountAmount += amount;

                Transaction transaction = Transaction.builder()
                        .date(dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime())
                        .amount(amount)
                        .category(spinCategory.getSelectedItemPosition())
                        .idAccount(account.getId())
                        .accountAmount(accountAmount)
                        .id(transFromIntent.getId())
                        .currency(account.getCurrency())
                        .accountType(account.getType())
                        .accountName(account.getName())
                        .build();

                if (isAccountTheSame) {
                    repository.updateTransaction(transaction)
                            .subscribe(new Subscriber<Transaction>() {

                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(Transaction transaction) {
                                    lastActions();
                                }
                            });
                } else {
                    repository.updateTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId)
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

    private boolean checkSumField(String sum) {
        if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
            toastManager.showClosableToast(getActivity(), getString(R.string.empty_amount_field), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private boolean checkIsEnoughCosts(boolean isExpense, double amount, double accountAmount) {
        if (isExpense && Math.abs(amount) > accountAmount) {
            toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private void lastActions() {
        pushBroadcast();
        finish();
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        if (mode == 1) {
            newCalendar.setTime(new Date(transFromIntent.getDate()));
        }
        tvDate.setText(dateFormatManager.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            if (newDate.getTimeInMillis() > System.currentTimeMillis()) {
                toastManager.showClosableToast(getActivity(), getString(R.string.transaction_date_future), ToastManager.SHORT);
            } else {
                tvDate.setText(dateFormatManager.dateToString(newDate.getTime(), DATEFORMAT));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @OnClick({R.id.tvTransactionDate, R.id.tvAddTransDefAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTransactionDate:
                datePickerDialog.show();
                break;
            case R.id.tvAddTransDefAmount:
                openNumericDialog(tvAmount.getText().toString());
                break;
        }
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setAmountValue(amount);
    }

    @Override
    void handleSaveAction() {
        switch (mode) {
            case 0:
                addTransaction();
                break;
            case 1:
                editTransaction();
                break;
        }
    }

    private void setAmountValue(String amount) {
        setTVTextSize(tvAmount, amount, 9, 14);
        tvAmount.setText(String.format("%1$s %2$s", transType == 1 ? "+" : "-", amount));
    }
}
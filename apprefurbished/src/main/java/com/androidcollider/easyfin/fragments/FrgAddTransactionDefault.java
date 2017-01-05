package com.androidcollider.easyfin.fragments;

import android.app.DatePickerDialog;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.events.UpdateFrgHome;
import com.androidcollider.easyfin.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.models.Transaction;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.repository.memory.InMemoryRepository;
import com.androidcollider.easyfin.utils.DateFormatUtils;
import com.androidcollider.easyfin.utils.DoubleFormatUtils;
import com.androidcollider.easyfin.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscriber;

public class FrgAddTransactionDefault extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private TextView tvDate, tvAmount;
    private DatePickerDialog datePickerDialog;
    private Spinner spinCategory, spinAccount;
    private final String DATEFORMAT = "dd MMMM yyyy";
    private View view;
    private ArrayList<Account> accountList = null;
    private int mode, transType;
    private Transaction transFromIntent;
    private final String prefixExpense = "-", prefixIncome = "+";

    @Inject
    Repository repository;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_trans_def, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        setToolbar();

        accountList = InMemoryRepository.getInstance().getAccountList();

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollAddTransDef);

        if (accountList.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            showDialogNoAccount();
        } else {
            scrollView.setVisibility(View.VISIBLE);
            mode = getArguments().getInt("mode", 0);
            tvAmount = (TextView) view.findViewById(R.id.tvAddTransDefAmount);
            tvAmount.setOnClickListener(v -> openNumericDialog());

            switch (mode) {
                case 0: {
                    transType = getArguments().getInt("type", 0);
                    switch (transType) {
                        case 0:
                            tvAmount.setText(String.format("%1$s %2$s", prefixExpense, "0,00"));
                            break;
                        case 1:
                            tvAmount.setText(String.format("%1$s %2$s", prefixIncome, "0,00"));
                            break;
                    }
                    openNumericDialog();
                    break;
                }
                case 1: {
                    transFromIntent = (Transaction) getArguments().getSerializable("transaction");
                    final int PRECISE = 100;
                    final String FORMAT = "###,##0.00";

                    if (transFromIntent != null) {
                        double amount = transFromIntent.getAmount();
                        if (!DoubleFormatUtils.isDoubleNegative(amount)) {
                            transType = 1;
                            String amountS = DoubleFormatUtils.doubleToStringFormatterForEdit(amount, FORMAT, PRECISE);
                            setTVTextSize(amountS);
                            tvAmount.setText(String.format("%1$s %2$s", prefixIncome, amountS));
                        } else {
                            transType = 0;
                            String amountS = DoubleFormatUtils.doubleToStringFormatterForEdit(Math.abs(amount), FORMAT, PRECISE);
                            setTVTextSize(amountS);
                            tvAmount.setText(String.format("%1$s %2$s", prefixExpense, amountS));
                        }
                    }
                    break;
                }
            }

            tvDate = (TextView) view.findViewById(R.id.tvTransactionDate);
            setDateTimeField();
            setSpinner();

            switch (transType) {
                case 0:
                    tvAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_red));
                    break;
                case 1:
                    tvAmount.setTextColor(ContextCompat.getColor(getActivity(), R.color.custom_green));
                    break;
            }
        }

        return view;
    }

    private void setSpinner() {
        spinCategory = (Spinner) view.findViewById(R.id.spinAddTransCategory);
        spinAccount = (Spinner) view.findViewById(R.id.spinAddTransDefAccount);

        String[] categoryArray = getResources().getStringArray(
                transType == 1 ?
                        R.array.transaction_category_income_array :
                        R.array.transaction_category_expense_array);
        TypedArray categoryIcons = getResources().obtainTypedArray(
                transType == 1 ?
                        R.array.transaction_category_income_icons :
                        R.array.transaction_category_expense_icons);

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
                getActivity(), R.layout.spin_head_icon_text, accountList));

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
        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
        if (checkSumField(sum)) {
            double amount = Double.parseDouble(sum);
            boolean isExpense = transType == 0;
            if (isExpense) amount *= -1;

            Account account = (Account) spinAccount.getSelectedItem();

            double accountAmount = account.getAmount();

            if (checkIsEnoughCosts(isExpense, amount, accountAmount)) {
                accountAmount += amount;

                int category = spinCategory.getSelectedItemPosition();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();
                int idAccount = account.getId();

                Transaction transaction = Transaction.builder()
                        .date(date)
                        .amount(amount)
                        .category(category)
                        .idAccount(idAccount)
                        .accountAmount(accountAmount)
                        .accountName(account.getName())
                        .accountType(account.getType())
                        .currency(account.getCurrency())
                        .build();
                //InMemoryRepository.getInstance().getDataSource().insertNewTransaction(transaction);
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
        String sum = DoubleFormatUtils.prepareStringToParse(tvAmount.getText().toString());
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

                int category = spinCategory.getSelectedItemPosition();
                Long date = DateFormatUtils.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();
                int idAccount = account.getId();

                int idTrans = transFromIntent.getId();

                Transaction transaction = Transaction.builder()
                        .date(date)
                        .amount(amount)
                        .category(category)
                        .idAccount(idAccount)
                        .accountAmount(accountAmount)
                        .id(idTrans)
                        .currency(account.getCurrency())
                        .accountType(account.getType())
                        .accountName(account.getName())
                        .build();

                if (isAccountTheSame) {
                    //InMemoryRepository.getInstance().getDataSource().editTransaction(transaction);
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
                    //InMemoryRepository.getInstance().getDataSource().editTransactionDifferentAccounts(transaction, oldAccountAmount, oldAccountId);
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
            ToastUtils.showClosableToast(getActivity(), getString(R.string.empty_amount_field), 1);
            return false;
        }
        return true;
    }

    private boolean checkIsEnoughCosts(boolean isExpense, double amount, double accountAmount) {
        if (isExpense && Math.abs(amount) > accountAmount) {
            ToastUtils.showClosableToast(getActivity(), getString(R.string.not_enough_costs), 1);
            return false;
        }
        return true;
    }

    private void lastActions() {
        InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
        finish();
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(v -> datePickerDialog.show());
        Calendar newCalendar = Calendar.getInstance();
        if (mode == 1) {
            newCalendar.setTime(new Date(transFromIntent.getDate()));
        }
        tvDate.setText(DateFormatUtils.dateToString(newCalendar.getTime(), DATEFORMAT));

        datePickerDialog = new DatePickerDialog(getActivity(), (view1, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            if (newDate.getTimeInMillis() > System.currentTimeMillis()) {
                ToastUtils.showClosableToast(getActivity(), getString(R.string.transaction_date_future), 1);
            } else {
                tvDate.setText(DateFormatUtils.dateToString(newDate.getTime(), DATEFORMAT));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
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
                    case 0:
                        addTransaction();
                        break;
                    case 1:
                        editTransaction();
                        break;
                }
            });

            btnClose.setOnClickListener(v -> finish());
        }
    }

    private void showDialogNoAccount() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_transaction_no_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .onPositive((dialog, which) -> goToAddAccount())
                .onNegative((dialog, which) -> finish())
                .cancelable(false)
                .show();
    }

    private void goToAddAccount() {
        FrgAddAccount frgAddAccount = new FrgAddAccount();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        frgAddAccount.setArguments(arguments);

        addFragment(frgAddAccount);
    }

    private void openNumericDialog() {
        Bundle args = new Bundle();
        args.putString("value", tvAmount.getText().toString());

        DialogFragment numericDialog = new FrgNumericDialog();
        numericDialog.setTargetFragment(this, 2);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog2");
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(amount);
        switch (transType) {
            case 0:
                tvAmount.setText(String.format("%1$s %2$s", prefixExpense, amount));
                break;
            case 1:
                tvAmount.setText(String.format("%1$s %2$s", prefixIncome, amount));
                break;
        }
    }

    private void setTVTextSize(String s) {
        int length = s.length();
        if (length > 9 && length <= 14)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        else if (length > 14)
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        else
            tvAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
    }
}
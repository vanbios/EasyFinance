package com.androidcollider.easyfin.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransHeadIconAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
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

import rx.Subscriber;

public class FrgAddDebt extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private View view;
    private DatePickerDialog datePickerDialog;
    private TextView tvDate, tvAmount;
    private EditText etName;
    private Spinner spinAccount;
    private final String DATEFORMAT = "dd MMMM yyyy";
    private ArrayList<Account> accountList = null;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_debt, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        mode = getArguments().getInt("mode", 0);
        if (mode == 1) debtFrIntent = (Debt) getArguments().getSerializable("debt");
        else debtType = getArguments().getInt("type", 0);

        setToolbar();

        CardView cardView = (CardView) view.findViewById(R.id.cardAddDebtElements);

        //accountList = InMemoryRepository.getInstance().getAccountList();
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
                            showDialogNoAccount();
                        } else {
                            cardView.setVisibility(View.VISIBLE);
                            initializeFields();
                            setDateTimeField();
                            setSpinner();
                            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(view.findViewById(R.id.layoutActAddDebtParent), getActivity());

                            if (mode == 1) setViewsToEdit();

                            switch (debtType) {
                                case 0:
                                    tvAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.custom_green));
                                    break;
                                case 1:
                                    tvAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.custom_red));
                                    break;
                            }
                        }
                    }
                });

        return view;
    }


    private void initializeFields() {
        etName = (EditText) view.findViewById(R.id.editTextDebtName);
        tvDate = (TextView) view.findViewById(R.id.tvAddDebtDate);
        tvAmount = (TextView) view.findViewById(R.id.tvAddDebtAmount);

        if (mode == 0) {
            tvAmount.setText("0,00");
            openNumericDialog();
        }

        tvAmount.setOnClickListener(v -> openNumericDialog());
    }

    private void setViewsToEdit() {
        etName.setText(debtFrIntent.getName());
        etName.setSelection(etName.getText().length());

        final int PRECISE = 100;
        final String FORMAT = "###,##0.00";

        String amount = numberFormatManager.doubleToStringFormatterForEdit(debtFrIntent.getAmountCurrent(), FORMAT, PRECISE);
        setTVTextSize(amount);
        tvAmount.setText(amount);

        debtType = debtFrIntent.getType();
    }

    private void setSpinner() {
        spinAccount = (Spinner) view.findViewById(R.id.spinAddDebtAccount);
        spinAccount.setAdapter(new SpinAccountForTransHeadIconAdapter(
                getActivity(),
                R.layout.spin_head_icon_text,
                accountList,
                numberFormatManager
        ));

        if (mode == 1) {
            int idAccount = debtFrIntent.getIdAccount();
            int pos = 0;
            for (int i = 0; i < accountList.size(); i++) {
                if (idAccount == accountList.get(i).getId()) {
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

            int type = debtType;

            double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));

            if (checkIsEnoughCosts(type, amount, accountAmount)) {
                String name = etName.getText().toString();
                int accountId = account.getId();
                Long date = dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type) {
                    case 0:
                        accountAmount -= amount;
                        break;
                    case 1:
                        accountAmount += amount;
                        break;
                }

                Debt debt = Debt.builder()
                        .name(name)
                        .amountCurrent(amount)
                        .type(type)
                        .idAccount(accountId)
                        .date(date)
                        .accountAmount(accountAmount)
                        .build();
                //InMemoryRepository.getInstance().getDataSource().insertNewDebt(debt);
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
                String name = etName.getText().toString();
                Long date = dateFormatManager.stringToDate(tvDate.getText().toString(), DATEFORMAT).getTime();

                switch (type) {
                    case 0:
                        accountAmount -= amount;
                        break;
                    case 1:
                        accountAmount += amount;
                        break;
                }

                int idDebt = debtFrIntent.getId();

                //Debt debt = new Debt(name, amount, type, accountId, date, accountAmount, idDebt);
                Debt debt = Debt.builder()
                        .name(name)
                        .amountCurrent(amount)
                        .type(type)
                        .idAccount(accountId)
                        .date(date)
                        .accountAmount(accountAmount)
                        .id(idDebt)
                        .build();

                if (isAccountsTheSame) {
                    //InMemoryRepository.getInstance().getDataSource().editDebt(debt);
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
                    //InMemoryRepository.getInstance().getDataSource().editDebtDifferentAccounts(debt, oldAccountAmount, oldAccountId);
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
        //InMemoryRepository.getInstance().updateAccountList();
        pushBroadcast();
        this.finish();
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
    }

    private boolean checkForFillNameField() {
        String st = etName.getText().toString().replaceAll("\\s+", "");
        if (st.isEmpty()) {
            shakeEditTextManager.highlightEditText(etName);
            toastManager.showClosableToast(getActivity(), getString(R.string.empty_name_field), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    private void setDateTimeField() {
        tvDate.setOnClickListener(v -> datePickerDialog.show());
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

    private void showDialogNoAccount() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.no_account))
                .content(getString(R.string.dialog_text_debt_no_account))
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
                    case 0:
                        addDebt();
                        break;
                    case 1:
                        editDebt();
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
        numericDialog.setTargetFragment(this, 4);
        numericDialog.setArguments(args);
        numericDialog.show(getActivity().getSupportFragmentManager(), "numericDialog4");
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
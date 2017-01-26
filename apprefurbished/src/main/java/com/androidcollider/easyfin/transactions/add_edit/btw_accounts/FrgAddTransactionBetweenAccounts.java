package com.androidcollider.easyfin.transactions.add_edit.btw_accounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.models.Account;
import com.androidcollider.easyfin.common.repository.Repository;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransAdapter;
import com.androidcollider.easyfin.common.ui.fragments.FrgNumericDialog;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.common.utils.EditTextAmountWatcher;

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

public class FrgAddTransactionBetweenAccounts extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    @BindView(R.id.spinAddTransBTWAccountFrom)
    Spinner spinAccountFrom;
    @BindView(R.id.spinAddTransBTWAccountTo)
    Spinner spinAccountTo;
    @BindView(R.id.editTextTransBTWExchange)
    EditText etExchange;
    @BindView(R.id.tvAddTransBTWAmount)
    TextView tvAmount;
    @BindView(R.id.layoutAddTransBTWExchange)
    RelativeLayout layoutExchange;
    @BindView(R.id.scrollAddTransBTW)
    ScrollView scrollView;

    private SpinAccountForTransAdapter adapterAccountTo;
    private List<Account> accountListFrom, accountListTo;

    @Inject
    Repository repository;

    @Inject
    ExchangeManager exchangeManager;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

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
        return R.layout.frg_add_trans_btw;
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

        accountListFrom = new ArrayList<>();
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
                        accountListFrom.clear();
                        accountListFrom.addAll(accountList);

                        if (accountListFrom.size() < 2) {
                            scrollView.setVisibility(View.GONE);
                            showDialogNoAccount(getString(R.string.dialog_text_transfer_no_accounts), false);
                        } else {
                            scrollView.setVisibility(View.VISIBLE);
                            tvAmount.setText("0,00");
                            openNumericDialog(tvAmount.getText().toString());
                            etExchange.addTextChangedListener(new EditTextAmountWatcher(etExchange));
                            setSpinners();
                            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(scrollView, getActivity());
                        }
                    }
                });
    }

    private void setSpinners() {
        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinAccountForTransAdapter(
                getActivity(),
                R.layout.spin_head_text,
                accountListFrom,
                numberFormatManager,
                resourcesManager
        ));

        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo = new SpinAccountForTransAdapter(
                getActivity(),
                R.layout.spin_head_text,
                accountListTo,
                numberFormatManager,
                resourcesManager
        );

        spinAccountTo.setAdapter(adapterAccountTo);

        spinAccountFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSpinnerTo();
                setCurrencyMode(checkForMultiCurrency());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinAccountTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrencyMode(checkForMultiCurrency());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void updateSpinnerTo() {
        accountListTo.clear();
        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());
        adapterAccountTo.notifyDataSetChanged();
    }

    private void setCurrencyMode(boolean mode) {
        if (mode) {
            layoutExchange.setVisibility(View.VISIBLE);

            etExchange.setText(numberFormatManager.doubleToStringFormatter(
                    exchangeManager.getExchangeRate(
                            ((Account) spinAccountFrom.getSelectedItem()).getCurrency(),
                            ((Account) spinAccountTo.getSelectedItem()).getCurrency()),
                    NumberFormatManager.FORMAT_3,
                    NumberFormatManager.PRECISE_2
            ));
            etExchange.setSelection(etExchange.getText().length());
        } else {
            layoutExchange.setVisibility(View.GONE);
        }
    }

    private boolean checkForMultiCurrency() {
        return !((Account) spinAccountFrom.getSelectedItem()).getCurrency()
                .equals(((Account) spinAccountTo.getSelectedItem()).getCurrency());
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    public void addTransactionBTW() {
        double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));

        Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
        double accountAmountFrom = accountFrom.getAmount();

        if (amount > accountAmountFrom) {
            toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
        } else {
            int accountIdFrom = accountFrom.getId();

            Account accountTo = (Account) spinAccountTo.getSelectedItem();

            int accountIdTo = accountTo.getId();
            double accountAmountTo = accountTo.getAmount();

            if (layoutExchange.getVisibility() == View.VISIBLE) {
                if (checkEditTextForCorrect(etExchange, R.string.empty_exchange_field)) {
                    double exchange = Double.parseDouble(numberFormatManager.prepareStringToParse(etExchange.getText().toString()));
                    double amountTo = amount / exchange;
                    lastActions(amount, amountTo, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
                }
            } else {
                lastActions(amount, amount, accountIdFrom, accountIdTo, accountAmountFrom, accountAmountTo);
            }
        }
    }

    private void lastActions(double amount, double amountTo,
                             int idFrom, int idTo,
                             double accAmountFrom, double accAmountTo) {
        double accountAmountFrom = accAmountFrom - amount;
        double accountAmountTo = accAmountTo + amountTo;

        repository.transferBTWAccounts(idFrom, accountAmountFrom, idTo, accountAmountTo)
                .subscribe(new Subscriber<Boolean>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        pushBroadcast();
                        finish();
                    }
                });
    }

    private boolean checkEditTextForCorrect(EditText et, int strRes) {
        String s = numberFormatManager.prepareStringToParse(et.getText().toString());
        if (!s.matches(".*\\d.*") || Double.parseDouble(s) == 0) {
            shakeEditTextManager.highlightEditText(et);
            toastManager.showClosableToast(getActivity(), getString(strRes), ToastManager.SHORT);
            return false;
        }
        return true;
    }

    @OnClick({R.id.tvAddTransBTWAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddTransBTWAmount:
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
    public void handleSaveAction() {
        addTransactionBTW();
    }
}
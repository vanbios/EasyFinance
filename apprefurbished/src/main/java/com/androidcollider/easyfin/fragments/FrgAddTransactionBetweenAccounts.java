package com.androidcollider.easyfin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinAccountForTransAdapter;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.managers.rates.exchange.ExchangeManager;
import com.androidcollider.easyfin.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.models.Account;
import com.androidcollider.easyfin.repository.Repository;
import com.androidcollider.easyfin.utils.EditTextAmountWatcher;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

public class FrgAddTransactionBetweenAccounts extends CommonFragmentAddEdit implements FrgNumericDialog.OnCommitAmountListener {

    private Spinner spinAccountFrom, spinAccountTo;
    private SpinAccountForTransAdapter adapterAccountTo;
    private View view;
    private EditText etExchange;
    private TextView tvAmount;
    private RelativeLayout layoutExchange;
    private List<Account> accountListFrom, accountListTo = null;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_trans_btw, container, false);
        ((App) getActivity().getApplication()).getComponent().inject(this);
        setToolbar();

        //accountListFrom = InMemoryRepository.getInstance().getAccountList();
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

                        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollAddTransBTW);

                        if (accountListFrom.size() < 2) {
                            scrollView.setVisibility(View.GONE);
                            showDialogNoAccount(getString(R.string.dialog_text_transfer_no_accounts), false);
                        } else {
                            scrollView.setVisibility(View.VISIBLE);

                            tvAmount = (TextView) view.findViewById(R.id.tvAddTransBTWAmount);
                            tvAmount.setText("0,00");
                            tvAmount.setOnClickListener(v -> openNumericDialog(tvAmount.getText().toString()));

                            openNumericDialog(tvAmount.getText().toString());

                            etExchange = (EditText) view.findViewById(R.id.editTextTransBTWExchange);
                            etExchange.addTextChangedListener(new EditTextAmountWatcher(etExchange));

                            layoutExchange = (RelativeLayout) view.findViewById(R.id.layoutAddTransBTWExchange);

                            setSpinners();
                            hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(view.findViewById(R.id.scrollAddTransBTW), getActivity());
                        }
                    }
                });

        return view;
    }

    private void setSpinners() {
        spinAccountFrom = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountFrom);
        spinAccountTo = (Spinner) view.findViewById(R.id.spinAddTransBTWAccountTo);

        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinAccountForTransAdapter(getActivity(),
                R.layout.spin_head_text, accountListFrom, numberFormatManager));

        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo = new SpinAccountForTransAdapter(getActivity(),
                R.layout.spin_head_text, accountListTo, numberFormatManager);

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

            Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
            String currFrom = accountFrom.getCurrency();
            Account accountTo = (Account) spinAccountTo.getSelectedItem();
            String currTo = accountTo.getCurrency();

            double exchangeRate = exchangeManager.getExchangeRate(currFrom, currTo);

            final int PRECISE = 100000;
            final String FORMAT = "#.#####";

            etExchange.setText(numberFormatManager.doubleToStringFormatter(exchangeRate, FORMAT, PRECISE));

            etExchange.setSelection(etExchange.getText().length());
        } else {
            layoutExchange.setVisibility(View.GONE);
        }
    }

    private boolean checkForMultiCurrency() {
        Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
        Account accountTo = (Account) spinAccountTo.getSelectedItem();
        return !accountFrom.getCurrency().equals(accountTo.getCurrency());
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    public void addTransactionBTW() {
        double amount = Double.parseDouble(numberFormatManager.prepareStringToParse(tvAmount.getText().toString()));

        Account accountFrom = (Account) spinAccountFrom.getSelectedItem();
        double accountAmountFrom = accountFrom.getAmount();

        if (amount > accountAmountFrom)
            toastManager.showClosableToast(getActivity(), getString(R.string.not_enough_costs), ToastManager.SHORT);
        else {
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
                        //InMemoryRepository.getInstance().updateAccountList();
                        pushBroadcast();
                        finish();
                    }
                });
        /*InMemoryRepository.getInstance().getDataSource().updateAccountsAmountAfterTransfer(idFrom,
                accountAmountFrom, idTo, accountAmountTo);*/
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

    @Override
    public void onCommitAmountSubmit(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    void handleSaveAction() {
        addTransactionBTW();
    }
}
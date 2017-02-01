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
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.ui.hide_touch_outside.HideTouchOutsideManager;
import com.androidcollider.easyfin.common.managers.ui.shake_edit_text.ShakeEditTextManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.ui.adapters.SpinAccountForTransAdapter;
import com.androidcollider.easyfin.common.ui.fragments.NumericDialogFragment;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentAddEdit;
import com.androidcollider.easyfin.common.utils.EditTextAmountWatcher;
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Ihor Bilous
 */

public class AddTransactionBetweenAccountsFragment extends CommonFragmentAddEdit
        implements NumericDialogFragment.OnCommitAmountListener, AddTransactionBetweenAccountsMVP.View {

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
    private List<SpinAccountViewModel> accountListFrom, accountListTo;

    @Inject
    ShakeEditTextManager shakeEditTextManager;

    @Inject
    ToastManager toastManager;

    @Inject
    HideTouchOutsideManager hideTouchOutsideManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    AddTransactionBetweenAccountsMVP.Presenter presenter;


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

        presenter.setView(this);
        presenter.loadAccounts();
    }

    private void setSpinners() {
        accountListTo = new ArrayList<>();

        spinAccountFrom.setAdapter(new SpinAccountForTransAdapter(
                getActivity(),
                R.layout.spin_head_text,
                accountListFrom,
                resourcesManager
        ));

        accountListTo.addAll(accountListFrom);
        accountListTo.remove(spinAccountFrom.getSelectedItemPosition());

        adapterAccountTo = new SpinAccountForTransAdapter(
                getActivity(),
                R.layout.spin_head_text,
                accountListTo,
                resourcesManager
        );

        spinAccountTo.setAdapter(adapterAccountTo);

        spinAccountFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSpinnerTo();
                presenter.setCurrencyMode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinAccountTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                presenter.setCurrencyMode();
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

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHomeBalance());
        EventBus.getDefault().post(new UpdateFrgAccounts());
    }

    @OnClick({R.id.tvAddTransBTWAmount})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAddTransBTWAmount:
                openNumericDialog();
                break;
        }
    }

    @Override
    public void onCommitAmountSubmit(String amount) {
        showAmount(amount);
    }


    @Override
    public void handleSaveAction() {
        presenter.save();
    }

    @Override
    public void showAmount(String amount) {
        setTVTextSize(tvAmount, amount, 10, 15);
        tvAmount.setText(amount);
    }

    @Override
    public void showExchangeRate(String rate) {
        layoutExchange.setVisibility(View.VISIBLE);
        etExchange.setText(rate);
        etExchange.setSelection(etExchange.getText().length());
    }

    @Override
    public void hideExchangeRate() {
        layoutExchange.setVisibility(View.GONE);
    }

    @Override
    public void highlightExchangeRateField() {
        shakeEditTextManager.highlightEditText(etExchange);
    }

    @Override
    public void showMessage(String message) {
        toastManager.showClosableToast(getActivity(), message, ToastManager.SHORT);
    }

    @Override
    public void openNumericDialog() {
        openNumericDialog(tvAmount.getText().toString());
    }

    @Override
    public void performLastActionsAfterSaveAndClose() {
        pushBroadcast();
        finish();
    }

    @Override
    public String getAmount() {
        return tvAmount.getText().toString();
    }

    @Override
    public String getExchangeRate() {
        return etExchange.getText().toString();
    }

    @Override
    public SpinAccountViewModel getAccountFrom() {
        return (SpinAccountViewModel) spinAccountFrom.getSelectedItem();
    }

    @Override
    public SpinAccountViewModel getAccountTo() {
        return (SpinAccountViewModel) spinAccountTo.getSelectedItem();
    }

    @Override
    public boolean isMultiCurrencyTransaction() {
        return layoutExchange.getVisibility() == View.VISIBLE;
    }

    @Override
    public void notifyNotEnoughAccounts() {
        scrollView.setVisibility(View.GONE);
        showDialogNoAccount(getString(R.string.dialog_text_transfer_no_accounts), false);
    }

    @Override
    public void setAccounts(List<SpinAccountViewModel> accountList) {
        accountListFrom.clear();
        accountListFrom.addAll(accountList);

        scrollView.setVisibility(View.VISIBLE);
        showAmount("0,00");
        openNumericDialog();
        etExchange.addTextChangedListener(new EditTextAmountWatcher(etExchange));
        setSpinners();
        hideTouchOutsideManager.hideKeyboardByTouchOutsideEditText(scrollView, getActivity());
    }
}
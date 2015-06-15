package com.androidcollider.easyfin.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.ActAccount;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.SpinnerAccountForTransAdapter;
import com.androidcollider.easyfin.database.DataSource;
import com.androidcollider.easyfin.objects.Account;
import com.androidcollider.easyfin.utils.Shake;

import java.util.ArrayList;
import java.util.List;

public class FrgAddTransactionBetweenAccounts extends Fragment {

    private Spinner spinAccount1, spinAccount2;

    private View view;
    private DataSource dataSource;

    private List<Account> accountList1 = null;

    int spin2SetCount = 0;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frg_add_transaction_between_accounts, container, false);

        dataSource = new DataSource(getActivity());

        setSpinnerAccount1();

        return view;
    }




    private void setSpinnerAccount1() {
        spinAccount1 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount1);

        List<String> accounts1 = new ArrayList<>();

        accountList1 = dataSource.getAllAccountsInfo();

        for (Account a : accountList1) {
            accounts1.add(a.getName());
        }

        spinAccount1.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                accounts1, accountList1));

        if (accounts1.size() == 0) {
            spinAccount1.setEnabled(false);
        }

        setSpinnerAccount2();


        spinAccount1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!setSpinnerAccount2() && spin2SetCount > 2) {
                    showNoAvailableAccountsDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean setSpinnerAccount2() {

        spinAccount2 = (Spinner) view.findViewById(R.id.spinAddTransBTWAccount2);
        spinAccount2.setEnabled(false);
        spinAccount2.setAdapter(null);
        spin2SetCount++;

        if (accountList1.size() > 1) {

            int pos = spinAccount1.getSelectedItemPosition();

            int idAccount1 = accountList1.get(pos).getId();
            String currency = accountList1.get(pos).getCurrency();


            List<Account> accountForTransferList = new ArrayList<>();
            List<String> accountsAvailable = new ArrayList<>();


            for (Account account : accountList1) {
                if (account.getId() != idAccount1 && account.getCurrency().equals(currency)) {
                    accountForTransferList.add(account);
                    accountsAvailable.add(account.getName());
                }
            }

            if (accountForTransferList.size() > 0) {
                spinAccount2.setEnabled(true);

                spinAccount2.setAdapter(new SpinnerAccountForTransAdapter(getActivity(), R.layout.spin_custom_item,
                        accountsAvailable, accountForTransferList));
            }

            else {
                return false;
            }
        }

        return true;
    }


    private void showNoAvailableAccountsDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(getActivity().getResources().getString(R.string.dialog_title_no_available_account))
                .content(getActivity().getResources().getString(R.string.dialog_text_no_available_account))
                .positiveText(getActivity().getResources().getString(R.string.get_it))
                .show();
    }

    private void showDialogSingleAccount() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.single_account))
                .content(getString(R.string.dialog_text_single_account))
                .positiveText(getString(R.string.new_account))
                .negativeText(getString(R.string.close))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        goToAddNewAccount();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                })
                .show();
    }

    public void goToAddNewAccount() {
        getActivity().finish();
        openAddAccountActivity();
    }

    private void openAddAccountActivity() {
        Intent intent = new Intent(getActivity(), ActAccount.class);
        startActivity(intent);
    }


    private void pushBroadcast() {
        Intent intentFragmentMain = new Intent(FrgMain.BROADCAST_FRAGMENT_MAIN_ACTION);
        intentFragmentMain.putExtra(FrgMain.PARAM_STATUS_FRAGMENT_MAIN, FrgMain.STATUS_UPDATE_FRAGMENT_MAIN);
        getActivity().sendBroadcast(intentFragmentMain);
    }


    public void addTransactionBTW() {

        if (accountList1.size() == 1) {
            showDialogSingleAccount();

        } else {

            if (!spinAccount2.isEnabled()) {
                showNoAvailableAccountsDialog();

            } else {

                EditText etSum = (EditText) view.findViewById(R.id.editTextTransBTWSum);

                String sum = etSum.getText().toString();

                if (!sum.matches(".*\\d.*") || Double.parseDouble(sum) == 0) {
                    Shake.highlightEditText(etSum);
                    Toast.makeText(getActivity(), getResources().getString(R.string.transaction_empty_amount_field), Toast.LENGTH_LONG).show();

                } else {

                    double amount = Double.parseDouble(sum);

                    int pos1 = spinAccount1.getSelectedItemPosition();

                    double accountAmount1 = accountList1.get(pos1).getAmount();

                    if (amount > accountAmount1) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.transaction_not_enough_costs) + " " +
                                Math.abs(amount), Toast.LENGTH_LONG).show();
                    } else {

                        String accountName2 = spinAccount2.getSelectedItem().toString();

                        int accountId1 = accountList1.get(pos1).getId();

                        int accountId2 = 0;
                        double accountAmount2 = 0;

                        for (Account account2 : accountList1) {
                            if (account2.getName().equals(accountName2)) {

                                accountId2 = account2.getId();
                                accountAmount2 = account2.getAmount();
                            }
                        }
                            accountAmount1 -= amount;
                            accountAmount2 += amount;

                            dataSource.updateAccountsAmountAfterTransfer(accountId1, accountAmount1, accountId2, accountAmount2);


                            pushBroadcast();

                            getActivity().finish();

                        }
                    }
                }
            }
        }

}

package com.androidcollider.easyfin.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.adapters.RecyclerDebtAdapter;
import com.androidcollider.easyfin.objects.Debt;
import com.androidcollider.easyfin.objects.InfoFromDB;

import java.util.ArrayList;

public class FrgDebts extends CommonFragment {

    public final static String BROADCAST_DEBT_ACTION = "com.androidcollider.easyfin.debt.broadcast";
    public final static String PARAM_STATUS_DEBT = "update_debt";
    public final static int STATUS_UPDATE_DEBT = 6;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private ArrayList<Debt> debtList = null;
    private RecyclerDebtAdapter recyclerAdapter;
    private BroadcastReceiver broadcastReceiver;
    private FloatingActionButton faButtonMain, faButtonTake, faButtonGive;
    private boolean isExpanded = false;
    private float offset1, offset2;
    final private boolean isApiHoneycombAndHigher = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_debts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerDebt);
        tvEmpty = (TextView) view.findViewById(R.id.tvEmptyDebt);
        setItemDebt();
        registerForContextMenu(recyclerView);

        final ViewGroup fabContainer = (ViewGroup) view.findViewById(R.id.coordinatorLayoutFloatDebt);
        faButtonMain = (FloatingActionButton) view.findViewById(R.id.btnFloatDebts);
        faButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFloatButtonsVisibility();
            }
        });
        faButtonTake = (FloatingActionButton) view.findViewById(R.id.btnFloatAddDebtTake);
        faButtonGive = (FloatingActionButton) view.findViewById(R.id.btnFloatAddDebtGive);
        faButtonTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddDebt(1);
                setFloatButtonsVisibility();
            }
        });
        faButtonGive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddDebt(0);
                setFloatButtonsVisibility();
            }
        });

        if (isApiHoneycombAndHigher) {
            fabContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    fabContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    offset1 = faButtonMain.getY() + faButtonMain.getHeight() / 6 - faButtonTake.getY();
                    faButtonTake.setTranslationY(offset1);
                    offset2 = faButtonMain.getY() + faButtonMain.getHeight() / 6 - faButtonGive.getY();
                    faButtonGive.setTranslationY(offset2);
                    return true;
                }
            });
        } else {
            faButtonTake.setVisibility(View.GONE);
            faButtonGive.setVisibility(View.GONE);
        }

        makeBroadcastReceiver();

        addNonFabTouchListener(view.findViewById(R.id.debts_content));

        return view;
    }

    private void setFloatButtonsVisibility() {
        if (isApiHoneycombAndHigher) {
            isExpanded = !isExpanded;
            if (isExpanded) {
                expandFab();
                faButtonMain.setImageResource(R.drawable.ic_close_white_24dp);
            } else {
                collapseFab();
                faButtonMain.setImageResource(R.drawable.ic_plus_white_48dp);
            }
        } else {
            if (faButtonTake.getVisibility() == View.GONE) {
                faButtonTake.setVisibility(View.VISIBLE);
                faButtonGive.setVisibility(View.VISIBLE);
                faButtonMain.setImageResource(R.drawable.ic_close_white_24dp);
            } else {
                faButtonTake.setVisibility(View.GONE);
                faButtonGive.setVisibility(View.GONE);
                faButtonMain.setImageResource(R.drawable.ic_plus_white_48dp);
            }
        }
    }

    private void setItemDebt() {
        debtList = InfoFromDB.getInstance().getDataSource().getAllDebtInfo();
        setVisibility();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerAdapter = new RecyclerDebtAdapter(getActivity(), debtList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void setVisibility() {
        recyclerView.setVisibility(debtList.isEmpty() ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(debtList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
        }
        return false;
    }

    private void goToAddDebt(int type) {
        FrgAddDebt frgAddDebt = new FrgAddDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", 0);
        arguments.putInt("type", type);
        frgAddDebt.setArguments(arguments);

        addFragment(frgAddDebt);
    }

    private void makeBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra(PARAM_STATUS_DEBT, 0) == STATUS_UPDATE_DEBT) {
                    debtList.clear();
                    debtList.addAll(InfoFromDB.getInstance().getDataSource().getAllDebtInfo());
                    setVisibility();
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_DEBT_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    public boolean onContextItemSelected(MenuItem item) {
        int pos;
        try {
            pos = (int) recyclerAdapter.getPosition();
        } catch (Exception e) {
            return super.onContextItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.ctx_menu_pay_all_debt: {
                goToPayDebt(pos, 1);
                break;
            }
            case R.id.ctx_menu_pay_part_debt: {
                goToPayDebt(pos, 2);
                break;
            }
            case R.id.ctx_menu_take_more_debt: {
                goToPayDebt(pos, 3);
                break;
            }
            case R.id.ctx_menu_edit_debt: {
                goToEditDebt(pos, 1);
                break;
            }
            case R.id.ctx_menu_delete_debt: {
                showDialogDeleteDebt(pos);
                break;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void showDialogDeleteDebt(final int pos) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.dialog_title_delete))
                .content(getString(R.string.debt_delete_warning))
                .positiveText(getString(R.string.delete))
                .negativeText(getString(R.string.cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteDebt(pos);
                    }
                })
                .cancelable(false)
                .show();
    }

    private void deleteDebt(int pos) {
        Debt debt = debtList.get(pos);
        int idAccount = debt.getIdAccount();
        int idDebt = debt.getId();
        double amount = debt.getAmountCurrent();
        int type = debt.getType();

        InfoFromDB.getInstance().getDataSource().deleteDebt(idAccount, idDebt, amount, type);

        debtList.remove(pos);
        setVisibility();
        recyclerAdapter.notifyDataSetChanged();
        InfoFromDB.getInstance().updateAccountList();
        pushBroadcast();
    }

    private void pushBroadcast() {
        Intent intentFrgMain = new Intent(FrgHome.BROADCAST_FRG_MAIN_ACTION);
        intentFrgMain.putExtra(FrgHome.PARAM_STATUS_FRG_MAIN, FrgHome.STATUS_UPDATE_FRG_MAIN_BALANCE);
        getActivity().sendBroadcast(intentFrgMain);

        Intent intentFrgAccounts = new Intent(FrgAccounts.BROADCAST_FRG_ACCOUNT_ACTION);
        intentFrgAccounts.putExtra(FrgAccounts.PARAM_STATUS_FRG_ACCOUNT, FrgAccounts.STATUS_UPDATE_FRG_ACCOUNT);
        getActivity().sendBroadcast(intentFrgAccounts);
    }

    private void goToPayDebt(int pos, int mode) {
        Debt debt = debtList.get(pos);

        FrgPayDebt frgPayDebt = new FrgPayDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", mode);
        arguments.putSerializable("debt", debt);
        frgPayDebt.setArguments(arguments);

        addFragment(frgPayDebt);
    }

    public void goToEditDebt(int pos, int mode) {
        Debt debt = debtList.get(pos);

        FrgAddDebt frgAddDebt = new FrgAddDebt();
        Bundle arguments = new Bundle();
        arguments.putInt("mode", mode);
        arguments.putSerializable("debt", debt);
        frgAddDebt.setArguments(arguments);

        addFragment(frgAddDebt);
    }


    private void collapseFab() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createCollapseAnimator(faButtonTake, offset1),
                createCollapseAnimator(faButtonGive, offset2));
        animatorSet.start();
    }

    private void expandFab() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(createExpandAnimator(faButtonTake, offset1),
                createExpandAnimator(faButtonGive, offset2));
        animatorSet.start();
    }

    private static final String TRANSLATION_Y = "translationY";

    private Animator createCollapseAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, 0, offset)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private Animator createExpandAnimator(View view, float offset) {
        return ObjectAnimator.ofFloat(view, TRANSLATION_Y, offset, 0)
                .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private void addNonFabTouchListener(View view) {
        if (!(view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (isExpanded) setFloatButtonsVisibility();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                addNonFabTouchListener(innerView);
            }
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.debts);
    }

}

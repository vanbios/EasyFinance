package com.androidcollider.easyfin.common.ui.fragments.common;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsManager;
import com.androidcollider.easyfin.common.ui.MainActivity;

import java.util.LinkedList;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public abstract class CommonFragment extends AbstractBaseFragment {

    @Inject
    AnalyticsManager analyticsManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        ((App) getActivity().getApplication()).getComponent().inject(this);

        analyticsManager.sendScreeName(getRealTag());
    }

    @Override
    public void onResume() {
        super.onResume();
        while (!pendingTransactions.isEmpty()) {
            pendingTransactions.pollFirst().run();
        }
    }

    public String getRealTag() {
        return this.getClass().getName();
    }

    protected void addFragment(CommonFragment f) {
        ((MainActivity) getActivity()).addFragment(f);
    }

    protected void finish() {
        tryExecuteTransaction(() -> getFragmentManager().popBackStack());
    }

    protected void popAll() {
        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public abstract String getTitle();

    private final LinkedList<Runnable> pendingTransactions = new LinkedList<>();

    protected void tryExecuteTransaction(Runnable runnable) {
        if (isResumed()) runnable.run();
        else pendingTransactions.addLast(runnable);
    }
}
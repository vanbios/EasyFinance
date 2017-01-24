package com.androidcollider.easyfin.common.ui.fragments.common;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.app.App;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.LinkedList;

/**
 * @author Ihor Bilous
 */

public abstract class CommonFragment extends AbstractBaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        Tracker mTracker = App.tracker();
        mTracker.setScreenName(getRealTag());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        //getFragmentManager().popBackStack();
    }

    protected void popAll() {
        getFragmentManager().popBackStack(1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public abstract String getTitle();

    private LinkedList<Runnable> pendingTransactions = new LinkedList<>();

    protected void tryExecuteTransaction(Runnable runnable) {
        if (isResumed()) runnable.run();
        else pendingTransactions.addLast(runnable);
    }
}
package com.androidcollider.easyfin.mockito.main;

import com.androidcollider.easyfin.main.MainMVP;
import com.androidcollider.easyfin.main.MainPresenter;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.rxjava3.core.Flowable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ihor Bilous
 */

public class MainPresenterTests {

    private MainMVP.Model mockModel;
    private MainMVP.View mockView;
    private MainMVP.Presenter presenter;


    @Before
    public void setup() {
        mockModel = mock(MainMVP.Model.class);
        mockView = mock(MainMVP.View.class);
        presenter = new MainPresenter(mockModel);
        presenter.setView(mockView);
    }

    @Test
    public void checkIsAccountsExistsPositiveTestViewNotNull() {
        Flowable<Integer> flowable = getTestFlowable(1);

        when(mockModel.getAccountsCountObservable()).thenReturn(flowable);

        presenter.checkIsAccountsExists();

        verify(mockModel, times(1)).getAccountsCountObservable();

        verify(mockView, never()).informNoAccounts();
    }

    @Test
    public void checkIsAccountsExistsNegativeTestViewNotNull() {
        Flowable<Integer> flowable = getTestFlowable(0);

        when(mockModel.getAccountsCountObservable()).thenReturn(flowable);

        presenter.checkIsAccountsExists();

        verify(mockModel, times(1)).getAccountsCountObservable();

        verify(mockView, times(1)).informNoAccounts();
    }

    @Test
    public void checkIsAccountsExistsNegativeTestViewIsNull() {
        Flowable<Integer> flowable = getTestFlowable(0);

        when(mockModel.getAccountsCountObservable()).thenReturn(flowable);

        presenter.setView(null);

        presenter.checkIsAccountsExists();

        verify(mockModel, times(1)).getAccountsCountObservable();

        verify(mockView, never()).informNoAccounts();
    }

    private Flowable<Integer> getTestFlowable(int value) {
        return Flowable.just(value);
    }
}
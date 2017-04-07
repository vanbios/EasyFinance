package com.androidcollider.easyfin.mockito.accounts.list;

import com.androidcollider.easyfin.accounts.list.AccountViewModel;
import com.androidcollider.easyfin.accounts.list.AccountsMVP;
import com.androidcollider.easyfin.accounts.list.AccountsPresenter;
import com.androidcollider.easyfin.common.models.Account;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ihor Bilous
 */

public class AccountsPresenterTests {

    private AccountsMVP.Model mockModel;
    private AccountsMVP.View mockView;
    private AccountsMVP.Presenter presenter;


    @Before
    public void setup() {
        mockModel = mock(AccountsMVP.Model.class);
        mockView = mock(AccountsMVP.View.class);
        presenter = new AccountsPresenter(mockModel);
        presenter.setView(mockView);
    }


    @Test
    public void loadDataTestViewNotNull() {
        List<AccountViewModel> list = getTestAccountViewModelList();
        Flowable<List<AccountViewModel>> flowable = getTestAccountViewModelListFlowable(list);

        when(mockModel.getAccountList()).thenReturn(flowable);

        presenter.loadData();

        verify(mockModel, times(1)).getAccountList();

        verify(mockView, times(1)).setAccountList(list);

        verify(mockView).setAccountList(ArgumentMatchers.eq(list));
    }

    @Test
    public void loadDataTestViewIsNull() {
        List<AccountViewModel> list = getTestAccountViewModelList();
        Flowable<List<AccountViewModel>> flowable = getTestAccountViewModelListFlowable(list);

        when(mockModel.getAccountList()).thenReturn(flowable);

        presenter.setView(null);

        presenter.loadData();

        verify(mockModel, times(1)).getAccountList();

        verify(mockView, never()).setAccountList(list);
    }


    @Test
    public void getAccountByIdTestViewNotNull() {
        Account account = getTestAccount();
        Flowable<Account> flowable = getTestAccountFlowable(account);
        int id = 1;

        when(mockModel.getAccountById(id)).thenReturn(flowable);

        presenter.getAccountById(id);

        verify(mockModel, times(1)).getAccountById(id);

        verify(mockModel).getAccountById(ArgumentMatchers.eq(id));

        verify(mockView, times(1)).goToEditAccount(account);

        verify(mockView).goToEditAccount(ArgumentMatchers.eq(account));
    }

    @Test
    public void getAccountByIdTestViewIsNull() {
        Account account = getTestAccount();
        Flowable<Account> flowable = getTestAccountFlowable(account);
        int id = 1;

        when(mockModel.getAccountById(id)).thenReturn(flowable);

        presenter.setView(null);

        presenter.getAccountById(id);

        verify(mockModel, times(1)).getAccountById(id);

        verify(mockModel).getAccountById(ArgumentMatchers.eq(id));

        verify(mockView, never()).goToEditAccount(account);
    }


    @Test
    public void deleteAccountByIdPositiveTestViewNotNull() {
        boolean isDeleted = true;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteAccountById(id)).thenReturn(flowable);

        presenter.deleteAccountById(id);

        verify(mockModel, times(1)).deleteAccountById(id);

        verify(mockModel).deleteAccountById(ArgumentMatchers.eq(id));

        verify(mockView, times(1)).deleteAccount();
    }

    @Test
    public void deleteAccountByIdNegativeTestViewNotNull() {
        boolean isDeleted = false;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteAccountById(id)).thenReturn(flowable);

        presenter.deleteAccountById(id);

        verify(mockModel, times(1)).deleteAccountById(id);

        verify(mockModel).deleteAccountById(ArgumentMatchers.eq(id));

        verify(mockView, never()).deleteAccount();
    }

    @Test
    public void deleteAccountByIdPositiveTestViewIsNull() {
        boolean isDeleted = true;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteAccountById(id)).thenReturn(flowable);

        presenter.setView(null);

        presenter.deleteAccountById(id);

        verify(mockModel, times(1)).deleteAccountById(id);

        verify(mockModel).deleteAccountById(ArgumentMatchers.eq(id));

        verify(mockView, never()).deleteAccount();
    }

    private Flowable<List<AccountViewModel>> getTestAccountViewModelListFlowable(List<AccountViewModel> list) {
        return Flowable.just(list);
    }

    private List<AccountViewModel> getTestAccountViewModelList() {
        List<AccountViewModel> list = new ArrayList<>();
        list.add(mock(AccountViewModel.class));
        return list;
    }

    private Flowable<Account> getTestAccountFlowable(Account account) {
        return Flowable.just(account);
    }

    private Account getTestAccount() {
        return mock(Account.class);
    }

    private Flowable<Boolean> getTestBooleanFlowable(boolean b) {
        return Flowable.just(b);
    }
}
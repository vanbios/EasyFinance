package com.androidcollider.easyfin.mockito.transactions.list;

import android.util.Pair;

import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.transactions.list.TransactionViewModel;
import com.androidcollider.easyfin.transactions.list.TransactionsMVP;
import com.androidcollider.easyfin.transactions.list.TransactionsPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ihor Bilous
 */

public class TransactionsPresenterTests {

    private TransactionsMVP.Model mockModel;
    private TransactionsMVP.View mockView;
    private TransactionsMVP.Presenter presenter;


    @Before
    public void setup() {
        mockModel = mock(TransactionsMVP.Model.class);
        mockView = mock(TransactionsMVP.View.class);
        presenter = new TransactionsPresenter(mockModel);
        presenter.setView(mockView);
    }

    /*@Test
    public void loadDataTest() {
        Pair<List<TransactionViewModel>,
                Pair<List<TransactionCategory>, List<TransactionCategory>>> pair = getTransactionAndTransactionCategoriesListsTestPair();

        Flowable<Pair<List<TransactionViewModel>,
                Pair<List<TransactionCategory>, List<TransactionCategory>>>> flowable = getTransactionAndTransactionCategoriesListsTestFlowable(
                pair);

        when(mockModel.getTransactionAndTransactionCategoriesLists()).thenReturn(flowable);

        presenter.loadData();

        verify(mockModel, times(1)).getTransactionAndTransactionCategoriesLists();

        verify(mockView, times(1)).setTransactionAndTransactionCategoriesLists(
                pair.first,
                pair.second.first,
                pair.second.second
        );

        verify(mockView).setTransactionAndTransactionCategoriesLists(
                ArgumentMatchers.eq(pair.first),
                ArgumentMatchers.eq(pair.second.first),
                ArgumentMatchers.eq(pair.second.second)
        );
    }*/

    @Test
    public void getTransactionByIdTestViewNotNull() {
        Transaction transaction = getTestTransaction();
        Flowable<Transaction> flowable = getTestTransactionFlowable(transaction);
        int id = 1;

        when(mockModel.getTransactionById(id)).thenReturn(flowable);

        presenter.getTransactionById(id);

        verify(mockModel, times(1)).getTransactionById(id);

        verify(mockModel).getTransactionById(ArgumentMatchers.eq(id));

        verify(mockView, times(1)).goToEditTransaction(transaction);

        verify(mockView).goToEditTransaction(ArgumentMatchers.eq(transaction));
    }

    @Test
    public void getTransactionByIdTestViewIsNull() {
        Transaction transaction = getTestTransaction();
        Flowable<Transaction> flowable = getTestTransactionFlowable(transaction);
        int id = 1;

        when(mockModel.getTransactionById(id)).thenReturn(flowable);

        presenter.setView(null);

        presenter.getTransactionById(id);

        verify(mockModel, times(1)).getTransactionById(id);

        verify(mockModel).getTransactionById(ArgumentMatchers.eq(id));

        verify(mockView, never()).goToEditTransaction(transaction);
    }

    @Test
    public void deleteTransactionByIdPositiveTestViewNotNull() {
        boolean isDeleted = true;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteTransactionById(id)).thenReturn(flowable);

        presenter.deleteTransactionById(id);

        verify(mockModel, times(1)).deleteTransactionById(id);

        verify(mockModel).deleteTransactionById(ArgumentMatchers.eq(id));

        verify(mockView, times(1)).deleteTransaction();
    }

    @Test
    public void deleteTransactionByIdNegativeTestViewNotNull() {
        boolean isDeleted = false;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteTransactionById(id)).thenReturn(flowable);

        presenter.deleteTransactionById(id);

        verify(mockModel, times(1)).deleteTransactionById(id);

        verify(mockModel).deleteTransactionById(ArgumentMatchers.eq(id));

        verify(mockView, never()).deleteTransaction();
    }

    @Test
    public void deleteTransactionByIdPositiveTestViewIsNull() {
        boolean isDeleted = true;
        int id = 1;
        Flowable<Boolean> flowable = getTestBooleanFlowable(isDeleted);

        when(mockModel.deleteTransactionById(id)).thenReturn(flowable);

        presenter.setView(null);

        presenter.deleteTransactionById(id);

        verify(mockModel, times(1)).deleteTransactionById(id);

        verify(mockModel).deleteTransactionById(ArgumentMatchers.eq(id));

        verify(mockView, never()).deleteTransaction();
    }

    private Transaction getTestTransaction() {
        return new Transaction();
    }

    private Flowable<Transaction> getTestTransactionFlowable(Transaction transaction) {
        return Flowable.just(transaction);
    }

    private Flowable<Boolean> getTestBooleanFlowable(boolean b) {
        return Flowable.just(b);
    }

    private Flowable<Pair<List<TransactionViewModel>,
            Pair<List<TransactionCategory>, List<TransactionCategory>>>> getTransactionAndTransactionCategoriesListsTestFlowable(
            Pair<List<TransactionViewModel>,
                    Pair<List<TransactionCategory>, List<TransactionCategory>>> pair) {
        return Flowable.just(pair);
    }

    private Pair<List<TransactionViewModel>,
            Pair<List<TransactionCategory>, List<TransactionCategory>>> getTransactionAndTransactionCategoriesListsTestPair() {
        TransactionViewModel transactionViewModel = new TransactionViewModel();
        TransactionCategory transactionCategory = new TransactionCategory(0, "", 1);

        List<TransactionViewModel> transactionViewModelList = new ArrayList<>();
        transactionViewModelList.add(transactionViewModel);

        List<TransactionCategory> transactionCategoryList = new ArrayList<>();
        transactionCategoryList.add(transactionCategory);

        Pair<List<TransactionCategory>, List<TransactionCategory>> categoriesPair = new Pair<>(transactionCategoryList, transactionCategoryList);

        return new Pair<>(transactionViewModelList, categoriesPair);
    }
}
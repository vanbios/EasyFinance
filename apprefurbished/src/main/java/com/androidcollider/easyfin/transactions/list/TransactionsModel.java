package com.androidcollider.easyfin.transactions.list;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Pair;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager;
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.models.Transaction;
import com.androidcollider.easyfin.common.models.TransactionCategory;
import com.androidcollider.easyfin.common.repository.Repository;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import io.reactivex.Flowable;

/**
 * @author Ihor Bilous
 */

class TransactionsModel implements TransactionsMVP.Model {

    private Repository repository;
    private DateFormatManager dateFormatManager;
    private NumberFormatManager numberFormatManager;
    private Context context;
    private final String[] curArray, curLangArray;


    TransactionsModel(Repository repository,
                      DateFormatManager dateFormatManager,
                      NumberFormatManager numberFormatManager,
                      ResourcesManager resourcesManager,
                      Context context) {
        this.repository = repository;
        this.dateFormatManager = dateFormatManager;
        this.numberFormatManager = numberFormatManager;
        this.context = context;
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY);
        curLangArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG);
    }

    @Override
    public Flowable<Pair<List<TransactionViewModel>,
            Pair<List<TransactionCategory>, List<TransactionCategory>>>> getTransactionAndTransactionCategoriesLists() {
        return Flowable.combineLatest(
                repository.getAllTransactions()
                        .map(this::transformTransactionListToViewModelList),
                repository.getAllTransactionIncomeCategories(),
                repository.getAllTransactionExpenseCategories(),
                (transactionViewModels, transactionCategoryIncomeList, transactionCategoryExpenseList) ->
                        new Pair<>(
                                transactionViewModels,
                                new Pair<>(
                                        transactionCategoryIncomeList,
                                        transactionCategoryExpenseList
                                )
                        ));
    }

    @Override
    public Flowable<Transaction> getTransactionById(int id) {
        return repository.getAllTransactions()
                .flatMap(Flowable::fromIterable)
                .filter(transaction -> transaction.getId() == id);
    }

    @Override
    public Flowable<Boolean> deleteTransactionById(int id) {
        return getTransactionById(id)
                .flatMap(transaction ->
                        repository.deleteTransaction(
                                transaction.getIdAccount(),
                                transaction.getId(),
                                transaction.getAmount()
                        )
                );
    }

    private TransactionViewModel transformTransactionToViewModel(Transaction transaction) {
        TransactionViewModel.TransactionViewModelBuilder builder = TransactionViewModel.builder();

        builder.id(transaction.getId());
        builder.accountName(transaction.getAccountName());
        builder.date(dateFormatManager.longToDateString(transaction.getDate(), DateFormatManager.DAY_MONTH_YEAR_DOTS));

        String amount = numberFormatManager.doubleToStringFormatter(
                transaction.getAmount(),
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
        );
        String curLang = null;

        for (int i = 0; i < curArray.length; i++) {
            if (transaction.getCurrency().equals(curArray[i])) {
                curLang = curLangArray[i];
                break;
            }
        }

        boolean isExpense = amount.contains("-");
        builder.isExpense(isExpense);

        if (isExpense) {
            builder.amount(String.format("- %1$s %2$s", amount.substring(1), curLang));
            builder.colorRes(ContextCompat.getColor(context, R.color.custom_red));
        } else {
            builder.amount(String.format("+ %1$s %2$s", amount, curLang));
            builder.colorRes(ContextCompat.getColor(context, R.color.custom_green));
        }

        builder.category(transaction.getCategory());
        builder.accountType(transaction.getAccountType());

        return builder.build();
    }

    private List<TransactionViewModel> transformTransactionListToViewModelList(List<Transaction> transactionList) {
        return Stream.of(transactionList).map(this::transformTransactionToViewModel).collect(Collectors.toList());
    }
}
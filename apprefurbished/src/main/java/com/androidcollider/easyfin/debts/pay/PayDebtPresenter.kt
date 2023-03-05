package com.androidcollider.easyfin.debts.pay

import android.content.Context
import android.os.Bundle
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.utils.serializable
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import com.androidcollider.easyfin.debts.list.DebtsFragment
import io.reactivex.rxjava3.core.Single

/**
 * @author Ihor Bilous
 */
internal class PayDebtPresenter(
    private val context: Context,
    private val model: PayDebtMVP.Model
) : PayDebtMVP.Presenter {

    private var view: PayDebtMVP.View? = null
    private var mode = 0
    private var debt: Debt? = null

    override fun setView(view: PayDebtMVP.View?) {
        this.view = view
    }

    override fun setArguments(args: Bundle?) {
        val aMode = args?.getInt(DebtsFragment.MODE, 0)
        aMode?.let { mode = it }
        debt = args?.serializable(DebtsFragment.DEBT) as Debt?
    }

    override fun loadAccounts() {
        model.allAccounts
            .subscribe(this::setupView)
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun save() {
        when (mode) {
            DebtsFragment.PAY_ALL -> payAllDebt()
            DebtsFragment.PAY_PART -> payPartDebt()
            DebtsFragment.TAKE_MORE -> takeMoreDebt()
        }
    }

    private fun payAllDebt() {
        if (view != null) {
            val amountDebt = debt!!.amountCurrent
            val type = debt!!.type
            val account = view!!.account
            var amountAccount = account.amount
            if (type == DebtsFragment.TYPE_TAKE) {
                amountAccount -= amountDebt
            } else {
                amountAccount += amountDebt
            }
            handleActionWithDebt(
                model.payFullDebt(
                    account.id,
                    amountAccount,
                    debt!!.id
                )
            )
        }
    }

    private fun payPartDebt() {
        if (view != null) {
            val sum = model.prepareStringToParse(view!!.amount)
            if (checkForFillSumField(sum)) {
                val amountDebt = sum.toDouble()
                val amountAllDebt = debt!!.amountCurrent
                if (amountDebt > amountAllDebt) {
                    view!!.showMessage(context.getString(R.string.debt_sum_more_then_amount))
                } else {
                    val type = debt!!.type
                    val account = view!!.account
                    var amountAccount = account.amount
                    if (type == DebtsFragment.TYPE_TAKE && amountDebt > amountAccount) {
                        view!!.showMessage(context.getString(R.string.not_enough_costs))
                    } else {
                        val idDebt = debt!!.id
                        val idAccount = account.id
                        if (type == DebtsFragment.TYPE_TAKE) {
                            amountAccount -= amountDebt
                        } else {
                            amountAccount += amountDebt
                        }
                        if (amountDebt == amountAllDebt) {
                            handleActionWithDebt(
                                model.payFullDebt(
                                    idAccount,
                                    amountAccount,
                                    idDebt
                                )
                            )
                        } else {
                            val newDebtAmount = amountAllDebt - amountDebt
                            handleActionWithDebt(
                                model.payPartOfDebt(
                                    idAccount,
                                    amountAccount,
                                    idDebt,
                                    newDebtAmount
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun takeMoreDebt() {
        if (view != null) {
            val sum = model.prepareStringToParse(view!!.amount)
            if (checkForFillSumField(sum)) {
                val amountDebt = sum.toDouble()
                val amountDebtCurrent = debt!!.amountCurrent
                val amountDebtAll = debt!!.amountAll
                val type = debt!!.type
                val account = view!!.account
                var amountAccount = account.amount
                if (type == DebtsFragment.TYPE_GIVE && amountDebt > amountAccount) {
                    view!!.showMessage(context.getString(R.string.not_enough_costs))
                } else {
                    when (type) {
                        DebtsFragment.TYPE_GIVE -> amountAccount -= amountDebt
                        DebtsFragment.TYPE_TAKE -> amountAccount += amountDebt
                    }
                    val newDebtCurrentAmount = amountDebtCurrent + amountDebt
                    val newDebtAllAmount = amountDebtAll + amountDebt
                    handleActionWithDebt(
                        model.takeMoreDebt(
                            account.id,
                            amountAccount,
                            debt!!.id,
                            newDebtCurrentAmount,
                            newDebtAllAmount
                        )
                    )
                }
            }
        }
    }

    private fun checkForFillSumField(s: String): Boolean {
        if (!s.matches(".*\\d.*".toRegex()) || s.toDouble().compareTo(0) == 0) {
            view?.showMessage(context.getString(R.string.empty_amount_field))
            return false
        }
        return true
    }

    private fun handleActionWithDebt(single: Single<Boolean>) {
        single.subscribe(
            { aBoolean: Boolean ->
                if (aBoolean) {
                    view?.performLastActionsAfterSaveAndClose()
                }
            }) { obj: Throwable -> obj.printStackTrace() }
    }

    private fun setupView(accountList: List<SpinAccountViewModel>) {
        if (view != null) {
            val accountsAvailableList = getAccountAvailableList(accountList)
            if (accountsAvailableList.isEmpty()) {
                view!!.notifyNotEnoughAccounts()
            } else {
                view!!.accounts = accountsAvailableList
                view!!.showName(debt!!.name)
                if (mode == DebtsFragment.PAY_ALL || mode == DebtsFragment.PAY_PART) {
                    view!!.showAmount(model.formatAmount(debt!!.amountCurrent))
                } else {
                    view!!.showAmount("0,00")
                    view!!.openNumericDialog()
                }
                if (mode == DebtsFragment.PAY_ALL) view!!.disableAmountField()
                view!!.setupSpinner()
                val idAccount = debt!!.idAccount
                var pos = 0
                for (i in accountsAvailableList.indices) {
                    if (idAccount == accountsAvailableList[i].id) {
                        pos = i
                        break
                    }
                }
                view!!.showAccount(pos)
            }
        }
    }

    private fun getAccountAvailableList(accountList: List<SpinAccountViewModel>):
            List<SpinAccountViewModel> {
        val accountsAvailableList: MutableList<SpinAccountViewModel> = ArrayList()
        debt?.let {
            val currency = it.currency
            val amount = it.amountCurrent
            val type = it.type
            accountList
                .filter { account: SpinAccountViewModel ->
                    if (mode == DebtsFragment.PAY_ALL && type == DebtsFragment.TYPE_TAKE)
                        account.currency == currency && account.amount >= amount
                    else account.currency == currency
                }
                .forEach { e: SpinAccountViewModel -> accountsAvailableList.add(e) }
        }
        return accountsAvailableList
    }
}
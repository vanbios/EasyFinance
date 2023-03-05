package com.androidcollider.easyfin.debts.add_edit

import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.utils.serializable
import com.androidcollider.easyfin.common.view_models.SpinAccountViewModel
import com.androidcollider.easyfin.debts.list.DebtsFragment
import io.reactivex.rxjava3.core.Single
import java.util.*
import kotlin.math.abs

/**
 * @author Ihor Bilous
 */
internal class AddDebtPresenter(
    private val context: Context,
    private val model: AddDebtMVP.Model
) : AddDebtMVP.Presenter {

    private var view: AddDebtMVP.View? = null
    private var mode = 0
    private var debtType = 0
    private var debtFrIntent: Debt? = null

    override fun setView(view: AddDebtMVP.View?) {
        this.view = view
    }

    override fun setArguments(args: Bundle?) {
        args?.let {
            mode = it.getInt(DebtsFragment.MODE, 0)
            if (mode == DebtsFragment.EDIT) {
                debtFrIntent = it.serializable(DebtsFragment.DEBT) as Debt?
            } else {
                debtType = it.getInt(DebtsFragment.TYPE, 0)
            }
        }
    }

    override fun loadAccounts() {
        model.allAccounts
            .subscribe({ accountList: List<SpinAccountViewModel> -> setupView(accountList) })
            { obj: Throwable -> obj.printStackTrace() }
    }

    override fun save() {
        when (mode) {
            DebtsFragment.ADD -> addDebt()
            DebtsFragment.EDIT -> editDebt()
        }
    }

    private fun addDebt() {
        view?.let {
            if (validateName(it.name)) {
                val account = it.account
                var accountAmount = account!!.amount
                val amount = model.prepareStringToParse(it.amount).toDouble()
                if (checkIsEnoughCosts(debtType, amount, accountAmount)) {
                    when (debtType) {
                        DebtsFragment.TYPE_GIVE -> accountAmount -= amount
                        DebtsFragment.TYPE_TAKE -> accountAmount += amount
                    }
                    val debt = buildDebt(
                        account,
                        amount,
                        debtType,
                        it.name,
                        it.date,
                        accountAmount
                    )
                    handleActionWithDebt(
                        model.addNewDebt(debt)
                    )
                }
            }
        }
    }

    private fun editDebt() {
        view?.let {
            if (validateName(it.name)) {
                val account = it.account
                var accountAmount = account!!.amount
                val type = debtType
                val amount = model.prepareStringToParse(it.amount).toDouble()
                val accountId = account.id
                val oldAccountId = debtFrIntent!!.idAccount
                val isAccountsTheSame = accountId == oldAccountId
                val oldAmount = debtFrIntent!!.amountCurrent
                var oldAccountAmount = 0.0
                val oldType = debtFrIntent!!.type
                if (isAccountsTheSame) {
                    when (oldType) {
                        DebtsFragment.TYPE_GIVE -> accountAmount += oldAmount
                        DebtsFragment.TYPE_TAKE -> accountAmount -= oldAmount
                    }
                } else {
                    val accountList = it.accounts
                    for (i in accountList.indices) {
                        if (oldAccountId == accountList[i].id) {
                            oldAccountAmount = accountList[i].amount
                            break
                        }
                    }
                    when (oldType) {
                        DebtsFragment.TYPE_GIVE -> oldAccountAmount += oldAmount
                        DebtsFragment.TYPE_TAKE -> oldAccountAmount -= oldAmount
                    }
                }
                if (checkIsEnoughCosts(type, amount, accountAmount)) {
                    when (type) {
                        DebtsFragment.TYPE_GIVE -> accountAmount -= amount
                        DebtsFragment.TYPE_TAKE -> accountAmount += amount
                    }
                    val debt =
                        buildDebt(account, amount, type, it.name, it.date, accountAmount)
                    if (isAccountsTheSame) {
                        handleActionWithDebt(
                            model.updateDebt(debt)
                        )
                    } else {
                        handleActionWithDebt(
                            model.updateDebtDifferentAccounts(
                                debt,
                                oldAccountAmount,
                                oldAccountId
                            )
                        )
                    }
                }
            }
        }
    }

    private fun checkIsEnoughCosts(type: Int, amount: Double, accountAmount: Double): Boolean {
        if (type == DebtsFragment.TYPE_GIVE && abs(amount) > accountAmount) {
            view?.showMessage(context.getString(R.string.not_enough_costs))
            return false
        }
        return true
    }

    private fun validateName(name: String?): Boolean {
        if (name!!.replace("\\s+".toRegex(), "").isEmpty()) {
            view?.highlightNameField()
            view?.showMessage(context.getString(R.string.empty_name_field))
            return false
        }
        return true
    }

    private fun handleActionWithDebt(single: Single<*>) {
        single.subscribe(
            { view?.performLastActionsAfterSaveAndClose() })
        { obj: Throwable -> obj.printStackTrace() }
    }

    private fun buildDebt(
        account: SpinAccountViewModel,
        amount: Double,
        type: Int,
        name: String?,
        date: String?,
        accountAmount: Double
    ): Debt {
        val debt = Debt()
        debt.name = name
        debt.amountCurrent = amount
        debt.type = type
        debt.idAccount = account.id
        debt.date = model.getMillisFromString(date)
        debt.accountAmount = accountAmount
        debt.id = if (debtFrIntent != null) debtFrIntent!!.id else 0
        debt.currency = account.currency
        debt.accountName = account.name
        debt.amountAll = amount
        return debt
    }

    private fun setupView(accountList: List<SpinAccountViewModel>) {
        view?.let {
            if (accountList.isEmpty()) {
                it.notifyNotEnoughAccounts()
            } else {
                it.accounts = accountList
                if (mode == DebtsFragment.ADD) {
                    it.showAmount("0,00")
                    it.openNumericDialog()
                }
                val calendar = Calendar.getInstance()
                val initTime = System.currentTimeMillis()
                if (mode == DebtsFragment.EDIT) {
                    calendar.time = Date(debtFrIntent!!.date)
                }
                it.setupDateTimeField(calendar, initTime)
                it.setupSpinner()
                if (mode == DebtsFragment.EDIT) {
                    it.showName(debtFrIntent!!.name)
                    it.showAmount(model.formatAmount(debtFrIntent!!.amountCurrent))
                    debtType = debtFrIntent!!.type
                    var pos = 0
                    for (i in accountList.indices) {
                        if (debtFrIntent!!.idAccount == accountList[i].id) {
                            pos = i
                            break
                        }
                    }
                    it.showAccount(pos)
                }
                it.setAmountTextColor(
                    ContextCompat.getColor(
                        context,
                        if (debtType == DebtsFragment.TYPE_TAKE)
                            R.color.custom_red else R.color.custom_green
                    )
                )
            }
        }
    }
}
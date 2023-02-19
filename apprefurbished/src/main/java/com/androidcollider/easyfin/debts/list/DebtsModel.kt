package com.androidcollider.easyfin.debts.list

import android.content.Context
import androidx.core.content.ContextCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.managers.format.date.DateFormatManager
import com.androidcollider.easyfin.common.managers.format.number.NumberFormatManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.models.Debt
import com.androidcollider.easyfin.common.repository.Repository
import io.reactivex.rxjava3.core.Flowable

/**
 * @author Ihor Bilous
 */
internal class DebtsModel(
    private val repository: Repository,
    private val dateFormatManager: DateFormatManager,
    private val numberFormatManager: NumberFormatManager,
    resourcesManager: ResourcesManager,
    private val context: Context
) : DebtsMVP.Model {
    private val curArray: Array<String>
    private val curLangArray: Array<String>

    init {
        curArray = resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY)
        curLangArray =
            resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY_LANG)
    }

    override val debtList: Flowable<List<DebtViewModel>>
        get() = repository.allDebts
            .map { debtList: List<Debt> -> transformDebtListToViewModelList(debtList) }

    override fun getDebtById(id: Int): Flowable<Debt> {
        return repository.allDebts
            .flatMap { source: List<Debt> -> Flowable.fromIterable(source) }
            .filter { debt: Debt -> debt.id == id }
    }

    override fun deleteDebtById(id: Int): Flowable<Boolean> {
        return getDebtById(id)
            .flatMap { debt: Debt ->
                repository.deleteDebt(
                    debt.idAccount,
                    debt.id,
                    debt.amountCurrent,
                    debt.type
                )
            }
    }

    private fun transformDebtToViewModel(debt: Debt): DebtViewModel {
        val model = DebtViewModel()
        model.id = debt.id
        model.name = debt.name
        var curLang: String? = null
        for (i in curArray.indices) {
            if (debt.currency == curArray[i]) {
                curLang = curLangArray[i]
                break
            }
        }
        val amountCurrent = debt.amountCurrent
        val amountAll = debt.amountAll
        model.amount = String.format(
            "%1\$s %2\$s",
            numberFormatManager.doubleToStringFormatter(
                amountCurrent,
                NumberFormatManager.FORMAT_1,
                NumberFormatManager.PRECISE_1
            ),
            curLang
        )
        model.accountName = debt.accountName
        model.date =
            dateFormatManager.longToDateString(debt.date, DateFormatManager.DAY_MONTH_YEAR_DOTS)
        val progress = (amountCurrent / amountAll * 100).toInt()
        model.progress = progress
        model.progressPercents = String.format("%s%%", progress)
        model.colorRes = ContextCompat.getColor(
            context,
            if (debt.type == 1) R.color.custom_red else R.color.custom_green
        )
        return model
    }

    private fun transformDebtListToViewModelList(debtList: List<Debt>): List<DebtViewModel> {
        return debtList.map { debt -> transformDebtToViewModel(debt) }
    }
}
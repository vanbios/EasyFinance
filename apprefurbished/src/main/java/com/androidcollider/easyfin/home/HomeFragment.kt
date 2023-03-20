package com.androidcollider.easyfin.home

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.getCustomView
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.*
import com.androidcollider.easyfin.common.managers.chart.setup.ChartSetupManager
import com.androidcollider.easyfin.common.managers.rates.rates_info.RatesInfoManager
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.ui.adapters.SpinIconTextHeadAdapter
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents
import com.androidcollider.easyfin.common.utils.ChartLargeValueFormatter
import com.androidcollider.easyfin.common.utils.setSafeOnClickListener
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.button.MaterialButton
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class HomeFragment : CommonFragmentWithEvents(), HomeMVP.View {

    private lateinit var spinPeriod: Spinner
    private lateinit var spinBalanceCurrency: Spinner
    private lateinit var spinChartType: Spinner
    private lateinit var tvStatisticSum: TextView
    private lateinit var tvBalanceSum: TextView
    private lateinit var tvStatisticNoData: TextView
    private lateinit var tvBalanceNoData: TextView
    private lateinit var tvBalance: TextView
    private lateinit var ivBalanceSettings: MaterialButton
    private lateinit var chartStatistic: HorizontalBarChart
    private lateinit var chartBalance: HorizontalBarChart
    private lateinit var chartStatisticPie: PieChart
    private lateinit var chkBoxConvert: CheckBox
    private lateinit var chkBoxShowOnlyIntegers: CheckBox
    private lateinit var balanceSettingsDialog: MaterialDialog
    private var convert = false
    private var showOnlyIntegers = false

    // we can prevent init spinner issue with subscribing
    // to listeners in onRestoreInstanceState
    private var spinPeriodNotInitSelectedItemCall = false
    private var spinBalanceCurrencyNotInitSelectedItemCall = false
    //private var spinChartTypeNotInitSelectedItemCall = false

    @Inject
    lateinit var ratesInfoManager: RatesInfoManager

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    @Inject
    lateinit var resourcesManager: ResourcesManager

    @Inject
    lateinit var chartSetupManager: ChartSetupManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var presenter: HomeMVP.Presenter

    override val contentView: Int
        get() = R.layout.frg_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        presenter.setView(this)
        presenter.loadBalanceAndStatistic(spinPeriod.selectedItemPosition + 1)
    }

    private fun setupUI(view: View) {
        spinPeriod = view.findViewById(R.id.spinMainPeriod)
        spinBalanceCurrency = view.findViewById(R.id.spinMainCurrency)
        spinChartType = view.findViewById(R.id.spinMainChart)
        tvStatisticSum = view.findViewById(R.id.tvMainStatisticSum)
        tvBalanceSum = view.findViewById(R.id.tvMainSumValue)
        tvStatisticNoData = view.findViewById(R.id.tvMainStatisticChartNoData)
        tvBalanceNoData = view.findViewById(R.id.tvMainBalanceChartNoData)
        tvBalance = view.findViewById(R.id.tvMainCurrentBalance)
        ivBalanceSettings = view.findViewById(R.id.btnMainBalanceSettings)
        chartStatistic = view.findViewById(R.id.chartHBarMainStatistic)
        chartBalance = view.findViewById(R.id.chartMainBalance)
        chartStatisticPie = view.findViewById(R.id.chartPieMainStatistic)

        ivBalanceSettings.setSafeOnClickListener { balanceSettingsDialog.show() }

        setupCharts()
        buildBalanceSettingsDialog()

        val balanceSettings = balanceSettingsDialog.getCustomView()
        chkBoxConvert = balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsConvert)
        chkBoxShowOnlyIntegers =
            balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsShowCents)
        convert = sharedPrefManager.mainBalanceSettingsConvertCheck
        showOnlyIntegers = sharedPrefManager.mainBalanceSettingsShowOnlyIntegersCheck
        chkBoxConvert.isChecked = convert
        chkBoxShowOnlyIntegers.isChecked = showOnlyIntegers
        chkBoxConvert.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            sharedPrefManager.mainBalanceSettingsConvertCheck = b
            convert = b
            setBalance(spinBalanceCurrency.selectedItemPosition)
            presenter.updateTransactionStatisticArray(spinBalanceCurrency.selectedItemPosition)
            setStatisticSumTV()
            updateStatisticChartsSection()
        }
        chkBoxShowOnlyIntegers.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            sharedPrefManager.mainBalanceSettingsShowOnlyIntegersCheck = b
            showOnlyIntegers = b
            setBalance(spinBalanceCurrency.selectedItemPosition)
            updateStatisticChartsSection()
        }
        ratesInfoManager.setupMultiTapListener(tvBalance, requireActivity())

        setBalanceCurrencySpinner()
        setStatisticPeriodSpinner()
    }

    private fun buildBalanceSettingsDialog() {
        activity?.let {
            balanceSettingsDialog = dialogManager.buildBalanceSettingsDialog(it)
        }
    }

    private fun setBalanceCurrencySpinner() {
        activity?.let {
            spinBalanceCurrency.adapter = SpinIconTextHeadAdapter(
                it,
                R.layout.spin_head_icon_text_main,
                R.id.tvSpinHeadIconTextMain,
                R.id.ivSpinHeadIconTextMain,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY),
                resourcesManager.getIconArray(ResourcesManager.ICON_CURRENCY)
            )
            spinBalanceCurrency.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    if (spinBalanceCurrencyNotInitSelectedItemCall) {
                        setBalance(i)
                        presenter.updateTransactionStatisticArray(i)
                        setStatisticSumTV()
                        updateStatisticChartsSection()
                        sharedPrefManager.homeBalanceCurrencyPos = i
                    } else {
                        spinBalanceCurrencyNotInitSelectedItemCall = true
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            spinBalanceCurrency.setSelection(sharedPrefManager.homeBalanceCurrencyPos)
        }
    }

    private fun setStatisticPeriodSpinner() {
        activity?.let {
            val adapterStatPeriod: ArrayAdapter<*> = ArrayAdapter.createFromResource(
                it,
                ResourcesManager.STRING_MAIN_STATISTIC_PERIOD,
                R.layout.spin_head_text_medium
            )

            adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinPeriod.adapter = adapterStatPeriod
            spinPeriod.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    i: Int,
                    l: Long
                ) {
                    if (spinPeriodNotInitSelectedItemCall) {
                        presenter.updateStatistic(i + 1)
                        sharedPrefManager.homePeriodPos = i
                    } else {
                        spinPeriodNotInitSelectedItemCall = true
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            spinPeriod.setSelection(sharedPrefManager.homePeriodPos)
        }
    }

    private fun setChartTypeSpinner() {
        activity?.let {
            spinChartType.adapter = SpinIconTextHeadAdapter(
                it,
                R.layout.spin_head_icon_text_main_chart,
                R.id.tvSpinHeadIconTextMainChart,
                R.id.ivSpinHeadIconTextMainChart,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_CHART_TYPE),
                resourcesManager.getIconArray(ResourcesManager.ICON_CHART_TYPE)
            )
            spinChartType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?, view: View?, i: Int, l: Long
                ) {
                    /*if (!spinChartTypeNotInitSelectedItemCall) {
                        spinChartTypeNotInitSelectedItemCall = true
                        return
                    }*/

                    if (presenter.isStatisticEmpty) {
                        tvStatisticNoData.visibility = View.VISIBLE
                        chartStatistic.visibility = View.GONE
                        chartStatisticPie.visibility = View.GONE
                    } else {
                        tvStatisticNoData.visibility = View.GONE
                        when (i) {
                            0 -> {
                                chartStatistic.visibility = View.VISIBLE
                                chartStatisticPie.visibility = View.GONE
                                setStatisticBarChartData()
                            }
                            1 -> {
                                chartStatistic.visibility = View.GONE
                                chartStatisticPie.visibility = View.VISIBLE
                                setStatisticPieChartData()
                            }
                        }
                    }
                    sharedPrefManager.homeChartTypePos = i
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHome) {
        presenter.updateBalanceAndStatistic(spinPeriod.selectedItemPosition + 1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHomeBalance) {
        presenter.updateBalance()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHomeNewRates) {
        presenter.updateRates()
        ratesInfoManager.prepareInfo()
        if (convert) {
            setBalance(spinBalanceCurrency.selectedItemPosition)
            presenter.updateTransactionStatisticArray(spinBalanceCurrency.selectedItemPosition)
            setStatisticSumTV()
            updateStatisticChartsSection()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DBImported) {
        presenter.updateBalanceAndStatisticAfterDBImport(
            spinPeriod.selectedItemPosition + 1
        )
    }

    private fun setupCharts() {
        chartSetupManager.setupMainBarChart(chartBalance)
        chartSetupManager.setupMainBarChart(chartStatistic)
        chartSetupManager.setupMainPieChart(chartStatisticPie)
    }

    private fun setBalanceBarChartData(balance: DoubleArray) {
        val data = presenter.getDataSetMainBalanceHorizontalBarChart(balance, chartBalance)
        data.setValueFormatter(ChartLargeValueFormatter(!showOnlyIntegers))
        chartBalance.data = data
        chartBalance.animateXY(2000, 2000)
        chartBalance.invalidate()
    }

    private fun setStatisticBarChartData() {
        val data = presenter.getDataSetMainStatisticHorizontalBarChart(chartStatistic)
        data.setValueFormatter(ChartLargeValueFormatter(!showOnlyIntegers))
        chartStatistic.data = data
        chartStatistic.animateXY(2000, 2000)
        chartStatistic.invalidate()
    }

    private fun setStatisticPieChartData() {
        val data = presenter.getDataSetMainStatisticPieChart(chartStatisticPie)
        data.setValueFormatter(ChartLargeValueFormatter(!showOnlyIntegers))
        chartStatisticPie.data = data
        chartStatisticPie.animateXY(2000, 2000)
        chartStatisticPie.invalidate()
    }

    private fun setStatisticSumTV() {
        tvStatisticSum.text = presenter.formattedStatistic
    }

    private fun setBalanceTV(balance: DoubleArray) {
        tvBalanceSum.text = presenter.getFormattedBalance(balance)
    }

    private fun setBalance(posCurrency: Int) {
        val balance = presenter.getCurrentBalance(posCurrency)
        setBalanceTV(balance)
        updateBalanceChartSection(balance)
    }

    private fun updateBalanceChartSection(balance: DoubleArray) {
        if (presenter.isBalanceEmpty(balance)) {
            tvBalanceNoData.visibility = View.VISIBLE
            chartBalance.visibility = View.GONE
        } else {
            tvBalanceNoData.visibility = View.GONE
            chartBalance.visibility = View.VISIBLE
            setBalanceBarChartData(balance)
        }
    }

    private fun updateStatisticChartsSection() {
        if (presenter.isStatisticEmpty) {
            tvStatisticNoData.visibility = View.VISIBLE
            chartStatisticPie.visibility = View.GONE
            chartStatistic.visibility = View.GONE
        } else {
            tvStatisticNoData.visibility = View.GONE
            when (spinChartType.selectedItemPosition) {
                0 -> {
                    chartStatistic.visibility = View.VISIBLE
                    setStatisticBarChartData()
                }
                1 -> {
                    chartStatisticPie.visibility = View.VISIBLE
                    setStatisticPieChartData()
                }
            }
        }
    }

    override fun setBalanceAndStatistic() {
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticBarChartData()
        setStatisticSumTV()
        setChartTypeSpinner()
    }

    override fun updateBalanceAndStatistic() {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticSumTV()
        updateStatisticChartsSection()
    }

    override fun updateBalanceAndStatisticAfterDBImport() {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticSumTV()
        updateStatisticChartsSection()
        EventBus.getDefault().post(UpdateFrgTransactions())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    override fun updateBalance() {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
    }

    override fun updateStatistic() {
        setStatisticSumTV()
        updateStatisticChartsSection()
    }

    override val isNeedToConvert: Boolean
        get() = convert

    override val balanceCurrencyPosition: Int
        get() = spinBalanceCurrency.selectedItemPosition
}
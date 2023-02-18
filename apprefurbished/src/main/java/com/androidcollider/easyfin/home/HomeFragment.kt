package com.androidcollider.easyfin.home

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.util.Pair
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
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
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
    private lateinit var tvNoData: TextView
    private lateinit var tvBalance: TextView
    private lateinit var ivBalanceSettings: ImageView
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
    private var spinChartTypeNotInitSelectedItemCall = false

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

    override fun getContentView(): Int {
        return R.layout.frg_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).component.inject(this)
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
        tvNoData = view.findViewById(R.id.tvMainNoData)
        tvBalance = view.findViewById(R.id.tvMainCurrentBalance)
        ivBalanceSettings = view.findViewById(R.id.ivMainBalanceSettings)
        chartStatistic = view.findViewById(R.id.chartHBarMainStatistic)
        chartBalance = view.findViewById(R.id.chartMainBalance)
        chartStatisticPie = view.findViewById(R.id.chartPieMainStatistic)

        ivBalanceSettings.setOnClickListener { balanceSettingsDialog.show() }

        setupCharts()
        buildBalanceSettingsDialog()

        val balanceSettings = balanceSettingsDialog.getCustomView()
        chkBoxConvert = balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsConvert)
        chkBoxShowOnlyIntegers = balanceSettings.findViewById(R.id.checkBoxMainBalanceSettingsShowCents)
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
            checkStatChartTypeForUpdate()
        }
        chkBoxShowOnlyIntegers.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            sharedPrefManager.mainBalanceSettingsShowOnlyIntegersCheck = b
            showOnlyIntegers = b
            setBalance(spinBalanceCurrency.selectedItemPosition)
            checkStatChartTypeForUpdate()
        }
        ratesInfoManager.setupMultiTapListener(tvBalance, activity)

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
                resourcesManager.getIconArray(ResourcesManager.ICON_FLAGS))
            spinBalanceCurrency.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    if (spinBalanceCurrencyNotInitSelectedItemCall) {
                        setBalance(i)
                        presenter.updateTransactionStatisticArray(i)
                        setStatisticSumTV()
                        checkStatChartTypeForUpdate()
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
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
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
        activity.let {
            spinChartType.adapter = SpinIconTextHeadAdapter(
                activity,
                R.layout.spin_head_icon_text_main_chart,
                R.id.tvSpinHeadIconTextMainChart,
                R.id.ivSpinHeadIconTextMainChart,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_CHART_TYPE),
                resourcesManager.getIconArray(ResourcesManager.ICON_CHART_TYPE))
            spinChartType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                    if (spinChartTypeNotInitSelectedItemCall) {
                        if (i == 1) {
                            chartStatistic.visibility = View.GONE
                            if (presenter.isStatisticEmpty) tvNoData.visibility = View.VISIBLE else {
                                chartStatisticPie.visibility = View.VISIBLE
                                setStatisticPieChartData()
                            }
                        } else {
                            tvNoData.visibility = View.GONE
                            chartStatisticPie.visibility = View.GONE
                            chartStatistic.visibility = View.VISIBLE
                            setStatisticBarChartData()
                        }
                        sharedPrefManager.homeChartTypePos = i
                    } else {
                        spinChartTypeNotInitSelectedItemCall = true
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

            //spinChartType.setSelection(sharedPrefManager.getHomeChartTypePos());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHome?) {
        presenter.updateBalanceAndStatistic(spinPeriod.selectedItemPosition + 1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHomeBalance?) {
        presenter.updateBalance()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: UpdateFrgHomeNewRates?) {
        presenter.updateRates()
        ratesInfoManager.prepareInfo()
        if (convert) {
            setBalance(spinBalanceCurrency.selectedItemPosition)
            presenter.updateTransactionStatisticArray(spinBalanceCurrency.selectedItemPosition)
            setStatisticSumTV()
            checkStatChartTypeForUpdate()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: DBImported?) {
        presenter.updateBalanceAndStatisticAfterDBImport(spinPeriod.selectedItemPosition + 1)
    }

    private fun setupCharts() {
        chartSetupManager.setupMainBarChart(chartBalance)
        chartSetupManager.setupMainBarChart(chartStatistic)
        chartSetupManager.setupMainPieChart(chartStatisticPie)
    }

    private fun setBalanceBarChartData(balance: DoubleArray) {
        val data = presenter.getDataSetMainBalanceHorizontalBarChart(balance)
        data.setValueFormatter(ChartLargeValueFormatter(!showOnlyIntegers))
        chartBalance.data = data
        chartBalance.animateXY(2000, 2000)
        chartBalance.invalidate()
    }

    private fun setStatisticBarChartData() {
        val data = presenter.dataSetMainStatisticHorizontalBarChart
        data.setValueFormatter(ChartLargeValueFormatter(!showOnlyIntegers))
        chartStatistic.data = data
        chartStatistic.animateXY(2000, 2000)
        chartStatistic.invalidate()
    }

    private fun setStatisticPieChartData() {
        val data = presenter.dataSetMainStatisticPieChart
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
        setBalanceBarChartData(balance)
    }

    private fun checkStatChartTypeForUpdate() {
        when (spinChartType.selectedItemPosition) {
            0 -> setStatisticBarChartData()
            1 -> if (presenter.isStatisticEmpty) {
                tvNoData.visibility = View.VISIBLE
                chartStatisticPie.visibility = View.GONE
            } else {
                tvNoData.visibility = View.GONE
                chartStatisticPie.visibility = View.VISIBLE
                setStatisticPieChartData()
            }
        }
    }

    override fun setBalanceAndStatistic(pair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>) {
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticBarChartData()
        setStatisticSumTV()
        setChartTypeSpinner()
    }

    override fun updateBalanceAndStatistic(pair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>) {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticSumTV()
        checkStatChartTypeForUpdate()
    }

    override fun updateBalanceAndStatisticAfterDBImport(mapPair: Pair<Map<String, DoubleArray>, Map<String, DoubleArray>>) {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
        setStatisticSumTV()
        checkStatChartTypeForUpdate()
        EventBus.getDefault().post(UpdateFrgTransactions())
        EventBus.getDefault().post(UpdateFrgAccounts())
    }

    override fun updateBalance(map: Map<String, DoubleArray>) {
        ratesInfoManager.prepareInfo()
        setBalance(spinBalanceCurrency.selectedItemPosition)
    }

    override fun updateStatistic(map: Map<String, DoubleArray>) {
        setStatisticSumTV()
        checkStatChartTypeForUpdate()
    }

    override fun isNeedToConvert(): Boolean {
        return convert
    }

    override fun getBalanceCurrencyPosition(): Int {
        return spinBalanceCurrency.selectedItemPosition
    }
}
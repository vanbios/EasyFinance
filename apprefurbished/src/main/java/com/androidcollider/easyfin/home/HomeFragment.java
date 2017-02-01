package com.androidcollider.easyfin.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeBalance;
import com.androidcollider.easyfin.common.events.UpdateFrgHomeNewRates;
import com.androidcollider.easyfin.common.managers.chart.setup.ChartSetupManager;
import com.androidcollider.easyfin.common.managers.rates.rates_info.RatesInfoManager;
import com.androidcollider.easyfin.common.managers.resources.ResourcesManager;
import com.androidcollider.easyfin.common.managers.shared_pref.SharedPrefManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.adapters.SpinIconTextHeadAdapter;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragmentWithEvents;
import com.androidcollider.easyfin.common.utils.ChartLargeValueFormatter;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

/**
 * @author Ihor Bilous
 */

public class HomeFragment extends CommonFragmentWithEvents implements HomeMVP.View {

    @BindView(R.id.spinMainPeriod)
    Spinner spinPeriod;
    @BindView(R.id.spinMainCurrency)
    Spinner spinBalanceCurrency;
    @BindView(R.id.spinMainChart)
    Spinner spinChartType;
    @BindView(R.id.tvMainStatisticSum)
    TextView tvStatisticSum;
    @BindView(R.id.tvMainSumValue)
    TextView tvBalanceSum;
    @BindView(R.id.tvMainNoData)
    TextView tvNoData;
    @BindView(R.id.tvMainCurrentBalance)
    TextView tvBalance;
    @BindView(R.id.ivMainBalanceSettings)
    ImageView ivBalanceSettings;
    @BindView(R.id.chartHBarMainStatistic)
    HorizontalBarChart chartStatistic;
    @BindView(R.id.chartMainBalance)
    HorizontalBarChart chartBalance;
    @BindView(R.id.chartPieMainStatistic)
    PieChart chartStatisticPie;
    CheckBox chkBoxConvert;
    CheckBox chkBoxShowOnlyIntegers;

    private MaterialDialog balanceSettingsDialog;

    private boolean convert, showOnlyIntegers,

    // we can prevent init spinner issue with subscribing
    // to listeners in onRestoreInstanceState
    spinPeriodNotInitSelectedItemCall,
            spinBalanceCurrencyNotInitSelectedItemCall,
            spinChartTypeNotInitSelectedItemCall;

    @Inject
    RatesInfoManager ratesInfoManager;

    @Inject
    SharedPrefManager sharedPrefManager;

    @Inject
    ResourcesManager resourcesManager;

    @Inject
    ChartSetupManager chartSetupManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    HomeMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViewsAndRes();

        setBalanceCurrencySpinner();
        setStatisticPeriodSpinner();

        presenter.setView(this);
        presenter.loadBalanceAndStatistic(spinPeriod.getSelectedItemPosition() + 1);
    }

    private void initializeViewsAndRes() {
        setupCharts();

        buildBalanceSettingsDialog();

        View balanceSettings = balanceSettingsDialog.getCustomView();

        if (balanceSettings != null) {
            chkBoxConvert = findById(balanceSettings, R.id.checkBoxMainBalanceSettingsConvert);
            chkBoxShowOnlyIntegers = findById(balanceSettings, R.id.checkBoxMainBalanceSettingsShowCents);
        }

        convert = sharedPrefManager.getMainBalanceSettingsConvertCheck();
        showOnlyIntegers = sharedPrefManager.getMainBalanceSettingsShowOnlyIntegersCheck();

        chkBoxConvert.setChecked(convert);
        chkBoxShowOnlyIntegers.setChecked(showOnlyIntegers);

        chkBoxConvert.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPrefManager.setMainBalanceSettingsConvertCheck(b);
            convert = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());

            presenter.updateTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
            setStatisticSumTV();
            checkStatChartTypeForUpdate();
        });

        chkBoxShowOnlyIntegers.setOnCheckedChangeListener((compoundButton, b) -> {
            sharedPrefManager.setMainBalanceSettingsShowOnlyIntegersCheck(b);
            showOnlyIntegers = b;
            setBalance(spinBalanceCurrency.getSelectedItemPosition());
            checkStatChartTypeForUpdate();
        });

        ratesInfoManager.setupMultiTapListener(tvBalance, getActivity());
    }

    private void buildBalanceSettingsDialog() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            balanceSettingsDialog = dialogManager.buildBalanceSettingsDialog(activity);
        }
    }

    private void setBalanceCurrencySpinner() {
        spinBalanceCurrency.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main,
                R.id.tvSpinHeadIconTextMain,
                R.id.ivSpinHeadIconTextMain,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_ACCOUNT_CURRENCY),
                resourcesManager.getIconArray(ResourcesManager.ICON_FLAGS)));

        spinBalanceCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinBalanceCurrencyNotInitSelectedItemCall) {
                    setBalance(i);
                    presenter.updateTransactionStatisticArray(i);
                    setStatisticSumTV();
                    checkStatChartTypeForUpdate();
                    sharedPrefManager.setHomeBalanceCurrencyPos(i);
                } else {
                    spinBalanceCurrencyNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinBalanceCurrency.setSelection(sharedPrefManager.getHomeBalanceCurrencyPos());
    }

    private void setStatisticPeriodSpinner() {
        ArrayAdapter<?> adapterStatPeriod = ArrayAdapter.createFromResource(
                getActivity(),
                ResourcesManager.STRING_MAIN_STATISTIC_PERIOD,
                R.layout.spin_head_text_medium
        );

        adapterStatPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPeriod.setAdapter(adapterStatPeriod);

        spinPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinPeriodNotInitSelectedItemCall) {
                    presenter.updateStatistic(i + 1);
                    sharedPrefManager.setHomePeriodPos(i);
                } else {
                    spinPeriodNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinPeriod.setSelection(sharedPrefManager.getHomePeriodPos());
    }

    private void setChartTypeSpinner() {
        spinChartType.setAdapter(new SpinIconTextHeadAdapter(
                getActivity(),
                R.layout.spin_head_icon_text_main_chart,
                R.id.tvSpinHeadIconTextMainChart,
                R.id.ivSpinHeadIconTextMainChart,
                R.layout.spin_drop_icon_text,
                R.id.tvSpinDropIconText,
                R.id.ivSpinDropIconText,
                resourcesManager.getStringArray(ResourcesManager.STRING_CHART_TYPE),
                resourcesManager.getIconArray(ResourcesManager.ICON_CHART_TYPE)));

        spinChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinChartTypeNotInitSelectedItemCall) {
                    if (i == 1) {
                        chartStatistic.setVisibility(View.GONE);
                        if (presenter.isStatisticEmpty())
                            tvNoData.setVisibility(View.VISIBLE);
                        else {
                            chartStatisticPie.setVisibility(View.VISIBLE);
                            setStatisticPieChartData();
                        }
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        chartStatisticPie.setVisibility(View.GONE);
                        chartStatistic.setVisibility(View.VISIBLE);
                        setStatisticBarChartData();
                    }
                    sharedPrefManager.setHomeChartTypePos(i);
                } else {
                    spinChartTypeNotInitSelectedItemCall = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinChartType.setSelection(sharedPrefManager.getHomeChartTypePos());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHome event) {
        presenter.updateBalanceAndStatistic(spinPeriod.getSelectedItemPosition() + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHomeBalance event) {
        presenter.updateBalance();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateFrgHomeNewRates event) {
        presenter.updateRates();
        ratesInfoManager.prepareInfo();

        if (convert) {
            setBalance(spinBalanceCurrency.getSelectedItemPosition());
            presenter.updateTransactionStatisticArray(spinBalanceCurrency.getSelectedItemPosition());
            setStatisticSumTV();
            checkStatChartTypeForUpdate();
        }
    }

    private void setupCharts() {
        chartSetupManager.setupMainBarChart(chartBalance);
        chartSetupManager.setupMainBarChart(chartStatistic);
        chartSetupManager.setupMainPieChart(chartStatisticPie);
    }

    private void setBalanceBarChartData(double[] balance) {
        BarData data = presenter.getDataSetMainBalanceHorizontalBarChart(balance);
        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartBalance.setData(data);
        chartBalance.animateXY(2000, 2000);
        chartBalance.invalidate();
    }

    private void setStatisticBarChartData() {
        BarData data = presenter.getDataSetMainStatisticHorizontalBarChart();
        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartStatistic.setData(data);
        chartStatistic.animateXY(2000, 2000);
        chartStatistic.invalidate();
    }

    private void setStatisticPieChartData() {
        PieData data = presenter.getDataSetMainStatisticPieChart();
        data.setValueFormatter(new ChartLargeValueFormatter(!showOnlyIntegers));

        chartStatisticPie.setData(data);
        chartStatisticPie.animateXY(2000, 2000);
        chartStatisticPie.invalidate();
    }

    private void setStatisticSumTV() {
        tvStatisticSum.setText(presenter.getFormattedStatistic());
    }

    private void setBalanceTV(double[] balance) {
        tvBalanceSum.setText(presenter.getFormattedBalance(balance));
    }

    private void setBalance(int posCurrency) {
        double[] balance = presenter.getCurrentBalance(posCurrency);
        setBalanceTV(balance);
        setBalanceBarChartData(balance);
    }

    private void checkStatChartTypeForUpdate() {
        switch (spinChartType.getSelectedItemPosition()) {
            case 0:
                setStatisticBarChartData();
                break;
            case 1:
                if (presenter.isStatisticEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                    chartStatisticPie.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    chartStatisticPie.setVisibility(View.VISIBLE);
                    setStatisticPieChartData();
                }
                break;
        }
    }

    @OnClick({R.id.ivMainBalanceSettings})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMainBalanceSettings:
                balanceSettingsDialog.show();
                break;
        }
    }


    @Override
    public void setBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
        setBalance(spinBalanceCurrency.getSelectedItemPosition());
        setStatisticBarChartData();
        setStatisticSumTV();
        setChartTypeSpinner();
    }

    @Override
    public void updateBalanceAndStatistic(Pair<Map<String, double[]>, Map<String, double[]>> pair) {
        ratesInfoManager.prepareInfo();
        setBalance(spinBalanceCurrency.getSelectedItemPosition());
        setStatisticSumTV();
        checkStatChartTypeForUpdate();
    }

    @Override
    public void updateBalance(Map<String, double[]> map) {
        ratesInfoManager.prepareInfo();
        setBalance(spinBalanceCurrency.getSelectedItemPosition());
    }

    @Override
    public void updateStatistic(Map<String, double[]> map) {
        setStatisticSumTV();
        checkStatChartTypeForUpdate();
    }

    @Override
    public boolean isNeedToConvert() {
        return convert;
    }

    @Override
    public int getBalanceCurrencyPosition() {
        return spinBalanceCurrency.getSelectedItemPosition();
    }
}
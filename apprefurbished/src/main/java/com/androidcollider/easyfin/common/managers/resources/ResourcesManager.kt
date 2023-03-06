package com.androidcollider.easyfin.common.managers.resources;

import android.content.Context;
import android.content.res.TypedArray;

import com.androidcollider.easyfin.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ihor Bilous
 */

public class ResourcesManager {

    public static final int STRING_NAVIGATION_DRAWER_MENU = R.array.navigation_drawer_string_array;
    public static final int STRING_JSON_RATES = R.array.json_rates_array;
    public static final int STRING_CHART_TYPE = R.array.chart_type_array;
    public static final int STRING_MAIN_STATISTIC_PERIOD = R.array.main_statistic_period_array;
    public static final int STRING_ACCOUNT_CURRENCY = R.array.account_currency_array;
    public static final int STRING_ACCOUNT_CURRENCY_LANG = R.array.account_currency_array_language;
    public static final int STRING_ACCOUNT_TYPE = R.array.account_type_array;
    public static final int STRING_TRANSACTION_CATEGORY_INCOME = R.array.transaction_category_income_array;
    public static final int STRING_TRANSACTION_CATEGORY_EXPENSE = R.array.transaction_category_expense_array;

    public static final int ICON_TRANSACTION_CATEGORY_EXPENSE = R.array.transaction_category_expense_icons;
    public static final int ICON_TRANSACTION_CATEGORY_INCOME = R.array.transaction_category_income_icons;
    public static final int ICON_FLAGS = R.array.flag_icons;
    public static final int ICON_ACCOUNT_TYPE = R.array.account_type_icons;
    public static final int ICON_CHART_TYPE = R.array.charts_main_icons;
    public static final int ICON_NAVIGATION_DRAWER_MENU = R.array.navigation_drawer_icons_array;

    private final Context context;
    private final Map<Integer, String[]> stringArrayResMap;
    private final Map<Integer, TypedArray> iconArrayResMap;

    ResourcesManager(Context context) {
        this.context = context;
        stringArrayResMap = new HashMap<>();
        iconArrayResMap = new HashMap<>();
    }

    public String[] getStringArray(int resId) {
        if (!stringArrayResMap.containsKey(resId)) {
            stringArrayResMap.put(resId, context.getResources().getStringArray(resId));
        }
        return stringArrayResMap.get(resId);
    }

    public TypedArray getIconArray(int resId) {
        if (!iconArrayResMap.containsKey(resId)) {
            iconArrayResMap.put(resId, context.getResources().obtainTypedArray(resId));
        }
        return iconArrayResMap.get(resId);
    }
}
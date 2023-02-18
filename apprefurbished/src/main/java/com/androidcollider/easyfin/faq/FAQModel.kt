package com.androidcollider.easyfin.faq;

import android.content.Context;
import android.util.Pair;

import com.androidcollider.easyfin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ihor Bilous
 */

class FAQModel implements FAQMVP.Model {

    private Context context;


    FAQModel(Context context) {
        this.context = context;
    }

    @Override
    public List<Pair<String, String>> getInfo() {
        List<Pair<String, String>> list = new ArrayList<>();
        list.add(new Pair<>(context.getString(R.string.general_info), context.getString(R.string.faq_about_app)));
        list.add(new Pair<>(context.getString(R.string.tab_accounts), context.getString(R.string.faq_about_accounts)));
        list.add(new Pair<>(context.getString(R.string.tab_transactions), context.getString(R.string.faq_about_transactions)));
        list.add(new Pair<>(context.getString(R.string.debts), context.getString(R.string.faq_about_debts)));
        list.add(new Pair<>(context.getString(R.string.tab_home), context.getString(R.string.faq_about_home)));
        return list;
    }
}
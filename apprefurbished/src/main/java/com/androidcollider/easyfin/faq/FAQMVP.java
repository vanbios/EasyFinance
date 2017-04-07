package com.androidcollider.easyfin.faq;

import android.util.Pair;

import java.util.List;

/**
 * @author Ihor Bilous
 */

public interface FAQMVP {

    interface Model {

        List<Pair<String, String>> getInfo();
    }

    interface View {

        void setInfo(List<Pair<String, String>> list);
    }

    interface Presenter {

        void setView(FAQMVP.View view);

        void loadInfo();
    }
}
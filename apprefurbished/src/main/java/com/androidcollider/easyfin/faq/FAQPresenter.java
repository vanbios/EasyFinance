package com.androidcollider.easyfin.faq;

import androidx.annotation.Nullable;

/**
 * @author Ihor Bilous
 */

public class FAQPresenter implements FAQMVP.Presenter {

    @Nullable
    private FAQMVP.View view;
    private FAQMVP.Model model;


    public FAQPresenter(FAQMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(@Nullable FAQMVP.View view) {
        this.view = view;
    }

    @Override
    public void loadInfo() {
        if (view != null) {
            view.setInfo(model.getInfo());
        }
    }
}
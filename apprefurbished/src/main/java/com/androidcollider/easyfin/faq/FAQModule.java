package com.androidcollider.easyfin.faq;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * @author Ihor Bilous
 */

@Module
public class FAQModule {

    @Provides
    FAQMVP.Presenter provideFAQMVPPresenter(FAQMVP.Model model) {
        return new FAQPresenter(model);
    }

    @Provides
    FAQMVP.Model provideFAQMVPModel(Context context) {
        return new FAQModel(context);
    }
}
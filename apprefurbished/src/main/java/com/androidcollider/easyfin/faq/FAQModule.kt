package com.androidcollider.easyfin.faq

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author Ihor Bilous
 */
@Module
class FAQModule {
    @Provides
    fun provideFAQMVPPresenter(model: FAQMVP.Model?): FAQMVP.Presenter {
        return FAQPresenter(model)
    }

    @Provides
    fun provideFAQMVPModel(context: Context?): FAQMVP.Model {
        return FAQModel(context)
    }
}
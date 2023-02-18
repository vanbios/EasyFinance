package com.androidcollider.easyfin.faq

/**
 * @author Ihor Bilous
 */
class FAQPresenter(private val model: FAQMVP.Model) : FAQMVP.Presenter {
    private var view: FAQMVP.View? = null
    override fun setView(view: FAQMVP.View?) {
        this.view = view
    }

    override fun loadInfo() {
        view?.setInfo(model.info)
    }
}
package com.androidcollider.easyfin.faq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Ihor Bilous
 */

public class FAQFragment extends CommonFragment implements FAQMVP.View {

    @BindView(R.id.rvFAQ)
    RecyclerView recyclerView;

    @Inject
    FAQMVP.Presenter presenter;


    @Override
    public int getContentView() {
        return R.layout.frg_faq;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);
        presenter.loadInfo();
    }

    @Override
    public void setInfo(List<Pair<String, String>> list) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new RecyclerFAQAdapter(list));
    }

    @Override
    public String getTitle() {
        return getString(R.string.app_faq);
    }
}
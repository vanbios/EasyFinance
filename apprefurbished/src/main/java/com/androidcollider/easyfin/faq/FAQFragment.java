package com.androidcollider.easyfin.faq;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.ui.fragments.common.CommonFragment;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class FAQFragment extends CommonFragment implements FAQMVP.View {

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
        setupUI(view);
        presenter.setView(this);
        presenter.loadInfo();
    }

    private void setupUI(View view) {
        recyclerView = view.findViewById(R.id.rvFAQ);
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
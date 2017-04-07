package com.androidcollider.easyfin.mockito.faq;

import android.util.Pair;

import com.androidcollider.easyfin.faq.FAQMVP;
import com.androidcollider.easyfin.faq.FAQPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ihor Bilous
 */

public class FAQPresenterTests {

    private FAQMVP.Model mockModel;
    private FAQMVP.View mockView;
    private FAQMVP.Presenter presenter;


    @Before
    public void setup() {
        mockModel = mock(FAQMVP.Model.class);
        mockView = mock(FAQMVP.View.class);
        presenter = new FAQPresenter(mockModel);
        presenter.setView(mockView);
    }

    @Test
    public void testLoadInfoViewNotNull() {
        List<Pair<String, String>> infoList = getTestInfoList();

        when(mockModel.getInfo()).thenReturn(infoList);

        presenter.loadInfo();

        verify(mockModel, times(1)).getInfo();

        verify(mockView, times(1)).setInfo(infoList);
        verify(mockView).setInfo(ArgumentMatchers.eq(infoList));
    }

    @Test
    public void testLoadInfoViewNull() {
        List<Pair<String, String>> infoList = getTestInfoList();

        when(mockModel.getInfo()).thenReturn(infoList);

        presenter.setView(null);

        presenter.loadInfo();

        verify(mockModel, never()).getInfo();

        verify(mockView, never()).setInfo(infoList);
    }

    private List<Pair<String, String>> getTestInfoList() {
        List<Pair<String, String>> infoList = new ArrayList<>();
        infoList.add(new Pair<>("test1", "test1"));
        infoList.add(new Pair<>("test2", "test2"));
        infoList.add(new Pair<>("test3", "test3"));

        return infoList;
    }
}
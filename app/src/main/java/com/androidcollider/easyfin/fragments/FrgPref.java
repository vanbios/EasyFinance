package com.androidcollider.easyfin.fragments;


import android.os.Bundle;

import com.androidcollider.easyfin.R;

public class FrgPref extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    public String getTitle() {
        return getString(R.string.settings);
    }

}

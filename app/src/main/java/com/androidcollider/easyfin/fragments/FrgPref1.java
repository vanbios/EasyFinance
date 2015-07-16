package com.androidcollider.easyfin.fragments;


import android.os.Bundle;

import com.androidcollider.easyfin.R;

public class FrgPref1 extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref1);
    }

}

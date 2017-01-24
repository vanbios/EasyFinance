package com.androidcollider.easyfin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.fragments.common.PreferenceFragment;
import com.androidcollider.easyfin.managers.import_export_db.ImportExportDbManager;
import com.androidcollider.easyfin.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.repository.database.DbHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class FrgPref extends PreferenceFragment {

    private static final int FILE_SELECT_CODE = 0;
    private static Uri uri;
    private final Context context = App.getContext();
    private Preference exportDBPref, importDBPref;
    private Tracker mTracker;

    @Inject
    ImportExportDbManager importExportDbManager;

    @Inject
    ToastManager toastManager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ((App) getActivity().getApplication()).getComponent().inject(this);

        initializePrefs();

        mTracker = App.tracker();
        mTracker.setScreenName(this.getClass().getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initializePrefs() {
        exportDBPref = findPreference("export_db");
        exportDBPref.setOnPreferenceClickListener(preference -> {
            exportDBPref.setEnabled(false);
            importExportDbManager.backupDatabase();

            mTracker.send(new HitBuilders.EventBuilder("click", "export")
                    .setLabel("export_db")
                    .build());
            return false;
        });

        importDBPref = findPreference("import_db");
        importDBPref.setOnPreferenceClickListener(preference -> {
            openFileExplorer();

            mTracker.send(new HitBuilders.EventBuilder("open", "file_explorer")
                    .setLabel("open_file_explorer")
                    .build());
            return false;
        });
    }

    private void showDialogImportDB() {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.import_db))
                .content(getString(R.string.import_dialog_warning))
                .positiveText(getString(R.string.confirm))
                .negativeText(getString(R.string.cancel))
                .onPositive((dialog, which) -> {
                    importDB();
                    mTracker.send(new HitBuilders.EventBuilder("click", "import")
                            .setLabel("import_confirm")
                            .build());
                })
                .cancelable(false)
                .show();
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            toastManager.showClosableToast(context, getString(R.string.import_no_file_explorer), ToastManager.LONG);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == -1) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    if (!uri.toString().contains(DbHelper.DATABASE_NAME)) {
                        toastManager.showClosableToast(context, getString(R.string.import_wrong_file_type), ToastManager.LONG);
                    } else {
                        try {
                            showDialogImportDB();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importDB() {
        boolean importDB = false;
        try {
            importDB = importExportDbManager.importDatabase(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (importDB) {
            importDBPref.setEnabled(false);
            pushBroadcast();
        }
        toastManager.showClosableToast(context,
                importDB ? getString(R.string.import_complete) : getString(R.string.import_error), ToastManager.LONG);
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());
        //EventBus.getDefault().post(new UpdateFrgHomeNewRates());

        //InMemoryRepository.getInstance().setRatesForExchange();
        //InMemoryRepository.getInstance().updateAccountList();
    }

    @Override
    public String getTitle() {
        return getString(R.string.settings);
    }
}
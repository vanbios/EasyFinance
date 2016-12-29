package com.androidcollider.easyfin.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidcollider.easyfin.AppController;
import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.database.DbHelper;
import com.androidcollider.easyfin.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.events.UpdateFrgDebts;
import com.androidcollider.easyfin.events.UpdateFrgHome;
import com.androidcollider.easyfin.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.objects.InfoFromDB;
import com.androidcollider.easyfin.utils.DBExportImportUtils;
import com.androidcollider.easyfin.utils.ToastUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class FrgPref extends PreferenceFragment {

    private static final int FILE_SELECT_CODE = 0;
    private static Uri uri;
    private final Context context = AppController.getContext();
    private Preference exportDBPref, importDBPref;
    private Tracker mTracker;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        initializePrefs();

        mTracker = AppController.tracker();
        mTracker.setScreenName(this.getClass().getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initializePrefs() {
        exportDBPref = findPreference("export_db");
        exportDBPref.setOnPreferenceClickListener(preference -> {
            exportDBPref.setEnabled(false);
            DBExportImportUtils.backupDB();

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
            ToastUtils.showClosableToast(context, getString(R.string.import_no_file_explorer), 2);
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
                        ToastUtils.showClosableToast(context, getString(R.string.import_wrong_file_type), 2);
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
            importDB = InfoFromDB.getInstance().getDataSource().importDatabase(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (importDB) {
            importDBPref.setEnabled(false);
            pushBroadcast();
        }
        ToastUtils.showClosableToast(context, importDB ? getString(R.string.import_complete) : getString(R.string.import_error), 2);
    }

    private void pushBroadcast() {
        EventBus.getDefault().post(new UpdateFrgHome());
        EventBus.getDefault().post(new UpdateFrgTransactions());
        EventBus.getDefault().post(new UpdateFrgAccounts());
        EventBus.getDefault().post(new UpdateFrgDebts());

        InfoFromDB.getInstance().setRatesForExchange();
        InfoFromDB.getInstance().updateAccountList();
    }

    @Override
    public String getTitle() {
        return getString(R.string.settings);
    }
}
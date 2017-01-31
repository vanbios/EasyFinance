package com.androidcollider.easyfin.common.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.androidcollider.easyfin.R;
import com.androidcollider.easyfin.common.app.App;
import com.androidcollider.easyfin.common.events.UpdateFrgAccounts;
import com.androidcollider.easyfin.common.events.UpdateFrgDebts;
import com.androidcollider.easyfin.common.events.UpdateFrgHome;
import com.androidcollider.easyfin.common.events.UpdateFrgTransactions;
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsManager;
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager;
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager;
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager;
import com.androidcollider.easyfin.common.repository.database.DbHelper;
import com.androidcollider.easyfin.common.ui.MainActivity;
import com.androidcollider.easyfin.common.ui.fragments.common.PreferenceFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import javax.inject.Inject;

/**
 * @author Ihor Bilous
 */

public class PrefFragment extends PreferenceFragment {

    private static final int FILE_SELECT_CODE = 0;
    private static Uri uri;
    private Preference exportDBPref, importDBPref;

    @Inject
    ImportExportDbManager importExportDbManager;

    @Inject
    ToastManager toastManager;

    @Inject
    DialogManager dialogManager;

    @Inject
    AnalyticsManager analyticsManager;

    @Inject
    Context context;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ((App) getActivity().getApplication()).getComponent().inject(this);

        initializePrefs();

        analyticsManager.sendScreeName(this.getClass().getName());
    }

    private void initializePrefs() {
        exportDBPref = findPreference("export_db");
        exportDBPref.setOnPreferenceClickListener(preference -> {
            exportDBPref.setEnabled(false);
            importExportDbManager.backupDatabase();

            analyticsManager.sendAction("click", "export", "export_db");
            return false;
        });

        importDBPref = findPreference("import_db");
        importDBPref.setOnPreferenceClickListener(preference -> {
            openFileExplorer();

            analyticsManager.sendAction("open", "file_explorer", "open_file_explorer");
            return false;
        });
    }

    private void showDialogImportDB() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            dialogManager.showImportDBDialog(
                    activity,
                    (dialog, which) -> {
                        importDB();

                        analyticsManager.sendAction("click", "import", "import_confirm");
                    }
            );
        }
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
    }

    @Override
    public String getTitle() {
        return getString(R.string.settings);
    }
}
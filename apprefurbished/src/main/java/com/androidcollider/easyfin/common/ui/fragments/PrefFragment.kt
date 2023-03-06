package com.androidcollider.easyfin.common.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.androidcollider.easyfin.R
import com.androidcollider.easyfin.common.app.App
import com.androidcollider.easyfin.common.events.DBImported
import com.androidcollider.easyfin.common.managers.analytics.AnalyticsManager
import com.androidcollider.easyfin.common.managers.import_export_db.ImportExportDbManager
import com.androidcollider.easyfin.common.managers.ui.dialog.DialogManager
import com.androidcollider.easyfin.common.managers.ui.toast.ToastManager
import com.androidcollider.easyfin.common.repository.database.DbHelper
import com.androidcollider.easyfin.common.ui.MainActivity
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import javax.inject.Inject

/**
 * @author Ihor Bilous
 */
class PrefFragment : PreferenceFragmentCompat() {

    private var exportDBPref: Preference? = null
    private var importDBPref: Preference? = null

    @Inject
    lateinit var importExportDbManager: ImportExportDbManager

    @Inject
    lateinit var toastManager: ToastManager

    @Inject
    lateinit var dialogManager: DialogManager

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        (activity?.application as App).component?.inject(this)
        initializePrefs()
        analyticsManager.sendScreeName(this.javaClass.name)
    }

    private fun initializePrefs() {
        exportDBPref = findPreference("export_db")
        exportDBPref?.setOnPreferenceClickListener {
            exportDBPref?.isEnabled = false
            importExportDbManager.backupDatabase()
            analyticsManager.sendAction("click", "export", "export_db")
            false
        }
        importDBPref = findPreference("import_db")
        importDBPref?.setOnPreferenceClickListener {
            openFileExplorer()
            analyticsManager.sendAction("open", "file_explorer", "open_file_explorer")
            false
        }
    }

    private fun showDialogImportDB() {
        val activity = activity as MainActivity?
        if (activity != null) {
            dialogManager.showImportDBDialog(
                activity
            ) {
                importDB()
                analyticsManager.sendAction("click", "import", "import_confirm")
            }
        }
    }

    private fun openFileExplorer() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                FILE_SELECT_CODE
            )
        } catch (ex: ActivityNotFoundException) {
            context?.let {
                toastManager.showClosableToast(
                    it,
                    getString(R.string.import_no_file_explorer), ToastManager.LONG
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == -1) {
                // Get the Uri of the selected file
                uri = data!!.data
                if (!uri.toString().contains(DbHelper.DATABASE_NAME)) {
                    context?.let {
                        toastManager.showClosableToast(
                            it,
                            getString(R.string.import_wrong_file_type), ToastManager.LONG
                        )
                    }

                } else {
                    try {
                        showDialogImportDB()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun importDB() {
        var importDB = false
        try {
            importDB = importExportDbManager.importDatabase(uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (importDB) {
            importDBPref!!.isEnabled = false
            pushBroadcast()
        }
        context?.let {
            toastManager.showClosableToast(
                it,
                if (importDB) getString(R.string.import_complete)
                else getString(R.string.import_error),
                ToastManager.LONG
            )
        }
    }

    private fun pushBroadcast() {
        EventBus.getDefault().post(DBImported())
    }

    val title: String
        get() = getString(R.string.settings)

    companion object {
        private const val FILE_SELECT_CODE = 0
        private var uri: Uri? = null
    }
}
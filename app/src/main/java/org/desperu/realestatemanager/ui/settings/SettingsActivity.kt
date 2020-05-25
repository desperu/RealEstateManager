package org.desperu.realestatemanager.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.alert_dialog.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseActivity
import org.desperu.realestatemanager.utils.MySharedPreferences
import org.desperu.realestatemanager.utils.*


/**
 * Activity to manage settings of the application.
 *
 * @constructor Instantiates a new SettingsActivity.
 */
class SettingsActivity : BaseActivity() {

    // FOR DATA
    private var isNotificationsEnabled = false
    private var disableUpdateNotification = false
    private var zoomLevel = 0
    private var isZoomButtonEnabled = false

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_settings

    override fun configureDesign() {
        configureToolBar()
        configureUpButton()
        savedPrefs
        updateUiWithSavedPrefs()
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onPause() {
        super.onPause()
        savePrefs()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Get saved prefs.
     */
    private val savedPrefs: Unit
        get() {
            isNotificationsEnabled = MySharedPreferences.getBoolean(baseContext, NOTIFICATION_ENABLED, NOTIFICATION_DEFAULT)
            disableUpdateNotification = MySharedPreferences.getBoolean(baseContext, DISABLE_UPDATE_NOTIFICATION, UPDATE_NOTIFICATION_DEFAULT)
            zoomLevel = MySharedPreferences.getInt(baseContext, MAP_ZOOM_LEVEL, ZOOM_LEVEL_DEFAULT)
            isZoomButtonEnabled = MySharedPreferences.getBoolean(baseContext, MAP_ZOOM_BUTTON, ZOOM_BUTTON_DEFAULT)
        }

    /**
     * Save current prefs.
     */
    private fun savePrefs() {
        MySharedPreferences.savePref(baseContext, NOTIFICATION_ENABLED, activity_settings_notification_switch!!.isChecked)
        MySharedPreferences.savePref(baseContext, DISABLE_UPDATE_NOTIFICATION, activity_settings_notification_disable_update_switch!!.isChecked)
        MySharedPreferences.savePref(baseContext, MAP_ZOOM_LEVEL, activity_settings_text_map_zoom_level_value!!.text.toString().toInt())
        MySharedPreferences.savePref(baseContext, MAP_ZOOM_BUTTON, activity_settings_map_zoom_button_switch!!.isChecked)
    }

    // --------------
    // ACTION
    // --------------

    /**
     * Notification checked change listener.
     */
//    private fun onCheckedNotificationChangeListener() {
//        activity_settings_notification_switch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
//            manageNotificationAlarm(isChecked) }
//        }
//    }
    /**
     * On click notification container.
     */
    fun onClickNotificationContainer(v: View) {
        activity_settings_notification_switch.isChecked = !activity_settings_notification_switch.isChecked
    }

    /**
     * On click disable notification update.
     */
    fun onClickDisableUpdate(v: View) {
        activity_settings_notification_disable_update_switch.isChecked =
                !activity_settings_notification_disable_update_switch.isChecked
    }

    /**
     * On click zoom size level.
     */
    fun onClickZoomSize(v: View) { alertDialog(ZOOM_DIALOG) }

    /**
     * On click map zoom button.
     */
    fun onClickMapZoomButton(v: View) {
        activity_settings_map_zoom_button_switch.isChecked = !activity_settings_map_zoom_button_switch.isChecked
    }

    /**
     * On click reset settings to default.
     */
    fun onclickResetSettings(v: View) { alertDialog(RESET_DIALOG) }

    // --------------
    // UI
    // --------------

    /**
     * Update ui with saved prefs data.
     */
    private fun updateUiWithSavedPrefs() {
        activity_settings_notification_switch.isChecked = isNotificationsEnabled
        activity_settings_notification_disable_update_switch.isChecked = disableUpdateNotification
        activity_settings_text_map_zoom_level_value.text = zoomLevel.toString()
        activity_settings_map_zoom_button_switch.isChecked = isZoomButtonEnabled
    }

    /**
     * Create alert dialog to set zoom value or confirm reset settings.
     * @param zoomOrReset Key to show corresponding dialog.
     */
    private fun alertDialog(zoomOrReset: Int) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        if (zoomOrReset == ZOOM_DIALOG) {
            // Create dialog for zoom level
            dialog.setTitle(R.string.activity_settings_text_map_zoom_size)
            dialog.setMessage(R.string.activity_settings_text_map_zoom_size_description)

            // Add edit text to dialog
            val editView: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog, alert_dialog_linear_root)
            val editText: EditText = editView.findViewById(R.id.alert_dialog_edit_text)
            editText.setText(zoomLevel.toString())
            editText.setSelection(editText.text.length)
            dialog.setView(editView)

            // Set positive button
            dialog.setPositiveButton(R.string.activity_settings_dialog_positive_button) { _, _ ->
                val newZoomLevel = editText.text.toString()
                if (newZoomLevel.isNotEmpty() && newZoomLevel.toInt() >= 2 && newZoomLevel.toInt() <= 21)
                    activity_settings_text_map_zoom_level_value.text = newZoomLevel
                else
                    Toast.makeText(this, R.string.activity_settings_toast_zoom_level_wrong_value, Toast.LENGTH_LONG).show()
            }
        } else if (zoomOrReset == RESET_DIALOG) {
            // Create dialog for reset settings
            dialog.setTitle(R.string.activity_settings_text_reset_settings)
            dialog.setMessage(R.string.activity_settings_dialog_reset_settings_message)

            // Set positive button
            dialog.setPositiveButton(R.string.activity_settings_dialog_positive_button) { _, _ -> resetSettings() }
        }
        // Set negative button
        dialog.setNegativeButton(R.string.activity_settings_dialog_negative_button) { dialog3, _ -> dialog3.cancel() }
        dialog.show()
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Reset settings to default value.
     */
    private fun resetSettings() {
        activity_settings_notification_switch.isChecked = NOTIFICATION_DEFAULT
        activity_settings_notification_disable_update_switch.isChecked = UPDATE_NOTIFICATION_DEFAULT
        activity_settings_text_map_zoom_level_value.setText(ZOOM_LEVEL_DEFAULT)
        activity_settings_map_zoom_button_switch.isChecked = ZOOM_BUTTON_DEFAULT
        Toast.makeText(baseContext, R.string.activity_settings_toast_reset_settings_default, Toast.LENGTH_SHORT).show()
    }
}
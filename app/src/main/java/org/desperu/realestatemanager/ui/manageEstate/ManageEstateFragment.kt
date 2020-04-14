package org.desperu.realestatemanager.ui.manageEstate

import android.Manifest.permission.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import icepick.State
import kotlinx.android.synthetic.main.fragment_estate_address.*
import kotlinx.android.synthetic.main.fragment_estate_data.*
import kotlinx.android.synthetic.main.fragment_estate_sale.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateImageBinding
import org.desperu.realestatemanager.extension.createDatePickerDialog
import org.desperu.realestatemanager.extension.toBitmap
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.StorageUtils.isExternalStorageWritable
import org.desperu.realestatemanager.utils.StorageUtils.setImageInStorage
import org.desperu.realestatemanager.utils.Utils.todayDate
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

/**
 * Fragments to manage estate : data, images, address, interest places and sale data.
 */
class ManageEstateFragment: BaseBindingFragment() {

    // FOR DATA
    @JvmField @State var fragmentKey: Int = -1
    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: ManageEstateViewModel

    /**
     * Companion object, used to create new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set fragmentKey.
         * @param fragmentKey the fragment key to configure
         * @return the new instance of ManageEstateFragment.
         */
        fun newInstance(fragmentKey: Int): ManageEstateFragment {
            val manageEstateFragment = ManageEstateFragment()
            manageEstateFragment.fragmentKey = fragmentKey
            return manageEstateFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureCorrespondingLayout()
    }

    // --------------------
    // METHODS OVERRIDE
    // --------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            onClickAddImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Calling the appropriate method after activity result
        handleResponse(requestCode, resultCode, data)
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, getFragmentLayout(), container, false)
        viewModel = (requireActivity() as ManageEstateActivity).getViewModel()

        binding.setVariable(org.desperu.realestatemanager.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure corresponding layout, depending of fragmentKey.
     */
    private fun configureCorrespondingLayout() {
        when (fragmentKey) {
            ESTATE_DATA -> configureSpinner(fragment_estate_data_spinner_type, R.array.estate_type_list)
            ESTATE_IMAGE -> configureImageRecycler()
            ESTATE_ADDRESS -> configureSpinner(fragment_estate_address_spinner_interest_places, R.array.estate_interest_places_list)
            ESTATE_SALE -> { configureSpinner(fragment_estate_sale_spinner_state, R.array.estate_state_list)
                             setPickerTextOnClickListener(context!!, fragment_estate_sale_date_picker_sale_date, viewModel.estate.value?.saleDate)
                             setPickerTextOnClickListener(context!!, fragment_estate_sale_date_picker_sold_out_date, viewModel.estate.value?.soldDate)
                           }
        }
    }

    /**
     * Configure estate spinner with adapter.
     * @param spinner Spinner to configure.
     * @param id String array id for spinner.
     */
    private fun configureSpinner(spinner: Spinner, @ArrayRes id: Int) {
//        fragment_estate_data_spinner_type.onItemSelectedListener = this
//        activity_manage_estate_spinner_type.startDragAndDrop() // TODO customize list ??
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(id))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    /**
     * Configure Linear Layout Manager for Image Recycler, and update image list.
     */
    private fun configureImageRecycler() {
        (binding as FragmentEstateImageBinding).fragmentEstateImageRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel.updateRecyclerImageList()
    }

    /**
     * Set picker text view on click listener to show date picker dialog.
     * @param context the context from this function is called.
     * @param pickerView the associated picker text view.
     * @param date the given string date, to set DatePickerDialog.
     */
    private fun setPickerTextOnClickListener(context: Context, pickerView: TextView, date: String?) {
        if (pickerView.tag == "saleDate") pickerView.text = todayDate()
        pickerView.setOnClickListener { createDatePickerDialog(context, pickerView, date) }
    }

    // -----------------
    // ACTION
    // -----------------

    /**
     * Set on click listener for add image floating button.
     */
    fun onClickAddImage() { alertDialogImage() }

    // -----------------
    // UI
    // -----------------

    /**
     * Create alert dialog to select photo action.
     */
    private fun alertDialogImage() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.AlertDialogTheme)
        dialog.setTitle(R.string.alert_dialog_image_title)
        dialog.setItems(arrayOf(
                getString(R.string.alert_dialog_image_button_choose),
                getString(R.string.alert_dialog_image_button_take))) {
                    dialog1, which -> when (which) {
                        0 -> chooseImageFromPhone()
                        1 -> takePhotoWithCamera()
                    }
                    dialog1.cancel()
                }
        dialog.show()
    }

    /**
     * Show Toast with corresponding message.
     * @param message Message to show.
     */
    private fun showToast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    // --------------------
    // IMAGE MANAGEMENT
    // --------------------

    /**
     * Choose image from phone, ask for permission before.
     */
    private fun chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(activity!!, READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.fragment_estate_image_popup_title_permission_files_access),
                    RC_PERMS_STORAGE, READ_EXTERNAL_STORAGE)
            return
        }
        // Launch an "Selection Image" Activity
        startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RC_CHOOSE_PHOTO)
    }

    /**
     * Take photo with phone camera, ask for permission before.
     */
    private fun takePhotoWithCamera() {
        if (!EasyPermissions.hasPermissions(activity!!, WRITE_EXTERNAL_STORAGE, CAMERA)) {
            EasyPermissions.requestPermissions(this, getString(R.string.fragment_estate_image_popup_title_permission_write_storage_and_camera),
                    RC_PERMS_PHOTO, WRITE_EXTERNAL_STORAGE, CAMERA)
            return
        }
        // Launch an "Take Photo" Activity
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), RC_TAKE_PHOTO)
    }

    // -----------------
    // DATA
    // -----------------

    /**
     * Save image in storage and add to image list.
     * @param bitmap the image bitmap to save and add to image list.
     */
    private fun saveImageAndAddToImageList(bitmap: Bitmap) {
        if (isExternalStorageWritable()) { // Check external storage access.
            val imageUri = setImageInStorage(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,
                    context!!, Date().time.toString() + ".jpg", FOLDER_NAME, bitmap)
            viewModel.addImageToImageList(imageUri)
        } else
            showToast(getString(R.string.fragment_estate_image_toast_error_write_external_storage))
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Get corresponding fragment layout, depending of fragmentKey value.
     */
    private fun getFragmentLayout() = when (fragmentKey) {
        ESTATE_DATA -> R.layout.fragment_estate_data
        ESTATE_IMAGE -> R.layout.fragment_estate_image
        ESTATE_ADDRESS -> R.layout.fragment_estate_address
        ESTATE_SALE -> R.layout.fragment_estate_sale
        else -> R.layout.fragment_estate_data
    }

    /**
     * Handle activity response (after user has chosen or not a picture).
     * @param requestCode Code of request.
     * @param resultCode Result code of request.
     * @param data Intent request result data.
     */
    private fun handleResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) { // SUCCESS
            var bitmap: Bitmap? = null
            when (requestCode) {
                RC_CHOOSE_PHOTO -> bitmap = data?.data?.toBitmap(activity?.contentResolver!!)
                RC_TAKE_PHOTO -> bitmap = data?.extras?.get("data") as Bitmap?
            }
            if (bitmap != null) saveImageAndAddToImageList(bitmap)
            else showToast(getString(R.string.fragment_estate_image_toast_error_getting_bitmap))
        } else // ERROR
            showToast(getString(R.string.fragment_estate_image_toast_title_no_image_chosen))
    }
}
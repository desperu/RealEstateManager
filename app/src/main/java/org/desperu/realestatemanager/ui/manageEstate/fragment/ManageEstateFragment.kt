package org.desperu.realestatemanager.ui.manageEstate.fragment

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
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import icepick.State
import kotlinx.android.synthetic.main.fragment_estate_address.*
import kotlinx.android.synthetic.main.fragment_estate_data.*
import kotlinx.android.synthetic.main.fragment_estate_sale.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateImageBinding
import org.desperu.realestatemanager.extension.createDatePickerDialog
import org.desperu.realestatemanager.extension.toBitmap
import org.desperu.realestatemanager.ui.manageEstate.Communication
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.StorageUtils.deleteFileInStorage
import org.desperu.realestatemanager.utils.StorageUtils.isExternalStorageWritable
import org.desperu.realestatemanager.utils.StorageUtils.setBitmapInStorage
import org.desperu.realestatemanager.utils.Utils.getFolderAndFileNameFromContentUri
import org.desperu.realestatemanager.view.NothingSelectedSpinnerAdapter
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

/**
 * Fragments to manage estate : data, images, address, interest places and sale data.
 * @constructor Instantiates a new ManageEstateFragment.
 */
class ManageEstateFragment: BaseBindingFragment() {

    // FOR DATA
    @JvmField @State var fragmentKey: Int = -1
    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: ManageEstateViewModel
    private lateinit var recyclerView: RecyclerView

    /**
     * Companion object, used to create new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set fragmentKey.
         * @param fragmentKey the fragment key to configure the asked fragment.
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
        // Calling the appropriate method after permissions result
        handlePermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Calling the appropriate method after activity result
        handleActivityResult(requestCode, resultCode, data)
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, getFragmentLayout(), container, false)
        viewModel = (activity as Communication).getViewModel()

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
            ESTATE_ADDRESS -> {
                configureSpinner(fragment_estate_address_spinner_interest_places1, R.array.estate_interest_places_list)
                configureSpinner(fragment_estate_address_spinner_interest_places2, R.array.estate_interest_places_list)
                configureSpinner(fragment_estate_address_spinner_interest_places3, R.array.estate_interest_places_list)
            }
            ESTATE_SALE -> {
                configureSpinner(fragment_estate_sale_spinner_state, R.array.estate_state_list)
                setPickerTextOnClickListener(context!!, fragment_estate_sale_date_picker_sale_date, viewModel.saleDate)
                setPickerTextOnClickListener(context!!, fragment_estate_sale_date_picker_sold_out_date, viewModel.soldDate)
            }
        }
    }

    /**
     * Configure estate spinner with adapter.
     * @param spinner Spinner to configure.
     * @param id String array id for spinner.
     */
    private fun configureSpinner(spinner: Spinner, @ArrayRes id: Int) {
        val spinnerAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, resources.getStringArray(id))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = NothingSelectedSpinnerAdapter(
                spinnerAdapter,
                R.layout.spinner_row_nothing_selected,
                context!!)
    }

    /**
     * Configure Linear Layout Manager for Image Recycler,
     * add on scroll listener to hide floating buttons to prevent ui mistake (hide item button),
     * and update image list.
     */
    private fun configureImageRecycler() {
        recyclerView = (binding as FragmentEstateImageBinding).fragmentEstateImageRecyclerView
        recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.addOnScrollListener( object: OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                switchFabVisibility()
            }
        })
        viewModel.updateRecyclerImageList()
    }

    /**
     * Set picker text view on click listener to show date picker dialog, and set sale date at today.
     * @param context the context from this function is called.
     * @param pickerView the associated picker text view.
     * @param date the given string date, to set DatePickerDialog.
     */
    private fun setPickerTextOnClickListener(context: Context, pickerView: TextView, date: ObservableField<String>?) {
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

    /**
     * Scroll to the new item position in recycler view.
     * @param position the position to scroll in the recycler view.
     */
    internal fun scrollToNewItem(position: Int) {
        val scroll = { recyclerView.layoutManager?.scrollToPosition(position) }
        if (position == 0) recyclerView.layoutManager?.postOnAnimation { scroll() } // When move item
        else scroll() // When add item
    }

    /**
     * Switch floating action buttons visibility, depend of recycler position and item list size.
     */
    private fun switchFabVisibility() =
            (activity as Communication)
                    .floatingVisibility(isLastVisible && recyclerView.adapter?.itemCount!! > 1)

    /**
     * Return if the last completely visible item is the last recycler item.
     */
    private val isLastVisible: Boolean
        get() {
            val pos = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            val numItems: Int = recyclerView.adapter?.itemCount!!
            return pos >= numItems - 1
        }

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
            return // TODO mistake when valid permission, close activity...!
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
    // STORAGE MANAGEMENT
    // -----------------

    /**
     * Save image in storage as JPEG format.
     * @param bitmap the image bitmap to save in storage.
     */
    private fun saveImageInStorage(bitmap: Bitmap) = storageAction {
        activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
            setBitmapInStorage(context?.applicationContext!!, it, FOLDER_NAME, Date().time.toString() + ".jpg", bitmap)
        }
    }

    /**
     * Delete image in storage.
     * @param imageUri the image uri to delete.
     */
    internal fun deleteImageInStorage(imageUri: String) = storageAction {
        val fileData = getFolderAndFileNameFromContentUri(imageUri)
        val messageError = "Can't retrieved folder name and file name from content uri"
        if (fileData.isNotEmpty())
            activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
                deleteFileInStorage(it, fileData["folderName"] ?: error(messageError),
                        fileData["fileName"] ?: error(messageError))
            }
    }

    /**
     * Check external storage write access and execute storage action wrap into coroutine.
     * @param action the storage action to execute.
     */
    private fun storageAction(action: suspend () -> Any?) {
        if (isExternalStorageWritable()) { // Check external storage write access.
            CoroutineScope(Dispatchers.Main).launch {
                val result = action()
                handleStorageResult(result)
            }
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
     * Handle permissions result, after user allow permission or not.
     * If permission granted start the selected user action.
     * @param requestCode Code of request.
     * @param grantResults the permission result.
     */
    private fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == RC_PERMS_STORAGE) chooseImageFromPhone()
            else if (requestCode == RC_PERMS_PHOTO) takePhotoWithCamera()
        } else
            showToast(getString(R.string.fragment_estate_image_toast_permission_refused))
    }

    /**
     * Handle activity result (after user has chosen or not a picture).
     * @param requestCode Code of request.
     * @param resultCode Result code of request.
     * @param data Intent request result data.
     */
    private fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) { // SUCCESS
            var bitmap: Bitmap? = null
            when (requestCode) {
                RC_CHOOSE_PHOTO -> bitmap = data?.data?.toBitmap(activity?.contentResolver!!)
                RC_TAKE_PHOTO -> bitmap = data?.extras?.get("data") as Bitmap?
            }
            if (bitmap != null) saveImageInStorage(bitmap)
            else showToast(getString(R.string.fragment_estate_image_toast_error_getting_bitmap))
        } else // ERROR
            showToast(getString(R.string.fragment_estate_image_toast_title_no_image_chosen))
    }

    /**
     * Handle storage action result.
     * @param result the result returned by the storage action.
     */
    private fun handleStorageResult(result: Any?) {
        if (result is Boolean && result) // Delete image result
             showToast(getString(R.string.fragment_estate_image_toast_deleted_photo))
        else if (result is String? && result != null) { // Save image result, contain the content uri of the created file
            showToast(getString(R.string.fragment_estate_image_toast_saved_photo))
            viewModel.addImageToImageList(result)
        } else
            showToast(getString(R.string.fragment_estate_image_toast_error_happened)) // TODO error when delete image, perhaps with simulator
    }
}
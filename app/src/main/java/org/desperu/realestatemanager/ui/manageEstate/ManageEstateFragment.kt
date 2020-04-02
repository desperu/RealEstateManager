package org.desperu.realestatemanager.ui.manageEstate

import android.Manifest.permission.*
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import icepick.State
import kotlinx.android.synthetic.main.fragment_estate_address.*
import kotlinx.android.synthetic.main.fragment_estate_data.*
import kotlinx.android.synthetic.main.fragment_estate_image.*
import kotlinx.android.synthetic.main.fragment_estate_sale.*
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentEstateImageBinding
import org.desperu.realestatemanager.utils.*
import org.desperu.realestatemanager.utils.StorageUtils.isExternalStorageWritable
import org.desperu.realestatemanager.utils.StorageUtils.setImageInStorage
import org.desperu.realestatemanager.utils.Utils.intDateToString
import org.desperu.realestatemanager.utils.Utils.todayDate
import org.desperu.realestatemanager.view.enableSwipe
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class ManageEstateFragment(): BaseBindingFragment() {

    // FOR DATA
    @JvmField @State var fragmentKey: Int = -1 // TODO save value but can't with Icepick, do it manually with bundle??
    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: ManageEstateViewModel
    // DATE PICKER
    private lateinit var datePickerSale: OnDateSetListener
    private lateinit var datePickerSold: OnDateSetListener
    private var saleDate = String()
    private var soldDate = String()

    // SECOND CONSTRUCTOR
    constructor(fragmentKey: Int): this() {
        this.fragmentKey = fragmentKey
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureCorrespondingLayout()
        configureSwipeToDeleteForRecycler()
    }

    // --------------------
    // METHODS OVERRIDE
    // --------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
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
        configureImageRecycler()
        return binding.root
    }

    /**
     * Configure corresponding layout, depending of fragmentKey.
     */
    private fun configureCorrespondingLayout() {
        when (fragmentKey) {
            ESTATE_DATA -> configureSpinner(fragment_estate_data_spinner_type, R.array.estate_type_list)
            ESTATE_ADDRESS -> configureSpinner(fragment_estate_address_spinner_interest_places, R.array.estate_interest_places_list)
            ESTATE_SALE -> { configureSpinner(fragment_estate_sale_spinner_state, R.array.estate_state_list)
                    configureDatePicker()}
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
     * Configure Linear Layout Manager for Image Recycler, and update data list.
     */
    private fun configureImageRecycler() {
        if (fragmentKey == ESTATE_IMAGE) {
            (binding as FragmentEstateImageBinding).fragmentEstateImageRecyclerView.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            viewModel.updateRecyclerImageList()
        }
    }

    /**
     * Configure swipe to delete gesture for recycler view.
     */
    private fun configureSwipeToDeleteForRecycler() {
        viewModel.estate.value?.imageList?.let {
            enableSwipe(requireActivity() as ManageEstateActivity, viewModel.getImageListAdapter, it as ArrayList<Any>)
                    .attachToRecyclerView(fragment_estate_image_recycler_view)
        }
    }

    // --------------
    // DATE PICKERS
    // --------------

    /**
     * Configure date picker.
     */
    private fun configureDatePicker() {
        fragment_estate_sale_date_picker_sale_date.text = todayDate() // TODO to check
        fragment_estate_sale_date_picker_sale_date.setOnClickListener { configureDatePickerDialog(datePickerSale) }
        datePickerSale = OnDateSetListener { _, year, month, dayOfMonth ->
            saleDate = intDateToString(dayOfMonth, month, year)
            fragment_estate_sale_date_picker_sale_date.text = saleDate
        }
        fragment_estate_sale_date_picker_sold_out_date.setOnClickListener { configureDatePickerDialog(datePickerSold) }
        datePickerSold = OnDateSetListener { _, year, month, dayOfMonth ->
            soldDate = intDateToString(dayOfMonth, month, year)
            fragment_estate_sale_date_picker_sold_out_date.text = soldDate
            viewModel.estate.value?.soldDate = soldDate
        }
    }

    /**
     * Configure Date picker dialog.
     * @param dateSetListener Date picker dialog listener.
     */
    private fun configureDatePickerDialog(dateSetListener: OnDateSetListener) {
        val cal: Calendar = Calendar.getInstance()
        if (dateSetListener === datePickerSale && saleDate.isNotEmpty()) cal.time = Utils.stringToDate(saleDate)
        if (dateSetListener === datePickerSold && soldDate.isNotEmpty()) cal.time = Utils.stringToDate(soldDate)
        val year: Int = cal.get(Calendar.YEAR)
        val monthOfYear: Int = cal.get(Calendar.MONTH)
        val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context!!, R.style.DatePickerDialogTheme,
                dateSetListener, year, monthOfYear, dayOfMonth)
        datePickerDialog.show()
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
     * Add image to image list.
     */
    private fun addImageToImageList(imageUri: String) = viewModel.addImageToImageList(imageUri)

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

    // --------------------
    // IMAGE MANAGEMENT
    // --------------------

    /**
     * Choose image from phone, ask for permission before.
     */
    private fun chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(activity!!, READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.fragment_estate_image_popup_title_permission_files_access),
                    PERMS_STORAGE, READ_EXTERNAL_STORAGE)
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
                    PERMS_PHOTO, WRITE_EXTERNAL_STORAGE, CAMERA)
            return
        }
        // Launch an "Take Photo" Activity
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), RC_TAKE_PHOTO)
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
        if (resultCode == RESULT_OK) { //SUCCESS
            if (isExternalStorageWritable()) {
                lateinit var bitmap: Bitmap
                when (requestCode) {
                    RC_CHOOSE_PHOTO -> bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data?.data)
                    RC_TAKE_PHOTO -> bitmap = data?.extras?.get("data") as Bitmap
                }
                addImageToImageList(setImageInStorage(activity?.getExternalFilesDir(
                        Environment.DIRECTORY_PICTURES)!!, context!!,
                        Date().time.toString() + ".jpg", FOLDER_NAME, bitmap))
            } else
                Toast.makeText(context, getString(R.string.fragment_estate_image_toast_error_write_external_storage), Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(context, getString(R.string.fragment_estate_image_toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
    }
}
package org.desperu.realestatemanager.ui.showImages.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.base.BaseBindingFragment
import org.desperu.realestatemanager.databinding.FragmentImageBinding
import org.desperu.realestatemanager.model.Image


/**
 * The name of the argument to received the image in this fragment.
 */
const val SHOW_IMAGE: String = "showImage"

/**
 * Fragment to show image.
 * @constructor Instantiates a new ShowImageFragment.
 */
class ShowImageFragment: BaseBindingFragment() {

    // FOR DATA
    private lateinit var binding: FragmentImageBinding
    private var viewModel: ShowImageViewModel? = null
    private val image: Image? get() = arguments?.getParcelable(SHOW_IMAGE)

    /**
     * Companion object, used to create new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set the image bundle.
         * @param image the image to show.
         * @return the new instance of ShowImageFragment.
         */
        fun newInstance(image: Image): ShowImageFragment {
            val showImageFragment = ShowImageFragment()
            val bundle = Bundle()
            bundle.putParcelable(SHOW_IMAGE, image)
            showImageFragment.arguments = bundle
            return showImageFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {}

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure data binding with view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)
        viewModel = image?.let { ShowImageViewModel(it) }

        binding.viewModel = viewModel
        return binding.root
    }
}
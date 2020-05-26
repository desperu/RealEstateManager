package org.desperu.realestatemanager.ui.main.filter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.service.ResourceService
import org.desperu.realestatemanager.utils.Utils.stringToDate
import org.desperu.realestatemanager.view.OnRangeChangeListener
import java.lang.IllegalArgumentException
import java.util.*

/**
 * View Model witch register and apply filters for estate list.
 *
 * @param resource the resource service interface witch provide application resources access.
 * @param communication the filter view model communication interface witch provide view model communication.
 *
 * @constructor Instantiates a new FilterViewModel.
 *
 * @property resource the resource service interface witch provide application resources access to set.
 * @property communication the filter view model communication interface witch provide view model communication to set.
 */
@Suppress("unchecked_cast")
class FilterViewModel(private val resource: ResourceService,
                      private val communication: FilterVMCommunication
): ViewModel() {

    // FOR DATA
    private var originalList = listOf<Estate>()
    private val mapFilters = sortedMapOf<String, Any>()
    private val hasFilter: Boolean get() = mapFilters.isNotEmpty()
    private val selectedBackground = resource.getDrawable(R.drawable.text_filter_selected)
    private val unselectedBackground = resource.getDrawable(R.drawable.text_filter_unselected)
    // Custom setter for dates values, due to multiple uses, in layout, in DialogDatePicker and need to intercept when set here.
    var saleBegin = String()
        set(value) { field = value; saleBeginObs.set(value); manageDateFilter("saleDate", saleBegin, saleEnd) }
    val saleBeginObs = ObservableField<String>()
    var saleEnd = String()
        set(value) { field = value; saleEndObs.set(value); manageDateFilter("saleDate", saleBegin, saleEnd) }
    val saleEndObs = ObservableField<String>()
    var soldBegin = String()
        set(value) { field = value; soldBeginObs.set(value); manageDateFilter("soldDate", soldBegin, soldEnd) }
    val soldBeginObs = ObservableField<String>()
    var soldEnd = String()
        set(value) { field = value; soldEndObs.set(value); manageDateFilter("soldDate", soldBegin, soldEnd) }
    val soldEndObs = ObservableField<String>()

    // -------------
    // SET ORIGINAL LIST
    // -------------

    /**
     * Set original estate list.
     * @param list the original estate list to set.
     */
    internal fun setOriginalList(list: List<Estate>) { originalList = list }

    // -------------
    // FILTERS
    // -------------

    /**
     * Add a filter to the map filter. Special case for "type" and "interestPlaces" filters,
     * they can have multiple value, so we need to manage list.
     * @param key the key of the filter to add.
     * @param value the value of the filter to add.
     */
    private fun addFilter(key: String, value: Any) = viewModelScope.launch(Dispatchers.Default) {
        if (key == "type" || key == "interestPlaces")
            if (mapFilters[key] != null) (mapFilters[key] as MutableList<String>).add(value.toString())
            else mapFilters[key] = mutableListOf(value.toString())
        else mapFilters[key] = value
        updateBottomBarColor()
    }

    /**
     * Remove a filter in the map filter. Special case for "type" and "interestPlaces" filters,
     * they can have multiple value, so we need to manage list.
     * @param key the key of the filter to remove.
     * @param value the value of the filter to remove, null if don't have the value, but in this case,
     *              the key is a unique identifier.
     */
    private fun removeFilter(key: String, value: Any?) = viewModelScope.launch(Dispatchers.Default) {
        if (key == "type" || key == "interestPlaces") {
            val valueList = (mapFilters[key] as MutableList<String>?)
            if (valueList != null && valueList.size > 1) valueList.remove(value.toString())
            else mapFilters.remove(key)
        } else if (value != null) mapFilters.remove(key, value)
        else mapFilters.remove(key)
        updateBottomBarColor()
    }

    /**
     * Manage dates filters.
     * @param key the key of the date filter.
     * @param beginDate the begin date of the filter.
     * @param endDate the end date of the filter.
     */
    private fun manageDateFilter(key: String, beginDate: String, endDate: String) {
        if (beginDate.isNotBlank() || endDate.isNotBlank()) addFilter(key, listOf(beginDate, endDate))
        else removeFilter(key, null)
    }

    // -------------
    // LISTENER
    // -------------

    /**
     * Filter on click listener, add or remove filter from map filter, depends of filter state.
     */
    private val onClickFilter = View.OnClickListener {
        val key = it.tag.toString()
        val value = (it as? TextView)?.text
        if (it.background == unselectedBackground) {
            value?.let { it1 -> addFilter(key, it1) }
            it.background = selectedBackground
        } else {
            removeFilter(key, value)
            it.background = unselectedBackground
        }
    }

    /**
     * Text watcher for edit text city.
     */
    private val onTextCityChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank()) addFilter("city", s.toString())
            else removeFilter("city", null)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Text watcher for edit text minimum image number.
     */
    private val onTextImageChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank()) addFilter("imageNumber", s.toString())
            else removeFilter("imageNumber", null)
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * On range change listener for all custom seek bar.
     */
    private val onRangeChanged = object : OnRangeChangeListener {
        override fun onRangeSelected(view: View, minValue: Number, maxValue: Number) {
            addFilter("${view.tag}", listOf(minValue, maxValue))
        }
        override fun onRangeUnselected(view: View) { removeFilter("${view.tag}", null) }
    }

    /**
     * Spinner listener, for state spinner.
     */
    private val spinnerListener = object: AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) addFilter("${parent?.tag}", parent?.getItemAtPosition(position).toString())
        }
    }

    // -------------
    // UTILS
    // -------------

    /**
     * Apply all filters in map filters to estate list.
     */
    fun applyFilters() = viewModelScope.launch(Dispatchers.Default) {
        if (mapFilters.isNotEmpty()) {
            val filteredList = mutableListOf<Estate>()

            originalList.forEach estate@ { estate ->
                var match = false
                mapFilters.forEach {
                    match = getPredicate(estate, it.key, it.value)
                    if (!match) return@estate
                }
                if (match) filteredList.add(estate)
            }
            updateEstateList(filteredList)
        }
    }

    /**
     * Check if the estate value is under the ranges values, or equal the value.
     * @param originalEstate the estate to test.
     * @param key the filter key.
     * @param value the filter value.
     * @return true if the estate value match the filter value, false otherwise.
     */
    private fun getPredicate(originalEstate: Estate, key: String, value: Any): Boolean = when (key) {
        "type" -> (value as List<String>).contains(originalEstate.type)
        "city" -> originalEstate.address.city.toLowerCase(Locale.ROOT).contains(value.toString().toLowerCase(Locale.ROOT))
        "imageNumber" -> originalEstate.imageList.size >= value.toString().toInt()
        "price" -> originalEstate.price >= (value as List<Int>)[0] && originalEstate.price <= value[1]
        "surface" -> originalEstate.surfaceArea >= (value as List<Int>)[0] && originalEstate.surfaceArea <= value[1]
        "rooms" -> originalEstate.roomNumber >= (value as List<Int>)[0] && originalEstate.roomNumber <= value[1]
//        "interestPlaces" -> originalEstate.interestPlaces == (value as List<String>).containsAll()
        "saleDate" -> compareDates(stringToDate(originalEstate.saleDate), value as List<String>)
        "soldDate" -> compareDates(stringToDate(originalEstate.soldDate), value as List<String>)
        "state" -> originalEstate.state == value.toString()
        else -> throw IllegalArgumentException("Filter key not found: $key")
    }

    /**
     * Compare estate date with range dates.
     * @param estateDate the estate date to compare.
     * @param rangeDate the range dates to compare to.
     * @return true if the estate date is in the the ranges dates.
     */
    private fun compareDates(estateDate: Date?, rangeDate: List<String>): Boolean {
        var compare1 = false
        var compare2 = false
        if (estateDate != null) {
            compare1 = if (rangeDate[0].isNotBlank()) estateDate >= stringToDate(rangeDate[0]) else true
            compare2 = if (rangeDate[1].isNotBlank()) estateDate <= stringToDate(rangeDate[1]) else true
        }
        return compare1 && compare2
    }

    /**
     * Remove all filters in map filters.
     */
    fun removeFilters() {
        mapFilters.clear()
        communication.closeFilterFragment()
    }

    // -------------
    // UI
    // -------------

    /**
     * Update bottom bar color, depend if has filters set.
     */
    private fun updateBottomBarColor() = viewModelScope.launch(Dispatchers.Main) {
        communication.updateBottomBarColor(hasFilter)
    }

    /**
     * Update estate list in other fragments.
     * @param newList the new estate list to set.
     */
    private fun updateEstateList(newList: List<Estate>) = viewModelScope.launch(Dispatchers.Main) {
        communication.updateEstateList(newList, hasFilter)
    }

    // --- GETTERS ---

    val getOnClickFilter = onClickFilter

    val getOnTextCityChanged = onTextCityChanged

    val getOnTextImageChanged = onTextImageChanged

    val getOnRangeChanged = onRangeChanged

    val getSpinnerListener = spinnerListener
}
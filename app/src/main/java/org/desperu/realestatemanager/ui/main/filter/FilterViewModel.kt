package org.desperu.realestatemanager.ui.main.filter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.filter.FilterHelper
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.view.CustomTextView
import org.desperu.realestatemanager.view.OnRangeChangeListener

/**
 * View Model witch register and apply filters for estate list.
 *
 * @param communication the filter view model communication interface witch provide view model communication.
 *
 * @constructor Instantiates a new FilterViewModel.
 *
 * @property communication the filter view model communication interface witch provide view model communication to set.
 */
@Suppress("unchecked_cast")
class FilterViewModel(private val communication: FilterVMCommunication): ViewModel() {

    // FOR DATA
    private var originalList = listOf<Estate>()
    private val filtersMap = sortedMapOf<String, Any>()
    private val hasFilter: Boolean get() = filtersMap.isNotEmpty()
    private val selectedBackground = R.drawable.text_filter_selected
    private val unselectedBackground = R.drawable.text_filter_unselected
    val saleBegin = ObservableField<String>()
    val saleEnd = ObservableField<String>()
    val soldBegin = ObservableField<String>()
    val soldEnd = ObservableField<String>()

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
            if (filtersMap[key] != null) (filtersMap[key] as MutableList<String>).add(value.toString())
            else filtersMap[key] = mutableListOf(value.toString())
        else filtersMap[key] = value
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
            val valueList = (filtersMap[key] as MutableList<String>?)
            if (valueList != null && valueList.size > 1) valueList.remove(value.toString())
            else filtersMap.remove(key)
        } else if (value != null) filtersMap.remove(key, value)
        else filtersMap.remove(key)
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
        val v = it as CustomTextView
        val key = v.tag.toString()
        val value = v.text
        if (v.compareDrawableTo(unselectedBackground)) {
            value?.let { it1 -> addFilter(key, it1) }
            v.setBackgroundResource(selectedBackground)
        } else {
            removeFilter(key, value)
            v.setBackgroundResource(unselectedBackground)
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
    private val onRangeChanged = object: OnRangeChangeListener {
        override fun onRangeSelected(view: View, minValue: Number, maxValue: Number) {
            addFilter("${view.tag}", listOf(minValue, maxValue))
        }
        override fun onRangeUnselected(view: View) { removeFilter("${view.tag}", null) }
    }

    /**
     * Text watcher for text view sale date begin.
     */
    private val onSaleDateBeginChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            saleBegin.set(s.toString())
            manageDateFilter("saleDate", s.toString(), saleEnd.get() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Text watcher for text view sale date end.
     */
    private val onSaleDateEndChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            saleEnd.set(s.toString())
            manageDateFilter("saleDate", saleBegin.get() ?: "", s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Text watcher for text view sold date begin.
     */
    private val onSoldDateBeginChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            soldBegin.set(s.toString())
            manageDateFilter("soldDate", s.toString(), soldEnd.get() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    /**
     * Text watcher for text view sold date end.
     */
    private val onSoldDateEndChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            soldEnd.set(s.toString())
            manageDateFilter("soldDate", soldBegin.get() ?: "", s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
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
    // ACTION
    // -------------

    /**
     * Apply all filters in map filters to estate list.
     */
    fun applyFilters() = viewModelScope.launch(Dispatchers.Default) {
        val filteredList = FilterHelper().applyFilters(originalList, filtersMap)
        updateEstateList(filteredList)
    }

    /**
     * Apply all filters in map filters to searched estate list.
     * @param searchedList the searched estate list to filter.
     */
    internal fun applyFilters(searchedList: List<Estate>?) = viewModelScope.launch(Dispatchers.Default) {
        val listToFilter = searchedList ?: originalList
        val filteredList = FilterHelper().applyFilters(listToFilter, filtersMap)
        updateEstateList(filteredList)
    }

    /**
     * Remove all filters in map filters.
     */
    fun removeFilters() {
        filtersMap.clear()
        communication.closeFilterFragment(true)
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

    val getOnSaleDateBeginChanged = onSaleDateBeginChanged

    val getOnSaleDateEndChanged = onSaleDateEndChanged

    val getOnSoldDateBeginChanged = onSoldDateBeginChanged

    val getOnSoldDateEndChanged = onSoldDateEndChanged

    val getSpinnerListener = spinnerListener
}
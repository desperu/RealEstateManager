package org.desperu.realestatemanager.ui.main.filter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
    private val selectedBackground = resource.getDrawable(R.drawable.text_filter_selected)
    private val unselectedBackground = resource.getDrawable(R.drawable.text_filter_unselected)
    // Custom setter for date values, due to multiple uses, in layout, in DialogDatePicker and need to intercept when set here.
    var saleBegin = String()
        set(value) {
            field = value
            saleBeginObs.set(value)
            if (value.isNotBlank()) addFilter("saleBegin", value)
            else removeFilter("saleBegin", null)
        }
    var saleEnd = String()
        set(value) {
            field = value
            saleEndObs.set(value)
            if (value.isNotBlank()) addFilter("saleEnd", value)
            else removeFilter("saleEnd", null)
        }
    val saleBeginObs = ObservableField<String>()
    val saleEndObs = ObservableField<String>()

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
        // TODO("manage myLocation, city and seek distance")
    }

    /**
     * Text watcher for edit text city.
     */
    private val onTextChanged = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!s.isNullOrBlank()) addFilter("city", s.toString())
            else removeFilter("city", null)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    }

    /**
     * On range change listener for all custom seek bar. TODO("put in binding adapters and use getMapsFilter as value in xml")
     */
    private val onRangeChanged = object : OnRangeChangeListener {
        override fun onRangeSelected(view: View, minValue: Number, maxValue: Number) {
            addFilter("${view.tag}Min", minValue)
            addFilter("${view.tag}Max", maxValue)
        }

        override fun onRangeUnselected(view: View) {
            removeFilter("${view.tag}Min", null)
            removeFilter("${view.tag}Max", null)
        }
    }

    // -------------
    // UTILS
    // -------------

    fun applyFilters() = viewModelScope.launch(Dispatchers.Default) {
        if (mapFilters.isNotEmpty()) {
            val tempList = mutableListOf<Estate>()
            val filteredList = mutableListOf<Estate>()
//        mapFilters.forEach {
//            filteredList.addAll(originalList.filter { estate ->  getPredicate(estate, it.key, it.value) })
//        }

            originalList.forEach { estate ->
                var match = false
                mapFilters.forEach {
                    match = getPredicate(estate, it.key, it.value)
                    if (!match) return@forEach
                }
                if (match) filteredList.add(estate)
            }
            updateEstateList(filteredList)
        }
    }

    fun removeFilters() {
        mapFilters.clear()
        updateEstateList(originalList)
    }

    private fun getPredicate(originalEstate: Estate, key: String, value: Any): Boolean = when (key) {
        "type" -> (value as List<String>).contains(originalEstate.type)
//        "myLocation" -> // TODO("use seek distance and distanceTo function from go4lunch")
//        "distanceMin" ->
//        "distanceMax" ->
        "priceMin" -> originalEstate.price >= (value as Int).toLong()
        "priceMax" -> originalEstate.price <= (value as Int).toLong()
        "surfaceMin" -> originalEstate.surfaceArea >= value as Int
        "surfaceMax" -> originalEstate.surfaceArea <= value as Int
        "roomsMin" -> originalEstate.roomNumber >= value as Int
        "roomsMax" -> originalEstate.roomNumber <= value as Int
//        "interestPlaces" -> originalEstate.interestPlaces == (value as List<String>).containsAll()
        "saleBegin" -> stringToDate(value.toString())!! <= stringToDate(originalEstate.saleDate)
        "saleEnd" -> stringToDate(value.toString())!! >= stringToDate(originalEstate.saleDate)
        else -> throw IllegalArgumentException("Filter key not found: $key")
    }

    // -------------
    // UI
    // -------------

    /**
     * Update bottom bar color, depend if has filters set.
     */
    private fun updateBottomBarColor() = viewModelScope.launch(Dispatchers.Main) {
        communication.updateBottomBarColor(mapFilters.isNotEmpty())
    }

    /**
     * Update estate list in other fragments.
     * @param newList the new estate list to set.
     */
    private fun updateEstateList(newList: List<Estate>) = viewModelScope.launch(Dispatchers.Main) {
        communication.updateEstateList(newList)
    }

    // --- GETTERS ---

    val getOnClickFilter = onClickFilter

    val getOnTextChanged = onTextChanged

    val getOnRangeChanged = onRangeChanged
}
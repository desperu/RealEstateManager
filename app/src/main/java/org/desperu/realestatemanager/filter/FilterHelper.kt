package org.desperu.realestatemanager.filter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.utils.Utils
import java.util.*

/**
 * Class to apply filters to the estate list.
 *
 * @constructor Instantiates a new FilterHelper.
 */
class FilterHelper {

    /**
     * Apply all filters in map filters to estate list.
     * @param originalList the original estate List.
     * @param filtersMap the map of the filters to apply.
     * @return the estate list filtered.
     */
    internal suspend fun applyFilters(originalList: List<Estate>,
                                      filtersMap: Map<String, Any>
    ): MutableList<Estate> = withContext(Dispatchers.Default) {

        val filteredList = mutableListOf<Estate>()
        if (filtersMap.isNotEmpty()) {

            originalList.forEach estate@{ estate ->
                var match = false
                filtersMap.forEach {
                    match = getPredicate(estate, it.key, it.value)
                    if (!match) return@estate
                }
                if (match) filteredList.add(estate)
            }
        }
        filteredList
    }

    /**
     * Check if the estate value is under the ranges values, or equal the value.
     * @param originalEstate the estate to test.
     * @param key the filter key.
     * @param value the filter value.
     * @return true if the estate value match the filter value, false otherwise.
     */
    @Suppress("unchecked_cast")
    private suspend fun getPredicate(originalEstate: Estate, key: String, value: Any): Boolean = withContext(Dispatchers.Default) {
        when (key) {
            "type" -> (value as List<String>).contains(originalEstate.type)
            "city" -> originalEstate.address.city.toLowerCase(Locale.ROOT).contains(value.toString().toLowerCase(Locale.ROOT))
            "imageNumber" -> originalEstate.imageList.size >= value.toString().toInt()
            "price" -> originalEstate.price >= (value as List<Int>)[0] && originalEstate.price <= value[1]
            "surface" -> originalEstate.surfaceArea >= (value as List<Int>)[0] && originalEstate.surfaceArea <= value[1]
            "rooms" -> originalEstate.roomNumber >= (value as List<Int>)[0] && originalEstate.roomNumber <= value[1]
            "interestPlaces" -> Utils.deConcatenateStringToMutableList(originalEstate.interestPlaces).containsAll(value as List<String>)
            "saleDate" -> compareDates(Utils.stringToDate(originalEstate.saleDate), value as List<String>)
            "soldDate" -> compareDates(Utils.stringToDate(originalEstate.soldDate), value as List<String>)
            "state" -> originalEstate.state == value.toString()
            else -> throw IllegalArgumentException("Filter key not found: $key")
        }
    }

    /**
     * Compare estate date with range dates.
     * @param estateDate the estate date to compare.
     * @param rangeDate the range dates to compare to.
     * @return true if the estate date is in the the ranges dates.
     */
    private suspend fun compareDates(estateDate: Date?, rangeDate: List<String>): Boolean = withContext(Dispatchers.Default) {
        var compare1 = false
        var compare2 = false
        if (estateDate != null) {
            compare1 = if (rangeDate[0].isNotBlank()) estateDate >= Utils.stringToDate(rangeDate[0]) else true
            compare2 = if (rangeDate[1].isNotBlank()) estateDate <= Utils.stringToDate(rangeDate[1]) else true
        }
        compare1 && compare2
    }
}
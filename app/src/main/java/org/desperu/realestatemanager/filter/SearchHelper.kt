package org.desperu.realestatemanager.filter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.model.Address
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.model.Image
import org.koin.ext.isInt
import java.util.*

/**
 * Class to apply query search to the estate list.
 *
 * @constructor Instantiates a new SearchHelper.
 */
class SearchHelper {

    /**
     * Apply the searched query term to the estate list, return the list of matched estates.
     * @param originalList the original estate List.
     * @param query the searched query term.
     * @return the filtered estate list.
     */
    internal suspend fun applySearch(originalList: List<Estate>, query: String): List<Estate> = withContext(Dispatchers.Default) {
        val filteredList = mutableListOf<Estate>()
        if (query.isNotBlank()) {
            originalList.forEach { estate ->
                if (getPredicate(estate, query.toLowerCase(Locale.ROOT))
                        || getPredicate(estate.address, query.toLowerCase(Locale.ROOT)))
                    filteredList.add(estate)
                else
                    estate.imageList.forEach { image ->
                        if (getPredicate(image, query.toLowerCase(Locale.ROOT)))
                            filteredList.add(estate)
                }
            }
        }
        filteredList
    }

    /**
     * Check if the query term match the estate's values.
     * @param originalEstate the estate to test.
     * @param query the query term to search.
     * @return true if the estate value match the filter value, false otherwise.
     */
    private suspend fun getPredicate(originalEstate: Estate, query: String): Boolean = withContext(Dispatchers.Default) {
        when {
            originalEstate.type.toLowerCase(Locale.ROOT).contains(query) -> true
            query.isInt() && originalEstate.price == query.toLong() -> true
            query.isInt() && originalEstate.surfaceArea == query.toInt() -> true
            query.isInt() && originalEstate.roomNumber == query.toInt() -> true
            originalEstate.description.toLowerCase(Locale.ROOT).contains(query) -> true
            originalEstate.interestPlaces.toLowerCase(Locale.ROOT).contains(query) -> true
            originalEstate.saleDate == query -> true
            originalEstate.soldDate == query -> true
            originalEstate.realEstateAgent.toLowerCase(Locale.ROOT).contains(query) -> true
            else -> false
        }
    }

    /**
     * Check if the query term match the estate's values.
     * @param originalAddress the estate to test.
     * @param query the query term to search.
     * @return true if the estate value match the filter value, false otherwise.
     */
    private suspend fun getPredicate(originalAddress: Address, query: String): Boolean = withContext(Dispatchers.Default) {
        when {
            query.isInt() && originalAddress.streetNumber == query.toInt() -> true
            originalAddress.streetName.toLowerCase(Locale.ROOT).contains(query) -> true
            originalAddress.flatBuilding.toLowerCase(Locale.ROOT).contains(query) -> true
            query.isInt() && originalAddress.postalCode == query.toInt() -> true
            originalAddress.city.toLowerCase(Locale.ROOT).contains(query) -> true
            originalAddress.country.toLowerCase(Locale.ROOT).contains(query) -> true
            else -> false
        }
    }

    /**
     * Check if the query term match the estate's values.
     * @param originalImage the estate to test.
     * @param query the query term to search.
     * @return true if the estate value match the filter value, false otherwise.
     */
    private suspend fun getPredicate(originalImage: Image, query: String): Boolean = withContext(Dispatchers.Default) {
        when {
            originalImage.description.toLowerCase(Locale.ROOT).contains(query) -> true
            else -> false
        }
    }
}
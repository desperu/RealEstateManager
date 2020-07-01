package org.desperu.realestatemanager.filter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.realestatemanager.model.Estate
import org.desperu.realestatemanager.ui.main.MainCommunication
import java.util.*

/**
 * Manage filters helper witch save and apply, filters and search, for estate list.
 *
 * @param communication the main communication interface witch provide main activity communication.
 *
 * @constructor Instantiates a new ManageFiltersHelper.
 *
 * @property communication the main communication interface witch provide main activity communication to set.
 */
class ManageFiltersHelper(private val communication: MainCommunication) {

    // FOR DATA
    private var fullEstateList = listOf<Estate>()
    private var filteredEstateList: List<Estate>? = null
    private var query: String = ""
    private var filtersMap = sortedMapOf<String, Any>()

    // -------------
    // SET ORIGINAL LIST
    // -------------

    /**
     * Set full estate list.
     * @param estateList the full estate list to set.
     */
    internal fun setFullEstateList(estateList: List<Estate>) { fullEstateList = estateList }

    // -------------------------
    // APPLY / REMOVE FILTERS
    // -------------------------

    /**
     * Apply search and filters to the full estate list.
     * @param query the query terms to search in the list.
     * @param filtersMap the filters to apply to the list.
     */
    internal suspend fun applyFilters(query: String? = null,
                                      filtersMap: SortedMap<String, Any>? = null
    ) = withContext(Dispatchers.Default) {
        updateQueryAndFilters(query, filtersMap)
        var tempList = fullEstateList
        if (fullEstateList.isNotEmpty()) {
            if (this@ManageFiltersHelper.filtersMap.isNotEmpty())
                tempList = FilterHelper().applyFilters(tempList, this@ManageFiltersHelper.filtersMap)
            if (this@ManageFiltersHelper.query.isNotBlank())
                tempList = SearchHelper().applySearch(tempList, this@ManageFiltersHelper.query)
        }
        updateEstateList(tempList)
    }

    /**
     * Remove filters and re-apply if there's a query.
     * @param isReload true if is called from swipe refresh to reload data from database.
     */
    internal suspend fun removeFilters(isReload: Boolean) = withContext(Dispatchers.Default) {
        clearFiltersMap()
        if (isReload) query = ""
        if (query.isNotBlank()) applyFilters(query)
        else updateEstateList(fullEstateList)
    }

    // -------------
    // UPDATE DATA
    // -------------

    /**
     * Update query and filters map in this class.
     * @param query the new query terms to set.
     * @param filtersMap the new filters map to set.
     */
    private fun updateQueryAndFilters(query: String?, filtersMap: SortedMap<String, Any>?) {
        if (query != null) this.query = query
        if (filtersMap != null) this.filtersMap.putAll(filtersMap)
    }

    /**
     * Clear filters map.
     */
    internal fun clearFiltersMap() = filtersMap.clear()

    // -------------
    // UI
    // -------------

    /**
     * Update estate list.
     * @param filteredEstateList the filtered estate list to show.
     */
    private suspend fun updateEstateList(filteredEstateList: List<Estate>) = withContext(Dispatchers.Main){
        this@ManageFiltersHelper.filteredEstateList = if (filteredEstateList != fullEstateList) filteredEstateList else null
        communication.updateEstateList(filteredEstateList)
    }

    // --- GETTERS ---

    val getFullEstateList get() = fullEstateList

    val getFilteredEstateList get() = filteredEstateList

    val hasFilters get() = filtersMap.isNotEmpty()

    val getQuery get() = query
}
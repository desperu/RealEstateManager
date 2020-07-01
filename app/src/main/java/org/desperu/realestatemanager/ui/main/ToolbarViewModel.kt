package org.desperu.realestatemanager.ui.main

import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.realestatemanager.filter.ManageFiltersHelper

/**
 * View Model witch manage and apply search query terms for estate list.
 *
 * @param filters the manage filters witch manage and apply filters and search for the estate list.
 *
 * @constructor Instantiates a new FilterViewModel.
 *
 * @property filters the manage filters witch manage and apply filters and search for the estate list to set.
 */
class ToolbarViewModel(private val filters: ManageFiltersHelper): ViewModel() {

    // FOR DATA
    private var query = ""

    // -------------
    // LISTENER
    // -------------

    /**
     * On query text listener for the search view.
     */
    private val onQueryTextListener = object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            applySearch(query)
            return false
        }

        override fun onQueryTextChange(s: String): Boolean {
            applySearch(s)
            return false
        }
    }

    // -------------
    // ACTION
    // -------------

    /**
     * Apply search query terms to the full estate list.
     * @param query the query terms to search.
     */
    private fun applySearch(query: String) = viewModelScope.launch(Dispatchers.Main) {
        if (this@ToolbarViewModel.query != query) {
            this@ToolbarViewModel.query = query
            filters.applyFilters(query)
        }
    }

    // --- GETTERS ---

    val getOnQueryTextListener = onQueryTextListener
}
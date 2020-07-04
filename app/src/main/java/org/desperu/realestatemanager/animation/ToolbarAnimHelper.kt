package org.desperu.realestatemanager.animation

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import org.desperu.realestatemanager.R

/**
 * Toolbar animation helper to show and hide search view and menu item in toolbar.
 * @constructor Instantiates a new ToolbarAnim instance.
 */
class ToolbarAnimHelper {

    /**
     * Switch search view visibility, clear query and start animations.
     * @param context the context from this function is called.
     * @param searchView the search view to switch visibility.
     * @param fromDrawer true if called from menu drawer, false otherwise.
     * @param isReload true if called for a reload, false otherwise.
     */
    internal fun switchSearchViewVisibility(context: Context, searchView: SearchView?, fromDrawer: Boolean, isReload: Boolean) {
        if (searchView != null) {
            // Hide search view
            if (searchView.isShown && !fromDrawer) {
                searchView.setQuery(null, true)
                animSearchViewVisibility(context, searchView, false)
                animMenuItem(context, true)
            } else if (!searchView.isShown && !isReload) { // Show search view
                animMenuItem(context, false)
                animSearchViewVisibility(context, searchView, true)
                searchView.onActionViewExpanded()
            }
        }
    }

    /**
     * Search View animation, for show and hide, from or to left out of screen.
     * @param context the context from this function is called.
     * @param searchView the search view to animate.
     * @param toShow {@code true} for show and {@code false} to hide menu item.
     */
    private fun animSearchViewVisibility(context: Context, searchView: SearchView, toShow: Boolean) {
        // Create animation object
        val animRes = if (toShow) R.anim.search_view_anim_show
                      else R.anim.search_view_anim_hide
        val animation: Animation = AnimationUtils.loadAnimation(context, animRes)

        // Set search view visibility
        searchView.visibility = if (toShow) View.VISIBLE else View.INVISIBLE

        // Set animation for the search view.
        searchView.startAnimation(animation)
    }

    /**
     * Menu item animation, for show and hide, from or to right out of screen.
     * @param context the context from this function is called.
     * @param toShow {@code true} for show and {@code false} to hide menu item.
     */
    private fun animMenuItem(context: Context, toShow: Boolean) {
        val activity = context as AppCompatActivity

        // Create animation object.
        val animRes = if (toShow) R.anim.menu_item_anim_show
                      else R.anim.menu_item_anim_hide
        val animation: Animation = AnimationUtils.loadAnimation(context, animRes)

        // Get view for each menu item.
        val menuItemList = listOf<View?>(
                activity.findViewById(R.id.activity_main_menu_search),
                activity.findViewById(R.id.activity_main_menu_add),
                activity.findViewById(R.id.activity_main_menu_update))

        // Set animation for each menu item.
        menuItemList.forEach { it?.startAnimation(animation) }
    }
}
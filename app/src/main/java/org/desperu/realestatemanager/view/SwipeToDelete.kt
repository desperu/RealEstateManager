package org.desperu.realestatemanager.view

import android.graphics.*
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.manageEstate.ManageImageViewModel
import org.desperu.realestatemanager.ui.main.estateList.EstateListFragment
import org.desperu.realestatemanager.ui.main.estateList.EstateViewModel
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity

lateinit var list: MutableList<Any>
var restored = false

internal fun enableSwipe(fragment: Fragment, adapter: RecyclerViewAdapter, givenList: MutableList<Any>): ItemTouchHelper {

    list = givenList

    val p = Paint()

    val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val deletedModel = if (position <= list.size) list[position] else 0
            removeItem(fragment, adapter, position)
            // showing snack bar with Undo option
            val snackBar = fragment.activity?.currentFocus?.let { Snackbar.make(it, " removed from Recyclerview!", Snackbar.LENGTH_LONG) }
            snackBar?.setAction("UNDO") { // undo is selected, restore the deleted item
                restoreItem(adapter, deletedModel, position)
            }
            snackBar?.setActionTextColor(Color.YELLOW)
            snackBar?.show()
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            val icon: Bitmap
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val itemView: View = viewHolder.itemView
                val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                val width = height / 3
                if (dX > 0) {
                    p.color = Color.parseColor("#388E3C")
                    val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                    c.drawRect(background, p)
                    icon = BitmapFactory.decodeResource(fragment.activity?.resources, R.drawable.ic_baseline_update_white_24)
                    val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                    c.drawBitmap(icon, null, iconDest, p)
                } else {
                    p.color = Color.parseColor("#D32F2F")
                    val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    c.drawRect(background, p)
                    icon = BitmapFactory.decodeResource(fragment.context?.resources, R.drawable.ic_baseline_delete_forever_black_24)
                    val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                    c.drawBitmap(icon, null, iconDest, p)
                }
//                if (dX > 0) {
//                    p.color = Color.parseColor("#388E3C")
//                    val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
//                    c.drawRect(background, p)
//                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete)
//                    val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
//                    c.drawBitmap(icon, null, icon_dest, p)
//                } else {
//                    p.color = Color.parseColor("#D32F2F")
//                    val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
//                    c.drawRect(background, p)
//                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete)
//                    val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
//                    c.drawBitmap(icon, null, icon_dest, p)
//                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
//    val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
//    itemTouchHelper.attachToRecyclerView(recyclerView)
    return ItemTouchHelper(simpleItemTouchCallback)
}

internal fun updateList(newList: MutableList<Any>) { list = newList }

private fun removeItem(fragment: Fragment, adapter: RecyclerViewAdapter, position: Int) {
    val itemId = getItemId(list[position], (list[position] as ViewModel)::class.java)
    list.removeAt(position)
    adapter.notifyItemRemoved(position)
    restored = false
    Handler().postDelayed( { if (!restored) removeDataSource(fragment.activity as AppCompatActivity, itemId) }, 5000)
}

private fun restoreItem(adapter: RecyclerViewAdapter, deletedItem: Any, position: Int) {
    list[position] = deletedItem
    adapter.notifyItemInserted(position)
    restored = true
}

private fun <T : ViewModel?> getItemId(swipedItem: Any, modelClass: Class<T>): Long {
    if (modelClass.isAssignableFrom(EstateViewModel::class.java)) {
        return (swipedItem as EstateViewModel).getEstate.value?.id!!
    }
    else if (modelClass.isAssignableFrom(ManageImageViewModel::class.java)) {
        return (swipedItem as ManageImageViewModel).image.value?.id!!
    }
    throw IllegalArgumentException("Unknown View Model class")
}

private fun removeDataSource(activity: AppCompatActivity, itemId: Long) {
    if (activity::class.java.isAssignableFrom(MainActivity::class.java)) {
        ((activity as MainActivity).supportFragmentManager
                .findFragmentById(R.id.activity_main_frame_layout) as EstateListFragment?)
                ?.getViewModel()?.deleteFullEstate(itemId)
        return
    } else if (activity::class.java.isAssignableFrom(ManageEstateActivity::class.java)) {
        (activity as ManageEstateActivity).getViewModel().deleteImage(itemId)
        return
    }
    throw IllegalArgumentException("Unknown Activity class")
}
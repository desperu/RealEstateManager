package org.desperu.realestatemanager.view

import android.graphics.*
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.desperu.realestatemanager.R
import org.desperu.realestatemanager.ui.main.EstateListFragment
import org.desperu.realestatemanager.ui.main.EstateViewModel
import org.desperu.realestatemanager.ui.main.MainActivity
import org.desperu.realestatemanager.ui.manageEstate.ManageEstateActivity

lateinit var list: ArrayList<Any>
var restored = false

fun enableSwipe(activity: AppCompatActivity, adapter: RecyclerViewAdapter, givenList: ArrayList<Any>): ItemTouchHelper {

    list = givenList

    val p = Paint()

    val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val deletedModel = list[position]
            removeItem(activity, adapter, position)
            // showing snack bar with Undo option
            val snackBar = activity.currentFocus?.let { Snackbar.make(it, " removed from Recyclerview!", Snackbar.LENGTH_LONG) }
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
                    icon = BitmapFactory.decodeResource(activity.resources, R.drawable.ic_baseline_delete_forever_black_24)
                    val iconDest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                    c.drawBitmap(icon, null, iconDest, p)
                } else {
                    p.color = Color.parseColor("#D32F2F")
                    val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    c.drawRect(background, p)
                    icon = BitmapFactory.decodeResource(activity.resources, R.drawable.ic_baseline_delete_forever_black_24)
                    val iconDest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                    c.drawBitmap(icon, null, iconDest, p)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
//    val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
//    itemTouchHelper.attachToRecyclerView(recyclerView)
    return ItemTouchHelper(simpleItemTouchCallback)
}

fun updateList(newList: ArrayList<Any>) { list = newList }

private fun removeItem(activity: AppCompatActivity, adapter: RecyclerViewAdapter, position: Int) {
    val estateId: Long = (list[position] as EstateViewModel).getEstate.value?.id!!
    adapter.removeItem(position)
    restored = false
    Handler().postDelayed( { if (!restored) removeDataSource(activity, estateId) }, 5000)
}

private fun restoreItem(adapter: RecyclerViewAdapter, deletedItem: Any, position: Int) {
    adapter.restoreItem(deletedItem, position)
    restored = true
}

private fun removeDataSource(activity: AppCompatActivity, estateId: Long) {
    if (activity::class.java.isAssignableFrom(MainActivity::class.java)) {
        ((activity as MainActivity).supportFragmentManager
                .findFragmentById(R.id.activity_main_frame_layout) as EstateListFragment?)
                ?.getViewModel()?.deleteFullEstate(estateId)
        return
    } else if (activity::class.java.isAssignableFrom(ManageEstateActivity::class.java)) {
        (activity as ManageEstateActivity).getViewModel().deleteImage(0L)
        return
    }
    throw IllegalArgumentException("Unknown Activity class")
}
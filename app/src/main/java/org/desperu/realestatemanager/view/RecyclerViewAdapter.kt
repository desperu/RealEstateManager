package org.desperu.realestatemanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(@LayoutRes private val layoutId: Int): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private lateinit var list: MutableList<Any>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list[position])


    override fun getItemViewType(position: Int) = layoutId

    override fun getItemCount(): Int = if (::list.isInitialized) list.size else 0

    /**
     * Get list if is initialized, else empty list.
     */
    internal fun getList() = if (::list.isInitialized) list else mutableListOf()

    /**
     * Update all item list.
     * @param list the list to set.
     */
    internal fun updateList(list: MutableList<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

    /**
     * Get item for given position
     * @param position the item position to get.
     */
    internal fun getItem(position: Int): Any = list[position]

    /**
     * Add an item in list.
     * @param position the position to add item.
     * @param any the item to add in list.
     */
    internal fun addItem(position: Int, any: Any) {
        list.add(position, any)
        // notify item added by position
        notifyItemInserted(position)
    }

    /**
     * Update item in list.
     * @param position the item position updated.
     * @param any the new item.
     */
    internal fun updateItem(position: Int, any: Any) {
        list.removeAt(position)
        list.add(position, any)
        notifyItemChanged(position)
    }

    /**
     * Move an item to new position in list.
     * @param fromPosition the actual item position move from.
     * @param toPosition the new item position move to.
     */
    internal fun moveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = list[fromPosition]
        list.removeAt(fromPosition)
        list.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Remove an item in list.
     * @param position the item position in list.
     */
    internal fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        internal fun bind(any: Any?) {
            binding.setVariable(org.desperu.realestatemanager.BR.viewModel, any)
            binding.executePendingBindings()
        }
    }
}
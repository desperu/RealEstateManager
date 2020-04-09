package org.desperu.realestatemanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(@LayoutRes private val layoutId: Int): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var list = ArrayList<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemViewType(position: Int) = layoutId

    override fun getItemCount(): Int = list.size

    fun updateList(list: ArrayList<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, any: Any) {
        list.add(position, any)
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    fun addItem( position: Int, any: Any) {
        list.add(position, any)
        // notify item added by position
        notifyItemInserted(position)
    }

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(any: Any?) {
            binding.setVariable(org.desperu.realestatemanager.BR.viewModel, any)
            binding.executePendingBindings()
        }
    }
}
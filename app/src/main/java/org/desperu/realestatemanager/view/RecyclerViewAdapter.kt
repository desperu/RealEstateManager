package org.desperu.realestatemanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.desperu.realestatemanager.model.Estate

class RecyclerViewAdapter(@LayoutRes private val layoutId: Int): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private lateinit var list: List<*>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list[position].let { holder.bind(it as Estate) }
//        holder.bind2(list2?.get(position))
    }

    override fun getItemViewType(position: Int) = layoutId

    override fun getItemCount(): Int = list.size

    fun updateList(list: List<*>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(any: Any?) {
            binding.setVariable(org.desperu.realestatemanager.BR.viewModel, any)
            binding.executePendingBindings()
        }

        fun bind2(any2: Any?) {
//            binding.setVariable(org.desperu.realestatemanager.BR.any2, any2)
            binding.executePendingBindings()
        }
    }
}
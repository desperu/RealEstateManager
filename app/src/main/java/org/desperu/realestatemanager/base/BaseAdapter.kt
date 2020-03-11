package org.desperu.realestatemanager.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    open fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getObjForPosition(position))
//        holder.bind2(getObj2ForPosition(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    protected abstract fun getObjForPosition(position: Int): Any?

//    protected abstract fun getObj2ForPosition(position: Int): Any?

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(any: Any?) {
//            binding.setVariable(org.desperu.realestatemanager.BR.any, any)
            binding.executePendingBindings()
        }
    }

//    class ViewHolder(private val binding: ItemPostBinding):RecyclerView.ViewHolder(binding.root){
//        private val viewModel = PostViewModel()
//
//        fun bind(post:Post){
//            viewModel.bind(post)
//            binding.viewModel = viewModel
//        }
//    }
}
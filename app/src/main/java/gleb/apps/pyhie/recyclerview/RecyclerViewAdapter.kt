package gleb.apps.pyhie.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingData

class RecyclerViewAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<RecyclerViewAdapter.BindingViewHolder>() {

    private val items = mutableListOf<RecyclerItem>()

    override fun getItemViewType(position: Int): Int {
        return getItem(position).layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false)
        return BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        getItem(position).bind(holder.binding)
        holder.binding.executePendingBindings()
    }

    private fun getItem(position: Int): RecyclerItem {
        return items[position]
    }

    fun updateData(newItems: List<RecyclerItem>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class BindingViewHolder (val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            listener.onItemClick( getItem(adapterPosition), adapterPosition)
        }
    }

    class OnItemClickListener( val clickListener: (recyclerItem: RecyclerItem, position: Int) -> Unit) {
        fun onItemClick(recyclerItem: RecyclerItem, position: Int) = clickListener(recyclerItem, position)
    }



}



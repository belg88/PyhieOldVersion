package gleb.apps.pyhie.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import gleb.apps.pyhie.R
import gleb.apps.pyhie.databinding.PlannerItemBinding
import gleb.apps.pyhie.pojos.PlannerData

class PlannerAdapter(private val clickListener: OnClickListener):RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder>() {
    private val plannerDataList: MutableList<PlannerData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlannerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: PlannerItemBinding = PlannerItemBinding.inflate(inflater, parent, false)

        return PlannerViewHolder(binding)
    }

    override fun getItemCount(): Int = plannerDataList.size

    private fun getItem(position: Int): PlannerData = plannerDataList[position]

    override fun onBindViewHolder(holder: PlannerViewHolder, position: Int) {
        holder.bind(clickListener,getItem(position))
    }

    class PlannerViewHolder(val binding: PlannerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind (clickListener: OnClickListener, plannerData: PlannerData) {
            binding.executePendingBindings()
        }
    }

    fun updateData(newItems: List<PlannerData>) {
        this.plannerDataList.clear()
        this.plannerDataList.addAll(newItems)
        notifyDataSetChanged()
    }

    class OnClickListener(val clickListener: (planner:PlannerData) -> Unit) {
        fun onClick(planner: PlannerData) = clickListener(planner)
    }
}



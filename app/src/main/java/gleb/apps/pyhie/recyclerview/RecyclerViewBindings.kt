package gleb.apps.pyhie.recyclerview

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import gleb.apps.pyhie.R
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingMain
import gleb.apps.pyhie.pojos.PlannerData
import gleb.apps.pyhie.pojos.SleepingData
import gleb.apps.pyhie.pojos.SleepingInfo

@BindingAdapter("items")
fun setRecyclerViewItems(
    recyclerView: RecyclerView,
    items: List<RecyclerItem>?
) {
    var adapter = (recyclerView.adapter as? RecyclerViewAdapter)
    if (adapter == null) {
        adapter = RecyclerViewAdapter(RecyclerViewAdapter.OnItemClickListener{recyclerItem, position ->  })
        recyclerView.adapter = adapter
    }
    adapter.updateData(items.orEmpty())
}

@BindingAdapter("setVisibility")
fun TextView.itemVisibility(itemVisibility: Int) {
   visibility = itemVisibility
}

@BindingAdapter("setColor")
fun TextView.setTextColor(textColor: Int) {
    setTextColor(textColor)
}

@BindingAdapter("setSrc")
fun ImageView.setSrc(id: Int) {
    setImageResource(id)
}


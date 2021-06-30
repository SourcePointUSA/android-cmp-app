package com.sourcepointmeta.metaapp.ui.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourcepointmeta.metaapp.R

internal class DemoAdapter() : RecyclerView.Adapter<DemoAdapter.Vh>() {

    private var list = mutableListOf<DemoActionItem>()
    var itemClickListener: ((DemoActionItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_action_demo, parent, false)
        return Vh(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)
    }

    class Vh(val view: View) : RecyclerView.ViewHolder(view)

    private fun Vh.bind(iv: DemoActionItem, pos: Int) {
        (view as DemoActionItemView).bind(iv)
    }

    fun addItems(newItems: List<DemoActionItem>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}

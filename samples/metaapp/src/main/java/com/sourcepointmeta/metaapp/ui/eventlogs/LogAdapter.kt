package com.sourcepointmeta.metaapp.ui.eventlogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.util.check
import kotlinx.android.synthetic.main.item_log.view.*
import org.json.JSONObject

class LogAdapter : RecyclerView.Adapter<LogAdapter.Vh>() {

    private var list = mutableListOf<LogItem>()

    var itemClickListener: ((LogItem) -> Unit)? = null
    val selectedIds = sortedSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return Vh(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)
    }

    class Vh(val view: View) : RecyclerView.ViewHolder(view)

    private fun Vh.bind(iv: LogItem, pos: Int) {
        (view as LogItemView).run {
            this.checkbox.setOnCheckedChangeListener { _, isChecked -> addId(isChecked, iv.id) }
            setOnClickListener {
                check { JSONObject(iv.jsonBody) }
                    .getOrNull()
                    ?.let { itemClickListener?.invoke(iv) }
            }
            this.bind(iv, pos)
        }
    }

    private fun addId(checked: Boolean, id: Long?) {
        id ?: return
        if (checked) selectedIds.add(id)
        else selectedIds.remove(id)
    }

    fun addItems(newItems: List<LogItem>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(item: LogItem) {
        list.add(0, item)
        notifyItemInserted(0)
    }

    fun deleteItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun deleteAllItems() {
        list.clear()
        notifyDataSetChanged()
    }
}

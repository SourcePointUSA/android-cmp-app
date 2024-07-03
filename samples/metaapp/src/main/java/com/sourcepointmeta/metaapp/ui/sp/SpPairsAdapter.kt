package com.sourcepointmeta.metaapp.ui.sp

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.sourcepointmeta.metaapp.databinding.SpListGroupBinding
import com.sourcepointmeta.metaapp.databinding.SpListItemBinding
import java.util.SortedMap

internal class SpPairsAdapter(
    private val context: Context,
) : BaseExpandableListAdapter() {

    private val expandableListTitle: MutableList<String> = mutableListOf()
    private var expandableListDetail: SortedMap<String, List<String>> = hashMapOf<String, List<String>>().toSortedMap()

    override fun getGroupView(listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val listTitle = getGroup(listPosition) as String

        val groupBinding = SpListGroupBinding.inflate(LayoutInflater.from(context), parent, false)

        groupBinding.listTitle.apply {
            setTypeface(null, Typeface.BOLD)
            text = listTitle
        }

        return groupBinding.root
    }

    override fun getChildView(
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val expandedListText = getChild(listPosition, expandedListPosition) as String

        val itemBinding = SpListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        itemBinding.expandedListItem.text = expandedListText

        return itemBinding.root
    }

    fun addAndClearElements(expandableList: SortedMap<String, List<String>>) {
        expandableListDetail.clear()
        expandableListTitle.clear()
        expandableListDetail = expandableList
        expandableListTitle.addAll(expandableListDetail.map { it.key })
        notifyDataSetChanged()
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return this.expandableListDetail[this.expandableListTitle[listPosition]]?.size ?: 0
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        return this.expandableListDetail[this.expandableListTitle[listPosition]]?.get(expandedListPosition) ?: 0
    }

    fun getValueByKey(groupPosition: Int, childPosition: Int): String? {
        val key: String = expandableListTitle[groupPosition]
        val list: List<String> = expandableListDetail[key] ?: emptyList()
        return list.getOrNull(childPosition)
    }

    fun getKeyByPosition(groupPosition: Int): String = expandableListTitle[groupPosition]

    override fun getGroupId(listPosition: Int): Long = listPosition.toLong()

    override fun getGroup(listPosition: Int): Any = this.expandableListTitle[listPosition]

    override fun getGroupCount(): Int = this.expandableListTitle.size

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long = expandedListPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true
}

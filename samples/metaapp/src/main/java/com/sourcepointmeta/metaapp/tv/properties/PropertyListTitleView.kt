package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.TitleViewAdapter
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.property_list_title.view.*


class PropertyListTitleView(context: Context, attrs: AttributeSet?, defStyle: Int) : ConstraintLayout(context, attrs, defStyle), TitleViewAdapter.Provider {
    private var mTitleViewAdapter: TitleViewAdapter = object : TitleViewAdapter() {
        // Nothing to return, there is no SearchBar
        override fun getSearchAffordanceView(): View = View(context)
    }

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    override fun getTitleViewAdapter(): TitleViewAdapter {
        return mTitleViewAdapter
    }

    init {
        val root: View = LayoutInflater.from(context).inflate(R.layout.property_list_title, this, true)

        val mRemoveAllBtn: Button? = root.findViewById(R.id.removeAllBtn)
        mRemoveAllBtn?.setOnClickListener(){
            Toast.makeText(context, "Remove All button clicked", Toast.LENGTH_SHORT).show()
        }

        val mAddBtn: Button? = root.findViewById(R.id.addBtn)
        mAddBtn?.setOnClickListener(){
            Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
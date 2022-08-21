package com.sourcepointmeta.metaapp.tv

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.leanback.widget.TitleViewAdapter
import com.sourcepointmeta.metaapp.R


class CustomTitleView(context: Context?, attrs: AttributeSet?, defStyle: Int) :
    RelativeLayout(context, attrs, defStyle), TitleViewAdapter.Provider {
    private var mTitleView: TextView? = null
    private var mAddBtn: Button? = null
    var mTitleViewAdapter: TitleViewAdapter = object : TitleViewAdapter() {
        override fun getSearchAffordanceView(): View? {
            return null
        }

        override fun setTitle(titleText: CharSequence?) {
            this@CustomTitleView.setTitle(titleText)
        }

        override fun setBadgeDrawable(drawable: Drawable?) {
            this@CustomTitleView.setBadgeDrawable(drawable);
        }

        override fun updateComponentsVisibility(flags: Int) {
            /*if ((flags & BRANDING_VIEW_VISIBLE) == BRANDING_VIEW_VISIBLE) {
            updateBadgeVisibility(true);
        } else {
            mAnalogClockView.setVisibility(View.GONE);
            mTitleView.setVisibility(View.GONE);
        }*/
            val visibility: Int =
                if (flags and SEARCH_VIEW_VISIBLE == SEARCH_VIEW_VISIBLE) View.VISIBLE else View.INVISIBLE
//            mSearchOrbView?.visibility = visibility
        }

        private fun updateBadgeVisibility(visible: Boolean) {
            if (visible) {
                mTitleView?.visibility = View.VISIBLE
            } else {
                mTitleView?.visibility = View.GONE
            }
        }
    }

    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    fun setTitle(title: CharSequence?) {
        if (title != null) {
            mTitleView?.text = title
            mTitleView?.visibility = View.VISIBLE
        }
    }

    fun setBadgeDrawable(drawable: Drawable?) {
        if (drawable != null) {
            mTitleView?.visibility = View.GONE
        }
    }

    override fun getTitleViewAdapter(): TitleViewAdapter {
        return mTitleViewAdapter
    }


    init {
        val root: View = LayoutInflater.from(context).inflate(R.layout.tv_custom_titleview, this, true)
        mTitleView = root.findViewById(R.id.titleTextView)
        mAddBtn = root.findViewById(R.id.addBtn)

        mAddBtn?.let {
            setOnClickListener {
                Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show()
            }
        }
//        mTitleView?.let {
//            setOnClickListener {
//                Toast.makeText(context, "Title clicked", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}
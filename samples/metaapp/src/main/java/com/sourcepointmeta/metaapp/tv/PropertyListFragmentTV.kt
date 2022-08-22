package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO


class PropertyListFragmentTV : BrowseSupportFragment() {
    companion object {
        private val TAG = "MainFragment"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)
        setupUIElements()
        loadRows()
        setupEventListeners()
    }

    private fun setupUIElements() {
        title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireActivity(), R.color.purple_500)
    }
    private fun loadRows() {
        // TODO("Not implemented yet")
    }
    private fun setupEventListeners() {
        onItemViewClickedListener = ItemViewClickedListener()

        // Remove Leanback interception on focus mAddBtn on CustomTitleView
        val browseFrameLayout = requireView().findViewById<BrowseFrameLayout>(R.id.browse_frame)
        browseFrameLayout.onFocusSearchListener = null

        // Remove Leanback interception on AddBtn.clickListener()
        titleView.setOnClickListener {  }
        // Add OnClickListener() to CustomTitleView.mTitleViewAdapter.getSearchAffordanceView() return (mAddBtn)
        titleViewAdapter.setOnSearchClickedListener {
            Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class ItemViewClickedListener() : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            Toast.makeText(requireActivity(), "${(item as PropertyDTO).propertyName} property clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
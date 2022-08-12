package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.cards.PropertyCardPresenter
import com.sourcepointmeta.metaapp.ui.BaseState

import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PropertyListFragmentTV : BrowseSupportFragment() {
    companion object {
        private val TAG = "MainFragment"
    }

    private val viewModel: PropertyListViewModel by viewModel()

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
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.purple_200)
    }
    private fun loadRows() {
        // TODO("Not implemented yet")
    }
    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireActivity(), "Update", Toast.LENGTH_LONG).show()
            viewModel.fetchPropertyList()
        }
        onItemViewClickedListener = ItemViewClickedListener()
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
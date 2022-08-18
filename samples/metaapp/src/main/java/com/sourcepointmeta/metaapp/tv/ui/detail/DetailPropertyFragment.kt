package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.ui.arrayObjectAdapter
import com.sourcepointmeta.metaapp.tv.ui.detail.DetailPropertyActivity.Companion.PROPERTY_NAME_KEY
import com.sourcepointmeta.metaapp.tv.ui.edit.AddUpdatePropertyViewModelTv
import com.sourcepointmeta.metaapp.tv.ui.initEntranceTransition
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailPropertyFragment : DetailsSupportFragment() {

    companion object {
        fun instance(
            propertyName: String?
        ) = DetailPropertyFragment().apply {
            arguments = Bundle().apply {
                putString(PROPERTY_NAME_KEY, propertyName)
            }
        }
    }

    private val viewModel by viewModel<AddUpdatePropertyViewModelTv>()

    private val propertyName by lazy {
        val name = arguments?.getString(PROPERTY_NAME_KEY) ?: defaultProperty.propertyName
        viewModel.fetchPropertySync(name)
    }

    var navListener: ((String, Int) -> Unit)? = null

    private val listener: (View, Int) -> Unit = { _, type ->
        navListener?.invoke(propertyName.propertyName, type)
    }

    private val actionListener: (Action, Property) -> Unit = { a, i ->
        Toast.makeText(requireContext(), "Run Action", Toast.LENGTH_SHORT).show()
    }

    private val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), "t_for_transition")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareEntranceTransition()

        adapter = ArrayObjectAdapter(createPresenterSelector(propertyName, actionListener, listener, helper)).apply {
            add(DetailsOverviewRow(propertyName).arrayObjectAdapter(Pair(1, "Run Demo")))
        }

        initEntranceTransition()
    }
}

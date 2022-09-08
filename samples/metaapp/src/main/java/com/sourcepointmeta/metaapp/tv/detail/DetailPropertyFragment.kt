package com.sourcepointmeta.metaapp.tv.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.arrayObjectAdapter
import com.sourcepointmeta.metaapp.tv.demo.DemoActivityTv
import com.sourcepointmeta.metaapp.tv.detail.DetailPropertyActivity.Companion.PROPERTY_NAME_KEY
import com.sourcepointmeta.metaapp.tv.edit.AddUpdatePropertyViewModelTv
import com.sourcepointmeta.metaapp.tv.initEntranceTransition
import com.sourcepointmeta.metaapp.tv.updatePListAndSelectLast
import com.sourcepointmeta.metaapp.tv.updatePropertyListAndClose
import com.sourcepointmeta.metaapp.ui.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailPropertyFragment : DetailsSupportFragment() {

    companion object {
        const val ACTION_RUN_DEMO = 1L
        const val ACTION_DELETE = 2L
        const val ACTION_DUPLICATE = 3L
    }

    private val viewModel by viewModel<AddUpdatePropertyViewModelTv>()

    var navListener: ((String, Int) -> Unit)? = null

    private val actionListener: (Action, Property) -> Unit = { a, i ->
        when (a.id) {
            ACTION_RUN_DEMO -> runDemo(viewModel.fetchPropertySync(i.propertyName))
            ACTION_DELETE -> {
                viewModel.deletePropertySync(i.propertyName)
                requireActivity().updatePropertyListAndClose()
            }
            ACTION_DUPLICATE -> {
                viewModel.duplicatePropertySync(i.propertyName)
                requireActivity().updatePListAndSelectLast()
            }
        }
    }

    private fun runDemo(property: Property) {
        val bundle = Bundle()
        bundle.putString(PROPERTY_NAME_KEY, property.propertyName)
        val i = Intent(activity, DemoActivityTv::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }

    private val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), "t_for_transition")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareEntranceTransition()
        initEntranceTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StateProperty -> showProperty(it.property)
                // TO DO handle error case
            }
        }
        val propertyName = arguments?.getString(PROPERTY_NAME_KEY) ?: defaultProperty.propertyName
        viewModel.fetchPropertyOrDefault(propertyName, defaultProperty)
    }

    fun updateProperty(propertyName: String?) {
        propertyName ?: return
        viewModel.fetchProperty(propertyName)
    }

    fun refreshPropertyNameArgument(propertyName: String?) {
        propertyName ?: return
        arguments = Bundle().apply {
            putString(PROPERTY_NAME_KEY, propertyName)
        }
    }

    private fun showProperty(property: Property) {
        adapter = ArrayObjectAdapter(
            createPresenterSelector(
                property,
                actionListener,
                { _, type -> navListener?.invoke(property.propertyName, type) },
                helper
            )
        ).apply {
            add(
                DetailsOverviewRow(property).arrayObjectAdapter(
                    Pair(ACTION_RUN_DEMO, "Run Demo"),
                    Pair(ACTION_DELETE, "Delete Property"),
                    Pair(ACTION_DUPLICATE, "Duplicate Property")
                )
            )
        }
    }
}

package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import com.sourcepointmeta.metaapp.tv.ui.PropertyTvDTO
import com.sourcepointmeta.metaapp.tv.ui.arrayObjectAdapter
import com.sourcepointmeta.metaapp.tv.ui.initEntranceTransition
import com.sourcepointmeta.metaapp.tv.ui.toPropertyTvDTO

class DetailPropertyFragment : DetailsSupportFragment() {

    companion object {
        fun instance(
            propertyName: String
        ) = DetailPropertyFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    private val propertyTvDTO by lazy {
        val name = arguments?.getString("property_name") ?: "" // throw RuntimeException("Property name not set!!!")
//        dataSource.fetchPropertyByNameSync(name) USE THIS TO FETCH the prop
        prop1.toPropertyTvDTO()
    }

    var navListener: (() -> Unit)? = null

    private val listener: (View) -> Unit = { view ->
        Toast.makeText(requireContext(), "Run", Toast.LENGTH_SHORT).show()
        navListener?.invoke()
    }

    private val actionListener: (Action, PropertyTvDTO) -> Unit = { a, i ->
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

        adapter = ArrayObjectAdapter(createPresenterSelector(propertyTvDTO, actionListener, listener, helper)).apply {
            add(DetailsOverviewRow(propertyTvDTO).arrayObjectAdapter(Pair(1, "Run Demo")))
        }

        initEntranceTransition()
    }
}

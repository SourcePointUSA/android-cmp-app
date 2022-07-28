package com.sourcepointmeta.metaapp.tv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.core.replaceWithoutBackstackFragment
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.*
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyViewModel
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.add_property_fragment.*
import kotlinx.android.synthetic.main.add_property_fragment.tool_bar
import kotlinx.android.synthetic.main.add_targeting_parameter.*
import kotlinx.android.synthetic.main.fragment_property_list.*
import kotlinx.android.synthetic.main.property_item.view.*
import kotlinx.android.synthetic.main.property_item_tv_controls.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PropertyItemControlsTV(
    val propertyDTO: PropertyDTO
): Fragment() {
    private val viewModel: PropertyListViewModel by viewModel()
    private val errorColor: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.property_item_tv_controls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StatePropertyList -> successState(it)
                is BaseState.StateError -> errorState(it)
                is BaseState.StateProperty -> updateProperty(it)
                is BaseState.StateLoading -> savingProperty(it.propertyName, it.loading)
                is BaseState.StateVersion -> showVersionPopup(it.version)
            }
        }

        // TODO("NEED ACCESS TO PROPERTIES LIST AND PROPERTY POSITION IN LIST")

        demoBtn.setOnClickListener { runDemo(propertyDTO.property) }
        editBtn.setOnClickListener {
            (activity as? AppCompatActivity)?.replaceWithoutBackstackFragment(
                R.id.container,
                AddUpdatePropertyFragment.instance(propertyDTO.propertyName)
            )
        }
        backBtn.setOnClickListener { activity?.onBackPressed() }
    }

    private fun updateProperty(state: BaseState.StateProperty) {
//        adapter.updateProperty(state.property.toPropertyDTO())
    }
    private fun savingProperty(propertyName: String, showLoading: Boolean) {
//        adapter.savingProperty(propertyName, showLoading)
    }
    private fun successState(it: BaseState.StatePropertyList) {
//        it.propertyList
//            .map { p -> p.toPropertyDTO() }
//            .let { adapter.addItems(it) }
    }
    private fun errorState(it: BaseState.StateError) {
    }
    private fun showVersionPopup(version: String) {
        tool_bar.setTitleTextColor(errorColor)
        tool_bar.title = "${tool_bar.title} -> $version"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Metaapp version ${BuildConfig.VERSION_NAME} out of date, new version $version is available.")
            .setPositiveButton("Update it") { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sourcepointmeta.metaapp")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.sourcepointmeta.metaapp")))
                }
            }
            .setNegativeButton("Continue") { _, _ -> }
            .show()
    }


    private fun runDemo(property: Property) {
        val bundle = Bundle()
        bundle.putString("property_name", property.propertyName)
        val i = Intent(activity, DemoActivity::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }
}
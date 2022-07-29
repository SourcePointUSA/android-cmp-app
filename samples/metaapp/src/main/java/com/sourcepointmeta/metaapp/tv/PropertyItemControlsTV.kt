package com.sourcepointmeta.metaapp.tv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.replaceFragmentWithoutBackstack
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.*
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyViewModel
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.add_property_fragment.*
import kotlinx.android.synthetic.main.property_item.*
import kotlinx.android.synthetic.main.property_item_tv_controls.*
import kotlinx.android.synthetic.main.property_item_tv_controls.chip_ccpa
import kotlinx.android.synthetic.main.property_item_tv_controls.chip_gdpr
import org.koin.androidx.viewmodel.ext.android.viewModel

class PropertyItemControlsTV(
    private val propertyDTO: PropertyDTO
): Fragment() {
    // TODO: refactor, using AddUpdateAdapter is incorrect, probably...
//    private val viewModel by viewModel<AddUpdatePropertyViewModel>()
    private val viewModel: PropertyListViewModel by viewModel()
    private val errorColor: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    // Local set for caching GDPR/CCPA status and save it on "back"
    private val editedSet = mutableSetOf<StatusCampaign>().apply { addAll(propertyDTO.property.statusCampaignSet) }

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
        deleteBtn.setOnClickListener { deleteProperty() }
        demoBtn.setOnClickListener { runDemo(propertyDTO.property) }
        editBtn.setOnClickListener {
            (activity as? AppCompatActivity)?.replaceFragmentWithoutBackstack(
                R.id.container,
                AddUpdatePropertyFragment.instance(propertyDTO.propertyName)
            )
        }
        backBtn.setOnClickListener {
            val editedSet = setOf(
                StatusCampaign(propertyDTO.propertyName, CampaignType.CCPA, chip_ccpa.isChecked),
                StatusCampaign(propertyDTO.propertyName, CampaignType.GDPR, chip_gdpr.isChecked)
            )
            // -----
            // Is following 2 lines doing the same? if yes better to use
            // viewModel: PropertyListViewModel by viewModel()
            // because PropertyListViewModel provides Delete
//            viewModel.createOrUpdateProperty(propertyDTO.property.copy(statusCampaignSet = editedSet))
            viewModel.updateProperty(propertyDTO.property.copy(statusCampaignSet = editedSet))
            // ------
            activity?.onBackPressed()
        }

        chip_ccpa.isChecked = propertyDTO.ccpaEnabled
        chip_gdpr.isChecked = propertyDTO.gdprEnabled
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

    private fun deleteProperty() {
        viewModel.deleteProperty(propertyDTO.propertyName)
        activity?.onBackPressed()
    }
}
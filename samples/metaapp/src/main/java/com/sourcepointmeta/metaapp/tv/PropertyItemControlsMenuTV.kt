package com.sourcepointmeta.metaapp.tv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.*
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.property_item_controls_menu.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PropertyItemControlsMenuTV(
    private val supportFragmentManager: FragmentManager,
    private val propertyDTO: PropertyDTO
): Fragment() {
    private val viewModel: PropertyListViewModel by viewModel()
    private val errorColor: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    // Local set for caching GDPR/CCPA status and update it on "back"
    private val editedSet = mutableSetOf<StatusCampaign>().apply { addAll(propertyDTO.property.statusCampaignSet) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.property_item_controls_menu, container, false)
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

        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val editedSet = setOf(
                    StatusCampaign(propertyDTO.propertyName, CampaignType.CCPA, chip_ccpa.isChecked),
                    StatusCampaign(propertyDTO.propertyName, CampaignType.GDPR, chip_gdpr.isChecked)
                )
                viewModel.updateProperty(propertyDTO.property.copy(statusCampaignSet = editedSet))
//                activity?.onBackPressed()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PropertyListFragmentTV(supportFragmentManager))
                    .commitNow()
            }
        })

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
//        tool_bar.setTitleTextColor(errorColor)
//        tool_bar.title = "${tool_bar.title} -> $version"
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Metaapp version ${BuildConfig.VERSION_NAME} out of date, new version $version is available.")
//            .setPositiveButton("Update it") { _, _ ->
//                try {
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sourcepointmeta.metaapp")))
//                } catch (e: ActivityNotFoundException) {
//                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.sourcepointmeta.metaapp")))
//                }
//            }
//            .setNegativeButton("Continue") { _, _ -> }
//            .show()
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
        Toast.makeText(requireContext(), "Delete ${propertyDTO.propertyName}", Toast.LENGTH_SHORT).show()
        activity?.onBackPressed()
    }
}
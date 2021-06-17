package com.sourcepointmeta.metaapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.addChip
import com.sourcepointmeta.metaapp.ui.component.bind
import com.sourcepointmeta.metaapp.ui.component.errorField
import com.sourcepointmeta.metaapp.ui.component.toProperty
import kotlinx.android.synthetic.main.add_property_fragment.*
import kotlinx.android.synthetic.main.add_targeting_parameter.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddUpdatePropertyFragment : Fragment() {

    private val viewModel by viewModel<AddUpdatePropertyViewModel>()

    private val messageLanguage = MessageLanguage.values()
    private val pmTabs = PMTab.values()

    companion object {
        val MessageType = listOf("WebView", "App")
        fun instance(propertyName: String) = AddUpdatePropertyFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.add_property_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageOptionAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, MessageType)
        message_type_autocomplete.setAdapter(messageOptionAdapter)
        message_type_autocomplete.setText(MessageType.first())
        message_type_autocomplete.threshold = 1

        val languages = messageLanguage.map { it.name }
        val messageLanguageAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, languages)
        message_language_autocomplete.setAdapter(messageLanguageAdapter)
        message_language_autocomplete.setText(languages.first { it.startsWith("ENG") })
        message_language_autocomplete.threshold = 1

        val tabs = pmTabs.map { it.name }
        val pmTabsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, tabs)
        pm_tab_autocomplete.setAdapter(pmTabsAdapter)
        pm_tab_autocomplete.setText(tabs.first { it.startsWith("PUR") })
        pm_tab_autocomplete.threshold = 1

        arguments?.getString("property_name")?.let { viewModel.fetchProperty(it) }

        btn_targeting_params_gdpr.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add new targeting parameter")
                .setView(R.layout.add_targeting_parameter)
                .setPositiveButton("Create") { dialog, _ ->
                    (dialog as? AlertDialog)?.let { d ->
                        val key = d.tp_key_ed.text
                        val value = d.tp_value_et.text
                        gdpr_chip_group.addChip("$key:$value")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btn_targeting_params_ccpa.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add new targeting parameter")
                .setView(R.layout.add_targeting_parameter)
                .setPositiveButton("Create") { dialog, _ ->
                    (dialog as? AlertDialog)?.let { d ->
                        val key = d.tp_key_ed.text
                        val value = d.tp_value_et.text
                        ccpa_chip_group.addChip("$key:$value")
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        save_btn.setOnClickListener {
            viewModel.createOrUpdateProperty(add_property_layout.toProperty())
        }

        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StatePropertySaved -> propertySavedState()
                is BaseState.StateProperty -> add_property_layout.bind(it.property)
                is BaseState.StateError -> errorState(it)
                is BaseState.StateErrorValidationField -> add_property_layout.errorField(it)
                else -> { }
            }
        }
    }

    private fun propertySavedState() {
        (activity as? AppCompatActivity)?.onBackPressed()
    }

    private fun errorState(it: BaseState.StateError) {
    }
}

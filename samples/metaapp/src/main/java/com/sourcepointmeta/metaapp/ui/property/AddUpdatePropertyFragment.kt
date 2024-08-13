package com.sourcepointmeta.metaapp.ui.property

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.databinding.AddPropertyFragmentBinding
import com.sourcepointmeta.metaapp.databinding.AddTargetingParameterBinding
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.addChip
import com.sourcepointmeta.metaapp.ui.component.bind
import com.sourcepointmeta.metaapp.ui.component.errorField
import com.sourcepointmeta.metaapp.ui.component.toProperty
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddUpdatePropertyFragment : Fragment() {

    private val viewModel by viewModel<AddUpdatePropertyViewModel>()
    private lateinit var binding: AddPropertyFragmentBinding

    private val messageLanguage = MessageLanguage.values()
    private val pmTabs = PMTab.values()

    companion object {
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
        binding = AddPropertyFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolBar.run {
            title = "${BuildConfig.VERSION_NAME} - ${getString(R.string.add_prop_title)}"
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        val languages = messageLanguage.map { it.name }
        val messageLanguageAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, languages)
        binding.messageLanguageAutocomplete.setAdapter(messageLanguageAdapter)
        binding.messageLanguageAutocomplete.setText(languages.first { it.startsWith("ENG") })
        binding.messageLanguageAutocomplete.threshold = 1

        val types = MessageType.values().map { it.name }
        val messageTypeAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, types)
        binding.messageTypeAutocomplete.setAdapter(messageTypeAdapter)
        binding.messageTypeAutocomplete.setText(types.first { it.startsWith("MOBILE") })
        binding.messageTypeAutocomplete.threshold = 1

        val tabs = pmTabs.map { it.name }
        val pmTabsAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.item_for_autocomplete, tabs)
        binding.pmTabAutocomplete.setAdapter(pmTabsAdapter)
        binding.pmTabAutocomplete.setText(tabs.first { it.startsWith("PUR") })
        binding.pmTabAutocomplete.threshold = 1

        arguments?.getString("property_name")?.let { viewModel.fetchProperty(it) }

        binding.btnTargetingParamsGdpr.setOnClickListener {
            val dialogBinding = AddTargetingParameterBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add new targeting parameter")
                .setView(R.layout.add_targeting_parameter)
                .setPositiveButton("Create") { _, _ ->
                    val key = dialogBinding.tpKeyEd.text
                    val value = dialogBinding.tpValueEt.text
                    binding.gdprChipGroup.addChip("$key:$value")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnTargetingParamsCcpa.setOnClickListener {
            val dialogBinding = AddTargetingParameterBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add new targeting parameter")
                .setView(R.layout.add_targeting_parameter)
                .setPositiveButton("Create") { _, _ ->
                    val key = dialogBinding.tpKeyEd.text
                    val value = dialogBinding.tpValueEt.text
                    binding.ccpaChipGroup.addChip("$key:$value")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.btnTargetingParamsUsnat.setOnClickListener {
            val dialogBinding = AddTargetingParameterBinding.inflate(layoutInflater)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add new targeting parameter")
                .setView(R.layout.add_targeting_parameter)
                .setPositiveButton("Create") { _, _ ->
                    val key = dialogBinding.tpKeyEd.text
                    val value = dialogBinding.tpValueEt.text
                    binding.usnatChipGroup.addChip("$key:$value")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.saveBtn.setOnClickListener {
            viewModel.createOrUpdateProperty(binding.addPropertyLayout.toProperty(binding))
        }

        binding.gppSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.optOutOptionRadioGroup.isEnabled = isChecked
            binding.serviceProviderModeRadioGroup.isEnabled = isChecked
            binding.optOutOptionRadioNa.isEnabled = isChecked
            binding.optOutOptionRadioNo.isEnabled = isChecked
            binding.optOutOptionRadioYes.isEnabled = isChecked
            binding.serviceProviderRadioNa.isEnabled = isChecked
            binding.serviceProviderRadioNo.isEnabled = isChecked
            binding.serviceProviderRadioYes.isEnabled = isChecked
            binding.gppFieldCoveredTransaction.isEnabled = isChecked
        }

        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StatePropertySaved -> propertySavedState()
                is BaseState.StateProperty -> binding.addPropertyLayout.bind(it.property, binding)
                is BaseState.StateError -> errorState(it)
                is BaseState.StateErrorValidationField -> binding.addPropertyLayout.errorField(it, binding)
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

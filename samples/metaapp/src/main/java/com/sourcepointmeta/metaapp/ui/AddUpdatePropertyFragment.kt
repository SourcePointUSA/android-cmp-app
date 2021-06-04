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
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.addChip
import kotlinx.android.synthetic.main.add_property_fragment.* // ktlint-disable
import kotlinx.android.synthetic.main.add_targeting_parameter.* // ktlint-disable

class AddUpdatePropertyFragment : Fragment() {

    private val viewModel = AddUpdatePropertyViewModel()

    private val messageOption = listOf("WebView", "App")
    private val messageLanguage = MessageLanguage.values()

    companion object {
        fun instance(propertyName: String) = AddUpdatePropertyFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_item, messageOption)
        message_type_autocomplete.setAdapter(adapter)
        message_type_autocomplete.setText(messageOption.first())
        message_type_autocomplete.threshold = 1

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
            // viewModel.createProperty // add prop
            propertySavedState()
        }
    }

    private fun propertySavedState() {
        (activity as? AppCompatActivity)?.onBackPressed()
    }
}

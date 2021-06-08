package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.ui.BaseState.* // ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_property_list.*
import org.koin.android.ext.android.inject

class PropertyListFragment : Fragment() {

    private val viewModel: PropertyListViewModel by inject()

    private val adapter by lazy { PropertyAdapter() }
    private val itemTouchHelper by lazy { ItemTouchHelper(swipeToDeleteCallback) }
    private val swipeToDeleteCallback: SwipeToDeleteCallback by lazy {
        SwipeToDeleteCallback(requireContext()) { showDeleteDialog(it, adapter) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_property_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            if (it is StateSuccess) successState(it)
            else if (it is StateError) errorState(it)
        }
        property_list.layoutManager = GridLayoutManager(context, 1)
        property_list.adapter = adapter
        fab.setOnClickListener {
            (activity as? AppCompatActivity)?.addFragment(R.id.container, AddUpdatePropertyFragment())
        }
        (activity as? AppCompatActivity)?.supportFragmentManager?.addOnBackStackChangedListener {
            viewModel.fetchPropertyList()
        }
        adapter.itemClickListener = {
            (activity as? AppCompatActivity)?.addFragment(
                R.id.container,
                AddUpdatePropertyFragment.instance(it.propertyName)
            )
        }
        adapter.propertyChangedListener = { viewModel.updateProperty(it) }
        adapter.demoProperty = { runDemo(it) }
        itemTouchHelper.attachToRecyclerView(property_list)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPropertyList()
    }

    private fun successState(it: StateSuccess) {
        it.propertyList
            .map {
                val env = if (it.is_staging) "stage" else "prod"
                PropertyDTO(
                    campaignEnv = env,
                    propertyName = it.propertyName,
                    accountId = it.accountId,
                    messageType = it.messageType,
                    ccpaEnabled = it.statusCampaignSet.find { s -> s.campaignType == CampaignType.CCPA }?.enabled
                        ?: false,
                    gdprEnabled = it.statusCampaignSet.find { s -> s.campaignType == CampaignType.GDPR }?.enabled
                        ?: false,
                    property = it
                )
            }
            .let { adapter.addItems(it) }
    }

    private fun errorState(it: StateError) {
    }

    private fun showDeleteDialog(position: Int, adapter: PropertyAdapter) {
        val propertyName = adapter.getPropertyNameByPosition(position)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Property $propertyName")
            .setPositiveButton("Confirm") { _, _ ->
                adapter.notifyItemChanged(position)
                viewModel.deleteProperty(propertyName)
            }
            .setNegativeButton("Cancel") { _, _ -> adapter.notifyItemChanged(position) }
            .show()
    }

    private fun runDemo(propertyName: String) {
    }
}

package com.sourcepointmeta.metaapp.ui

import android.content.Intent
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
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.DemoActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.ui.BaseState.* // ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_property_list.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class PropertyListFragment : Fragment() {

    private val viewModel: PropertyListViewModel by inject()
    private val clearDb: Boolean by inject(qualifier = named("clear_db"))

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
        if (clearDb) {
            viewModel.clearDB()
        }

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
            .map { p ->
                val env = if (p.is_staging) "stage" else "prod"
                PropertyDTO(
                    campaignEnv = env,
                    propertyName = p.propertyName,
                    accountId = p.accountId,
                    messageType = p.messageType,
                    ccpaEnabled = p.statusCampaignSet.find { s -> s.campaignType == CampaignType.CCPA }?.enabled
                        ?: false,
                    gdprEnabled = p.statusCampaignSet.find { s -> s.campaignType == CampaignType.GDPR }?.enabled
                        ?: false,
                    property = p,
                    ccpaPmId = p.ccpaPmId?.toString() ?: "",
                    gdprPmId = p.gdprPmId?.toString() ?: "",
                    pmTab = PMTab.values().find { it.name == p.pmTab } ?: PMTab.DEFAULT,
                    authId = p.authId ?: "",
                    messageLanguage = MessageLanguage.values().find { it.name == p.messageLanguage } ?: MessageLanguage.ENGLISH
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
        val bundle = Bundle()
        bundle.putString("property_name", propertyName)
        val i = Intent(activity, DemoActivity::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }

}

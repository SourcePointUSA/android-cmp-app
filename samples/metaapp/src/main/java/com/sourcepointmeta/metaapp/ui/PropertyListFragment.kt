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
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.ui.BaseState.* // ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import kotlinx.android.synthetic.main.fragment_property_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class PropertyListFragment : Fragment() {

    private val viewModel: PropertyListViewModel by viewModel()
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
            when (it) {
                is StatePropertyList -> successState(it)
                is StateError -> errorState(it)
                is StateProperty -> updateProperty(it)
                is StateLoading -> savingProperty(it.propertyName, it.loading)
            }
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

    private fun updateProperty(state: StateProperty) {
        adapter.updateProperty(state.property.toPropertyDTO())
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPropertyList()
    }

    private fun successState(it: StatePropertyList) {
        it.propertyList
            .map { p -> p.toPropertyDTO() }
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

    private fun savingProperty(propertyName: String, showLoading: Boolean) {
        adapter.savingProperty(propertyName, showLoading)
    }

    private fun runDemo(property: Property) {
        if (property.hasActiveCampaign()) {
            val bundle = Bundle()
            bundle.putString("property_name", property.propertyName)
            val i = Intent(activity, DemoActivity::class.java)
            i.putExtras(bundle)
            startActivity(i)
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("You need at least one active campaign to run the demo.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}

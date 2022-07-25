package com.sourcepointmeta.metaapp.tv

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sourcepoint.cmplibrary.util.clearAllData
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.fragment_property_list.*
import kotlinx.android.synthetic.main.fragment_property_list.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class PropertyListFragmentTV: Fragment() {

    private val viewModel: PropertyListViewModel by viewModel()
    private val clearDb: Boolean by inject(qualifier = named("clear_db"))

    private val errorColor: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    private val adapter by lazy { PropertyAdapter() }
    private val itemTouchHelper by lazy { ItemTouchHelper(swipeToDeleteCallback) }
    private val swipeToDeleteCallback: SwipeToDeleteCallback by lazy {
        SwipeToDeleteCallback(requireContext()) { showDeleteDialog(it, adapter) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_property_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (clearDb) {
            viewModel.clearDB()
        }

        tool_bar.title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"

        add_property_button?.setOnClickListener {
            (activity as? AppCompatActivity)?.addFragment(
                R.id.container,
                AddUpdatePropertyFragment.instance("EMPTY_NAME")
            )
        }

        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StatePropertyList -> successState(it)
                is BaseState.StateError -> errorState(it)
                is BaseState.StateProperty -> updateProperty(it)
                is BaseState.StateLoading -> savingProperty(it.propertyName, it.loading)
                is BaseState.StateVersion -> showVersionPopup(it.version)
            }
        }
        property_list.layoutManager = GridLayoutManager(context, 3)
        property_list.adapter = adapter
//        fab.setOnClickListener { (activity as? AppCompatActivity)?.addFragment(R.id.container, AddUpdatePropertyFragment()) }
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

        if (BuildConfig.BUILD_TYPE == "release") {
            viewModel.fetchLatestVersion()
        }
        tool_bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_clear_sp -> {
                    context?.let { clearAllData(it) }
                }
            }
            true
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_prop_list, menu)
    }
    override fun onResume() {
        super.onResume()
        viewModel.fetchPropertyList()
    }

    private fun updateProperty(state: BaseState.StateProperty) {
        adapter.updateProperty(state.property.toPropertyDTO())
    }
    private fun savingProperty(propertyName: String, showLoading: Boolean) {
        adapter.savingProperty(propertyName, showLoading)
    }

    private fun successState(it: BaseState.StatePropertyList) {
        it.propertyList
            .map { p -> p.toPropertyDTO() }
            .let { adapter.addItems(it) }
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

    private fun runDemo(property: Property) {
        val bundle = Bundle()
        bundle.putString("property_name", property.propertyName)
        val i = Intent(activity, DemoActivity::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }
}
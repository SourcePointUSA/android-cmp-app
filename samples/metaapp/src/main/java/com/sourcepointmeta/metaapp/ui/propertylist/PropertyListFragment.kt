package com.sourcepointmeta.metaapp.ui.propertylist

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.TypedValue
import android.view.* //ktlint-disable
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
import com.sourcepointmeta.metaapp.ui.BaseState.* //ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.sp.PreferencesActivity
import com.sourcepointmeta.metaapp.util.* //ktlint-disable
import kotlinx.android.synthetic.main.fragment_property_list.* //ktlint-disable
import org.json.JSONObject
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import java.io.File

class PropertyListFragment : Fragment() {

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

    companion object {
        const val OLD_V6_CONSENT = "sp.old.v6.consent"
        const val V7_CONSENT = "sp.preload.V7.consent"
    }

    private val sp by lazy { PreferenceManager.getDefaultSharedPreferences(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sp.contains(OLD_V6_CONSENT)) {
            context?.let { clearAllData(it) }
            sp.edit().remove(OLD_V6_CONSENT).apply()
        }
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

        tool_bar.title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"

        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is StatePropertyList -> successState(it)
                is StateError -> errorState(it)
                is StateProperty -> updateProperty(it)
                is StateLoading -> savingProperty(it.propertyName, it.loading)
                is StateVersion -> showVersionPopup(it.version)
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

        if (BuildConfig.BUILD_TYPE == "release") {
            viewModel.fetchLatestVersion()
        }
        tool_bar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_clear_sp -> {
                    context?.let { clearAllData(it) }
                    sp.edit().remove(OLD_V6_CONSENT).apply()
                }
                R.id.action_add_prop -> {
                    viewModel.addDefaultProperties()
                }
                R.id.action_show_pref -> {
                    startActivity(Intent(requireActivity(), PreferencesActivity::class.java))
                }
                R.id.action_save_old_v6_consent -> {
                    val editor = sp.edit()
                    editor.putBoolean(OLD_V6_CONSENT, true)
                    val v6LocalState = JSONObject(oldV6Consent690)
                    v6LocalState.keys().forEach {
                        check { v6LocalState.getString(it) }?.let { v -> editor.putString(it, v) }
                        check { v6LocalState.getBoolean(it) }?.let { v -> editor.putBoolean(it, v) }
                        check { v6LocalState.getInt(it) }?.let { v -> editor.putInt(it, v) }
                    }
                    editor.apply()
                }
                R.id.action_save_old_v6_consent630 -> {
                    val editor = sp.edit()
                    editor.putBoolean(OLD_V6_CONSENT, true)
                    val v6LocalState = JSONObject(oldV6Consent630)
                    v6LocalState.keys().forEach {
                        check { v6LocalState.getString(it) }?.let { v -> editor.putString(it, v) }
                        check { v6LocalState.getBoolean(it) }?.let { v -> editor.putBoolean(it, v) }
                        check { v6LocalState.getInt(it) }?.let { v -> editor.putInt(it, v) }
                    }
                    editor.apply()
                }
                R.id.action_save_cons_726 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent726)
                    editor.putBoolean(V7_CONSENT, true)
                    v7LocalState.keys().forEach {
                        check { v7LocalState.getString(it) }?.let { v -> editor.putString(it, v) }
                        check { v7LocalState.getBoolean(it) }?.let { v -> editor.putBoolean(it, v) }
                        check { v7LocalState.getInt(it) }?.let { v -> editor.putInt(it, v) }
                    }
                    editor.apply()
                }
                R.id.action_save_cons_711 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent711)
                    editor.putBoolean(V7_CONSENT, true)
                    v7LocalState.keys().forEach {
                        check { v7LocalState.getString(it) }?.let { v -> editor.putString(it, v) }
                        check { v7LocalState.getBoolean(it) }?.let { v -> editor.putBoolean(it, v) }
                        check { v7LocalState.getInt(it) }?.let { v -> editor.putInt(it, v) }
                    }
                    editor.apply()
                }
                R.id.action_save_cons_742 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent742)
                    editor.putBoolean(V7_CONSENT, true)
                    v7LocalState.keys().forEach {
                        check { v7LocalState.getString(it) }?.let { v -> editor.putString(it, v) }
                        check { v7LocalState.getBoolean(it) }?.let { v -> editor.putBoolean(it, v) }
                        check { v7LocalState.getInt(it) }?.let { v -> editor.putInt(it, v) }
                    }
                    editor.apply()
                }
                R.id.action_expire_gdpr -> {
                    val editor = sp.edit()
                    editor.putString("sp.gdpr.key.expiration.date", "2022-10-27T17:15:56.953Z")
                    editor.apply()
                }
                R.id.action_expire_ccpa -> {
                    val editor = sp.edit()
                    editor.putString("sp.ccpa.key.expiration.date", "2022-10-27T17:15:56.953Z")
                    editor.apply()
                }
                R.id.action_drop_db -> {
                    showDropDbDialog()
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_prop_list, menu)
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

    private fun showDropDbDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Drop the entire DB")
            .setMessage("After the confirmation the app will be terminated.")
            .setPositiveButton("Confirm") { _, _ ->
                val database = requireContext().getDatabasePath("newmetaapp.db")
                database.safeDelete()
                System.exit(0)
            }
            .setNegativeButton("Cancel") { _, _ -> }
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

    private fun showVersionPopup(version: String) {
        tool_bar.setTitleTextColor(errorColor)
        tool_bar.title = "${tool_bar.title} -> $version"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Metaapp version ${BuildConfig.VERSION_NAME} out of date, new version $version is available.")
            .setPositiveButton("Update it") { _, _ ->
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.sourcepointmeta.metaapp")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.sourcepointmeta.metaapp")
                        )
                    )
                }
            }
            .setNegativeButton("Continue") { _, _ -> }
            .show()
    }

    private fun savingProperty(propertyName: String, showLoading: Boolean) {
        adapter.savingProperty(propertyName, showLoading)
    }

    private fun runDemo(property: Property) {
        val bundle = Bundle()
        bundle.putString("property_name", property.propertyName)
        val i = Intent(activity, DemoActivity::class.java)
        i.putExtras(bundle)
        startActivity(i)
    }

    private fun <E> check(block: () -> E): E? {
        return try {
            block.invoke()
        } catch (e: Exception) {
            null
        }
    }

    private fun File.safeDelete(): Boolean {
        return when (exists()) {
            true -> this.delete()
            else -> false
        }
    }
}

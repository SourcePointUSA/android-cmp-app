package com.sourcepointmeta.metaapp.ui.propertylist

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
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
import com.sourcepointmeta.metaapp.databinding.FragmentPropertyListBinding
import com.sourcepointmeta.metaapp.ui.BaseState.* //ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.ui.sp.PreferencesActivity
import com.sourcepointmeta.metaapp.util.* //ktlint-disable
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
    private lateinit var binding: FragmentPropertyListBinding

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
        binding = FragmentPropertyListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (clearDb) {
            viewModel.clearDB()
        }

        binding.toolBar.title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"

        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is StatePropertyList -> successState(it)
                is StateError -> errorState(it)
                is StateProperty -> updateProperty(it)
                is StateLoading -> savingProperty(it.propertyName, it.loading)
                is StateVersion -> showVersionPopup(it.version)
                else -> {
                    // instead of else we need to provide a case for each BaseState heirs
                }
            }
        }
        binding.propertyList.layoutManager = GridLayoutManager(context, 1)
        binding.propertyList.adapter = adapter
        binding.fab.setOnClickListener {
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
        itemTouchHelper.attachToRecyclerView(binding.propertyList)

        if (BuildConfig.BUILD_TYPE == "release") {
            viewModel.fetchLatestVersion()
        }
        binding.toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_clear_sp -> {
                    context?.let { PreferenceManager.getDefaultSharedPreferences(it).edit().clear().apply() }
                }
                R.id.action_add_prop -> {
                    viewModel.addDefaultProperties()
                }
                R.id.action_show_pref -> {
                    startActivity(Intent(requireActivity(), PreferencesActivity::class.java))
                }
                R.id.action_clear_all_sp_data -> {
                    context?.let { clearAllData(it) }
                }
                R.id.action_save_old_v6_consent -> {
                    val editor = sp.edit()
                    editor.putBoolean(OLD_V6_CONSENT, true)
                    val v6LocalState = JSONObject(oldV6Consent690)
                    editor.storeJson(v6LocalState)
                    editor.apply()
                }
                R.id.action_save_old_v6_consent630 -> {
                    val editor = sp.edit()
                    editor.putBoolean(OLD_V6_CONSENT, true)
                    val v6LocalState = JSONObject(oldV6Consent630)
                    editor.storeJson(v6LocalState)
                    editor.apply()
                }
                R.id.action_save_cons_726 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent726)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_cons_711 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent711)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_cons_742 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(v7Consent742)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_ccpa_752_AcceptAll -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(ccpaConsentedAll752)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_ccpa_752_rejectAll -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(ccpaRejectedAll752)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_ccpa_752_rejectSome -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(ccpaRejectedSome752)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_ccpa_752_AcceptAll_auth -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(ccpaAcceptAllAuthId752)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_ccpa_752_NO_GPP -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(ccpaNoGPP752)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_usnat_752 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(usnatAcceptedAll)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_usnat_appl_sec_changed_752 -> {
                    val editor = sp.edit()
                    val v7LocalState = JSONObject(usnatApplicableSectionChanged)
                    editor.putBoolean(V7_CONSENT, true)
                    editor.storeJson(v7LocalState)
                    editor.apply()
                }
                R.id.action_save_keep_data -> {
                    val editor = sp.edit()
                    editor.putBoolean(V7_CONSENT, true)
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

        val intent: Intent = requireActivity().intent
        val action: String? = intent.action
        val type: String? = intent.type

        if (Intent.ACTION_VIEW == action && type != null) {
            if ("application/json" == type) {
                intent.data
                    ?.let { requireContext().readFileContent(it) }
                    ?.string2Json()
                    ?.let {
                        val editor = sp.edit()
                        editor.storeJson(it)
                        editor.putBoolean(V7_CONSENT, true)
                        editor.apply()
                    }
            }
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
        binding.toolBar.setTitleTextColor(errorColor)
        binding.toolBar.title = "${binding.toolBar.title} -> $version"
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

    private fun SharedPreferences.Editor.storeJson(json: JSONObject) {
        this.putBoolean(V7_CONSENT, true)
        json.keys().forEach {
            check { json.getString(it) }?.let { v -> this.putString(it, v) }
            check { json.getBoolean(it) }?.let { v -> this.putBoolean(it, v) }
            check { json.getInt(it) }?.let { v -> this.putInt(it, v) }
        }
    }
}

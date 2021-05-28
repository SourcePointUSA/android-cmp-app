package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import kotlinx.android.synthetic.main.add_property_fragment.*

class AddPropertyFragment : Fragment() {

    companion object {
        fun newInstance() = PropertyListFragment()
    }

    private val adapter by lazy { PropertyAdapter() }

    private lateinit var viewModel: MainViewModel

    val messageOption = listOf("WebView", "App")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.add_property_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_item, messageOption)
        message_type_autocomplete.setAdapter(adapter)
        message_type_autocomplete.setText(messageOption.first())
        message_type_autocomplete.threshold = 1
    }
}
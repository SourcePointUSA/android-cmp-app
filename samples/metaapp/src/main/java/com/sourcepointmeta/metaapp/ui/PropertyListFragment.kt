package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import kotlinx.android.synthetic.main.fragment_property_list.*

class PropertyListFragment : Fragment() {

    companion object {
        fun newInstance() = PropertyListFragment()
    }

    private val adapter by lazy { PropertyAdapter() }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
        property_list.layoutManager = GridLayoutManager(context, 1)
        property_list.adapter = adapter
        adapter.addItems(
            List(15) {
                PropertyDTO(
                    campaignEnv = "stage",
                    propertyName = "mobile.demo.com",
                    accountId = it,
                    messageType = "Web-view"
                )
            }
        )
    }
}

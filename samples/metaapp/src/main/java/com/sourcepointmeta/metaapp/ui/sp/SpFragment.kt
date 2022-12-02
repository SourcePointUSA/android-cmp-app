package com.sourcepointmeta.metaapp.ui.sp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.BaseState
import kotlinx.android.synthetic.main.sp_fragment_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpFragment : Fragment() {

    companion object {
        @JvmStatic
        fun instance() =
            SpFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    private val viewModel by viewModel<SpViewModel>()
    private val adapter by lazy { SpPairsAdapter(context = requireContext()) }
    var spItemClickListener: ((key: String, value: String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sp_fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp_expandable_list.setAdapter(adapter)
        sp_expandable_list.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val key = adapter.getKeyByPosition(groupPosition)
            adapter.getValueByKey(groupPosition, childPosition)?.let { spItemClickListener?.invoke(key, it) }
            true
        }
        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchItems()
    }

    fun update() {
        if (isAdded) {
            viewModel.fetchItems()
        }
    }

    private fun stateHandler(state: BaseState) {
        (state as? BaseState.StateSpItemList)?.let { adapter.addAndClearElements(it.spList) }
    }
}

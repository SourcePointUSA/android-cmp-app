package com.sourcepointmeta.metaapp.ui.sp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.databinding.SpFragmentListBinding
import com.sourcepointmeta.metaapp.ui.BaseState
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
    private lateinit var binding: SpFragmentListBinding
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
    ): View {
        binding = SpFragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.spExpandableList.setAdapter(adapter)
        binding.spExpandableList.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
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

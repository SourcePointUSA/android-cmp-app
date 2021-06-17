package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.math.MathUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.DemoActionItem
import com.sourcepointmeta.metaapp.ui.component.DemoAdapter
import com.sourcepointmeta.metaapp.ui.component.GridDividerDecoration
import kotlinx.android.synthetic.main.demo_fragment_layout.*

class DemoFragment : Fragment() {

    internal val adapter by lazy { DemoAdapter() }

    companion object {
        private const val GRID_SPAN_COUNT_MIN = 1
        private const val GRID_SPAN_COUNT_MAX = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.demo_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridSpanCount = calculateGridSpanCount()
        adapter.addItems(
            listOf(
                DemoActionItem("GDPR Pm", 1),
                DemoActionItem("CCPA Pm", 2),
                DemoActionItem("Custom Consent", 3)
            )
        )
        demo_list.adapter = adapter
        demo_list.layoutManager = GridLayoutManager(context, gridSpanCount)
        demo_list.addItemDecoration(
            GridDividerDecoration(
                resources.getDimensionPixelSize(R.dimen.cat_toc_grid_divider_size),
                ContextCompat.getColor(requireContext(), R.color.white_50),
                gridSpanCount
            )
        )
    }

    private fun calculateGridSpanCount(): Int {
        val displayMetrics = resources.displayMetrics
        val displayWidth = displayMetrics.widthPixels
        val itemSize = resources.getDimensionPixelSize(R.dimen.cat_toc_item_size)
        val gridSpanCount = displayWidth / itemSize
        return MathUtils.clamp(
            gridSpanCount,
            GRID_SPAN_COUNT_MIN,
            GRID_SPAN_COUNT_MAX
        )
    }
}

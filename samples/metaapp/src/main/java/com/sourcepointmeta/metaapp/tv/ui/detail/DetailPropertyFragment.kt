package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepointmeta.metaapp.tv.ui.detail.model.PropDto

class DetailPropertyFragment : DetailsSupportFragment() {

    private val listener: (View) -> Unit = { view ->
        Toast.makeText(requireContext(), "Run", Toast.LENGTH_SHORT).show()
    }

    private val actionListener: (Action, PropDto) -> Unit = { a, i ->
        Toast.makeText(requireContext(), "Run Action", Toast.LENGTH_SHORT).show()
    }

//    private fun createPresenterSelector(p: PropDto) =
//        ClassPresenterSelector().apply {
//            // 1
//            addClassPresenter(
//                DetailsOverviewRow::class.java,
//                createDetailsOverviewRowPresenter(p, actionListener, listener, helper)
//            )
//        }

    private val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), "t_for_transition")
        }
    }

    val p = PropDto(
        name = "carmelo.iriti",
        accId = 22,
        timeout = 3000
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareEntranceTransition()

        adapter = ArrayObjectAdapter(createPresenterSelector(p, actionListener, listener, helper)).apply {
            add(DetailsOverviewRow(p).arrayObjectAdapter(Pair(1, "Run Demo")))
        }

        initEntranceTransition()
    }
}

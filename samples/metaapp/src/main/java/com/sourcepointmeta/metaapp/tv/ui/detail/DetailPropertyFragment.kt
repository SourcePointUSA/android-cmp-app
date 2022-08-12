package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.model.PropDto
import com.sourcepointmeta.metaapp.tv.ui.detail.presenter.PropertyDescriptionPresenter

class DetailPropertyFragment: DetailsSupportFragment() {

    private val listener : (View) -> Unit = { view ->
        Toast.makeText(requireContext(), "Run", Toast.LENGTH_SHORT).show()
    }

    private val actionListener : (Action, PropDto) -> Unit = { a, i ->
        Toast.makeText(requireContext(), "Run Action", Toast.LENGTH_SHORT).show()
    }

    private fun createDetailsOverviewRowPresenter(
        propDto: PropDto,
        actionHandler: (Action, PropDto) -> Unit,
        itemHandler : (View) -> Unit
    ): FullWidthDetailsOverviewRowPresenter =
        FullWidthDetailsOverviewRowPresenter(PropertyDescriptionPresenter(requireContext(), itemHandler))
            .setBackgroundColor(requireContext(), R.color.purple_500)
            .setTransition(requireActivity(), "action_prop")
            .setOnActionClickListener(propDto, actionHandler)
            .setTransitionListener(helper)

    private fun createPresenterSelector(p: PropDto) =
        ClassPresenterSelector().apply {
            // 1
            addClassPresenter(
                DetailsOverviewRow::class.java,
                createDetailsOverviewRowPresenter(p, actionListener, listener)
            )
        }

    private val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), DetailViewExampleFragment.TRANSITION_NAME)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareEntranceTransition()

        val p = PropDto(
            name = "carmelo.iriti",
            accId = 22,
            timeout = 3000
        )

        adapter = ArrayObjectAdapter(createPresenterSelector(p)).apply {
            add(DetailsOverviewRow(p).arrayObjectAdapter(Pair(1, "Run Demo")))
        }

        initEntranceTransition()

    }


}
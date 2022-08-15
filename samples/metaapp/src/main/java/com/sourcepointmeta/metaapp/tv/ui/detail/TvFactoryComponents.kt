package com.sourcepointmeta.metaapp.tv.ui.detail

import android.view.View
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.ui.detail.presenter.PropertyDescriptionPresenter

internal fun DetailPropertyFragment.createDetailsOverviewRowPresenter(
    propDto: Property,
    actionHandler: (Action, Property) -> Unit,
    itemHandler: (View) -> Unit,
    helper: FullWidthDetailsOverviewSharedElementHelper
): FullWidthDetailsOverviewRowPresenter =
    FullWidthDetailsOverviewRowPresenter(PropertyDescriptionPresenter(requireContext(), itemHandler))
        .setBackgroundColor(requireContext(), R.color.purple_500)
        .setTransition(requireActivity(), "action_prop")
        .setOnActionClickListener(propDto, actionHandler)
        .setTransitionListener(helper)

internal fun DetailPropertyFragment.createPresenterSelector(
    propDto: Property,
    actionHandler: (Action, Property) -> Unit,
    itemHandler: (View) -> Unit,
    helper: FullWidthDetailsOverviewSharedElementHelper
): ClassPresenterSelector =
    ClassPresenterSelector().apply {
        // 1
        addClassPresenter(
            DetailsOverviewRow::class.java,
            createDetailsOverviewRowPresenter(propDto, actionHandler, itemHandler, helper)
        )
    }

package com.sourcepointmeta.metaapp.tv.ui.detail

import android.view.View
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.ui.* // ktlint-disable
import com.sourcepointmeta.metaapp.tv.ui.detail.presenter.PropertyDescriptionPresenter

internal fun DetailPropertyFragment.createDetailsOverviewRowPresenter(
    propDto: Property,
    actionHandler: (Action, Property) -> Unit,
    itemHandler: (View, Int) -> Unit,
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
    itemHandler: (View, Int) -> Unit,
    helper: FullWidthDetailsOverviewSharedElementHelper
): ClassPresenterSelector =
    ClassPresenterSelector().apply {
        // 1
        addClassPresenter(
            DetailsOverviewRow::class.java,
            createDetailsOverviewRowPresenter(propDto, actionHandler, itemHandler, helper)
        )
    }

fun GuidedStepSupportFragment.createAction(
    id: Long,
    title: String,
    description: String,
    editable: Boolean = false,
    inputType: Int
): GuidedAction {
    return GuidedAction.Builder(activity)
        .id(id)
        .title(title)
        .description(description)
        .editable(editable)
        .editInputType(inputType)
        .inputType(inputType)
        .build()
}

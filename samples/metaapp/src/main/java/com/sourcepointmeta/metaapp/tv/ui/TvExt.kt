package com.sourcepointmeta.metaapp.tv.ui

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.* // ktlint-disable

fun FullWidthDetailsOverviewRowPresenter.setBackgroundColor(
    ctx: Context,
    @ColorRes color: Int
): FullWidthDetailsOverviewRowPresenter {
    backgroundColor =
        ContextCompat.getColor(ctx, color)
    return this
}

fun FullWidthDetailsOverviewRowPresenter.setTransition(
    activity: Activity,
    sharedElemName: String
): FullWidthDetailsOverviewRowPresenter {
    val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
    sharedElementHelper.setSharedElementEnterTransition(
        activity,
        sharedElemName
    )
    setListener(sharedElementHelper)
    isParticipatingEntranceTransition = true
    return this
}

fun FullWidthDetailsOverviewRowPresenter.setOnActionClickListener(
    propDto: PropertyTvDTO,
    actionListener: (Action, PropertyTvDTO) -> Unit
): FullWidthDetailsOverviewRowPresenter {
    onActionClickedListener = OnActionClickedListener {
        actionListener(it, propDto)
    }
    return this
}

fun FullWidthDetailsOverviewRowPresenter.setTransitionListener(
    helper: FullWidthDetailsOverviewSharedElementHelper,
): FullWidthDetailsOverviewRowPresenter {
    setListener(helper)
    isParticipatingEntranceTransition = false
    return this
}

fun DetailsOverviewRow.arrayObjectAdapter(vararg pairs: Pair<Long, String>): DetailsOverviewRow {
    val arr = ArrayObjectAdapter()
    pairs.fold(ArrayObjectAdapter()) { acc, elem -> acc.apply { add(elem) } }
    actionsAdapter =
        pairs.fold(ArrayObjectAdapter()) { acc, elem -> acc.apply { add(Action(elem.first, elem.second)) } }
    return this
}

fun DetailsSupportFragment.initEntranceTransition() {
    Handler(Looper.getMainLooper()).postDelayed({ startEntranceTransition() }, 500)
}

fun GuidedStepSupportFragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

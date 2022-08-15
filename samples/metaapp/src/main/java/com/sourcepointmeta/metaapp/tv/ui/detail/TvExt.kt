package com.sourcepointmeta.metaapp.tv.ui.detail

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.OnActionClickedListener
import com.sourcepointmeta.metaapp.data.localdatasource.Property

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
    propDto: Property,
    actionListener: (Action, Property) -> Unit
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
    actionsAdapter = pairs.fold(ArrayObjectAdapter()) { acc, elem -> acc.apply { add(Action(elem.first, elem.second)) } }
    return this
}

fun DetailsSupportFragment.initEntranceTransition() {
    Handler(Looper.getMainLooper()).postDelayed({ startEntranceTransition() }, 500)
}

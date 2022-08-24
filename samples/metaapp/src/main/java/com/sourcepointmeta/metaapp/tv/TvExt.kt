package com.sourcepointmeta.metaapp.tv

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.detail.DetailPropertyActivity
import com.sourcepointmeta.metaapp.tv.edit.PropertyField

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
    actionsAdapter =
        pairs.fold(ArrayObjectAdapter()) { acc, elem -> acc.apply { add(Action(elem.first, elem.second)) } }
    return this
}

fun DetailsSupportFragment.initEntranceTransition() {
    Handler(Looper.getMainLooper()).postDelayed({ startEntranceTransition() }, 500)
}

fun VerticalGridSupportFragment.initEntranceTransition() {
    Handler(Looper.getMainLooper()).postDelayed({ startEntranceTransition() }, 500)
}

fun GuidedStepSupportFragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun FrameLayout.addPlusBtn() {
    addView(LayoutInflater.from(context).inflate(R.layout.plus_btn, null))
}

fun Context.createNewProperty() {
    startActivity(Intent(this, DetailPropertyActivity::class.java))
}

fun Context.showPropertyDetail(propertyName: String) {
    val i = Intent(this, DetailPropertyActivity::class.java)
    i.flags = i.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
    i.putExtra(DetailPropertyActivity.PROPERTY_NAME_KEY, propertyName)
    startActivity(i)
}

fun Property.updateDTO(fieldType: PropertyField, newField: String?): Property {
    newField ?: return this
    return when (fieldType) {
        PropertyField.PROPERTY_NAME -> this.copy(propertyName = newField)
        PropertyField.MESSAGE_LANGUAGE -> this.copy(messageLanguage = newField)
        PropertyField.ACCOUNT_ID -> this.copy(accountId = newField.toLongOrNull() ?: 1)
        PropertyField.TIMEOUT -> this.copy(timeout = newField.toLongOrNull() ?: 3000L)
    }
}

fun Context.updatePropertyList() {
    val i = Intent().apply { action = MainActivityTV.REFRESH_ACTION }
    sendBroadcast(i)
}

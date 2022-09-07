package com.sourcepointmeta.metaapp.tv

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* // ktlint-disable
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.demo.DemoEventFragmentTv
import com.sourcepointmeta.metaapp.tv.detail.DetailPropertyActivity
import com.sourcepointmeta.metaapp.tv.edit.PropertyField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

fun Context.createNewProperty() {
    startActivity(Intent(this, DetailPropertyActivity::class.java))
}

fun Context.showPropertyDetail(propertyName: String) {
    val i = Intent(this, DetailPropertyActivity::class.java)
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
        PropertyField.GDPR_PM_ID -> this.copy(gdprPmId = newField.toLongOrNull() ?: 3000L)
    }
}

fun Context.updatePropertyList() {
    val i = Intent().apply { action = MainActivityTV.REFRESH_ACTION }
    sendBroadcast(i)
}

fun Activity.updatePropertyListAndClose() {
    updatePropertyList()
    finish()
}

fun Activity.updatePropertyListAndGoBack() {
    updatePropertyList()
    onBackPressed()
}

fun DemoEventFragmentTv.bounceEventAndSelectFirstElement() {
    MainScope().launch {
        withContext(Dispatchers.Default) {
            channel.send(0)
        }
        channel
            .asFlow()
            .debounce(300)
            .collect {
                setSelectedPosition(0)
            }
    }
}

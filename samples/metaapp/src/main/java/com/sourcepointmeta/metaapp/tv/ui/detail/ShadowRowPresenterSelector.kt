/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.sourcepointmeta.metaapp.tv.ui.detail

import androidx.leanback.widget.PresenterSelector
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.tv.ui.detail.model.CardRow

/**
 * This [PresenterSelector] will return a [ListRowPresenter] which has shadow support
 * enabled or not depending on [CardRow.useShadow] for a given row.
 */
class ShadowRowPresenterSelector : PresenterSelector() {
    private val mShadowEnabledRowPresenter = ListRowPresenter()
    private val mShadowDisabledRowPresenter = ListRowPresenter()

    init {
        mShadowEnabledRowPresenter.setNumRows(1)
        mShadowDisabledRowPresenter.shadowEnabled = false
    }

    override fun getPresenter(item: Any): Presenter {
        if (item !is CardListRow) return mShadowDisabledRowPresenter
        val row = item.cardRow
        return if (row!!.useShadow()) mShadowEnabledRowPresenter else mShadowDisabledRowPresenter
    }

    override fun getPresenters(): Array<Presenter> {
        return arrayOf(
            mShadowDisabledRowPresenter,
            mShadowEnabledRowPresenter
        )
    }
}
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

import android.content.Context
import androidx.leanback.widget.PresenterSelector
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.SingleLineCardPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.VideoCardViewPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.ImageCardViewPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.SideInfoCardPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.TextCardPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.IconCardPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.CharacterCardPresenter
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card
import java.lang.RuntimeException
import java.util.HashMap

/**
 * This PresenterSelector will decide what Presenter to use depending on a given card's type.
 */
class CardPresenterSelector(private val mContext: Context) : PresenterSelector() {
    private val presenters = HashMap<Card.Type, Presenter>()
    override fun getPresenter(item: Any): Presenter {
        if (item !is Card) throw RuntimeException(
            String.format(
                "The PresenterSelector only supports data items of type '%s'",
                Card::class.java.name
            )
        )
        val card = item
        var presenter = presenters[card.type]
        if (presenter == null) {
            when (card.type) {
                Card.Type.SINGLE_LINE -> presenter = SingleLineCardPresenter(mContext)
                Card.Type.VIDEO_GRID -> presenter = VideoCardViewPresenter(mContext, R.style.VideoGridCardTheme)
                Card.Type.MOVIE, Card.Type.MOVIE_BASE, Card.Type.MOVIE_COMPLETE, Card.Type.SQUARE_BIG, Card.Type.GRID_SQUARE, Card.Type.GAME -> {
                    var themeResId = R.style.MovieCardSimpleTheme
                    if (card.type == Card.Type.MOVIE_BASE) {
                        themeResId = R.style.MovieCardBasicTheme
                    } else if (card.type == Card.Type.MOVIE_COMPLETE) {
                        themeResId = R.style.MovieCardCompleteTheme
                    } else if (card.type == Card.Type.SQUARE_BIG) {
                        themeResId = R.style.SquareBigCardTheme
                    } else if (card.type == Card.Type.GRID_SQUARE) {
                        themeResId = R.style.GridCardTheme
                    } else if (card.type == Card.Type.GAME) {
                        themeResId = R.style.GameCardTheme
                    }
                    presenter = ImageCardViewPresenter(mContext, themeResId)
                }
                Card.Type.SIDE_INFO -> presenter = SideInfoCardPresenter(mContext)
                Card.Type.TEXT -> presenter = TextCardPresenter(mContext)
                Card.Type.ICON -> presenter = IconCardPresenter(mContext)
                Card.Type.CHARACTER -> presenter = CharacterCardPresenter(mContext)
                else -> presenter = ImageCardViewPresenter(mContext)
            }
        }
        presenters[card.type] = presenter
        return presenter
    }
}
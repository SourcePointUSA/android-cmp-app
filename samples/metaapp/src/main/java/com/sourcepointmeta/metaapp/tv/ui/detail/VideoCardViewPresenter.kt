/*
 * Copyright (C) 2016 The Android Open Source Project
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
 */
package com.sourcepointmeta.metaapp.tv.ui.detail

import android.content.Context
import com.sourcepointmeta.metaapp.tv.ui.detail.ImageCardViewPresenter
import androidx.leanback.widget.ImageCardView
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card
import com.sourcepointmeta.metaapp.tv.ui.detail.model.VideoCard

/**
 * Presenter for rendering video cards on the Vertical Grid fragment.
 */
class VideoCardViewPresenter : ImageCardViewPresenter {
    constructor(context: Context?, cardThemeResId: Int) : super(context, cardThemeResId) {}
    constructor(context: Context?) : super(context) {}



    override fun onBindViewHolder(card: Card?, cardView: ImageCardView) {
        super.onBindViewHolder(card, cardView)
        val videoCard = card as VideoCard?
        //        Glide.with(getContext())
//                .asBitmap()
//                .load(videoCard.getImageUrl())
//                .into(cardView.getMainImageView());
    }
}
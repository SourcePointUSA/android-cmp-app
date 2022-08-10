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
 */
package com.sourcepointmeta.metaapp.tv.ui.detail

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.leanback.widget.ImageCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card

/**
 * A very basic [ImageCardView] [androidx.leanback.widget.Presenter].You can
 * pass a custom style for the ImageCardView in the constructor. Use the default constructor to
 * create a Presenter with a default ImageCardView style.
 */
open class ImageCardViewPresenter @JvmOverloads constructor(
    context: Context?,
    cardThemeResId: Int = R.style.DefaultCardTheme
) : AbstractCardPresenter<ImageCardView>(
    ContextThemeWrapper(context, cardThemeResId)
) {
    override fun onCreateView(): ImageCardView {
        //        imageCardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "Clicked on ImageCardView", Toast.LENGTH_SHORT).show();
//            }
//        });
        return ImageCardView(context)
    }

    override fun onBindViewHolder(card: Card?, cardView: ImageCardView) {
        cardView.tag = card
        cardView.titleText = card!!.title
        cardView.contentText = card.description
        if (card.localImageResourceName != null) {
            val resourceId = context.resources
                .getIdentifier(
                    card.localImageResourceName,
                    "drawable", context.packageName
                )
            //            Glide.with(getContext())
//                    .asBitmap()
//                    .load(resourceId)
//                    .into(cardView.getMainImageView());
        }
    }
}
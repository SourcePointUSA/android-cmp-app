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
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
/**
 * This Presenter will display a card consisting of an image on the left side of the card followed
 * by text on the right side. The image and text have equal width. The text will work like a info
 * box, thus it will be hidden if the parent row is inactive. This behavior is unique to this card
 * and requires a special focus handler.
 */
class SideInfoCardPresenter(context: Context) : AbstractCardPresenter<BaseCardView>(
    context
) {
    override fun onCreateView(): BaseCardView {
        val cardView = BaseCardView(
            context, null,
            R.style.SideInfoCardStyle
        )
        cardView.isFocusable = true
        cardView.addView(LayoutInflater.from(context).inflate(R.layout.tv_side_info_card, null))
        return cardView
    }

    override fun onBindViewHolder(card: Card?, cardView: BaseCardView) {
        val imageView = cardView.findViewById<ImageView>(R.id.main_image)
        if (card!!.localImageResourceName != null) {
            val width = context.resources
                .getDimension(R.dimen.sidetext_image_card_width).toInt()
            val height = context.resources
                .getDimension(R.dimen.sidetext_image_card_height).toInt()
            val resourceId = context.resources
                .getIdentifier(
                    card.localImageResourceName,
                    "drawable", context.packageName
                )
            //            RequestOptions myOptions = new RequestOptions()
//                    .override(width, height);
//            Glide.with(getContext())
//                    .asBitmap()
//                    .load(resourceId)
//                    .apply(myOptions)
//                    .into(imageView);
        }
        val primaryText = cardView.findViewById<TextView>(R.id.primary_text)
        primaryText.text = card.title
        val secondaryText = cardView.findViewById<TextView>(R.id.secondary_text)
        secondaryText.text = card.description
        val extraText = cardView.findViewById<TextView>(R.id.extra_text)
        extraText.text = card.extraText
    }
}
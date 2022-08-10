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
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card

class CharacterCardView(context: Context?) : BaseCardView(context, null, R.style.CharacterCardStyle) {
    init {
        LayoutInflater.from(getContext()).inflate(R.layout.character_card, this)
        onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            val mainImage = findViewById<ImageView>(R.id.main_image)
            val container = findViewById<View>(R.id.container)
            if (hasFocus) {
                container.setBackgroundResource(R.drawable.character_focused)
                mainImage.setBackgroundResource(R.drawable.character_focused)
            } else {
                container.setBackgroundResource(R.drawable.character_not_focused_padding)
                mainImage.setBackgroundResource(R.drawable.character_not_focused)
            }
        }
        isFocusable = true
    }

    fun updateUi(card: Card) {
        val primaryText = findViewById<TextView>(R.id.primary_text)
        val imageView = findViewById<ImageView>(R.id.main_image)
        primaryText.text = card.title
        if (card.localImageResourceName != null) {
            val resourceId = card.getLocalImageResourceId(context)
            val bitmap = BitmapFactory
                .decodeResource(context.resources, resourceId)
            val drawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)
            drawable.setAntiAlias(true)
            //            drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            imageView.setImageDrawable(drawable)
        }
    }
}
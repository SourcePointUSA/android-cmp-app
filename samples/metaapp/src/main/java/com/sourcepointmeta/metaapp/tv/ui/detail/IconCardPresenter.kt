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

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.leanback.widget.ImageCardView
import com.sourcepointmeta.metaapp.R

/**
 * This Presenter will display cards which consists of a single icon which will be highlighted by a
 * surrounding circle when the card is focused. AndroidTV uses these cards for entering settings
 * menu.
 */
class IconCardPresenter(context: Context?) : ImageCardViewPresenter(context, R.style.IconCardTheme) {
    override fun onCreateView(): ImageCardView {
        val imageCardView = super.onCreateView()
        val image = imageCardView.mainImageView
        image.setBackgroundResource(R.drawable.icon_focused)
        image.background.alpha = 0
        imageCardView.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus -> animateIconBackground(image.background, hasFocus) }
        return imageCardView
    }

    private fun animateIconBackground(drawable: Drawable, hasFocus: Boolean) {
        if (hasFocus) {
            ObjectAnimator.ofInt(drawable, "alpha", 0, 255).setDuration(ANIMATION_DURATION.toLong()).start()
        } else {
            ObjectAnimator.ofInt(drawable, "alpha", 255, 0).setDuration(ANIMATION_DURATION.toLong()).start()
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 200
    }
}
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
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card

/**
 * This Presenter is used to display the characters card row in the DetailView examples.
 */
class CharacterCardPresenter(context: Context?) : AbstractCardPresenter<CharacterCardView>(
    context!!
) {
    override fun onCreateView(): CharacterCardView {
        return CharacterCardView(context)
    }

    fun onBindViewHolder(card: Card, cardView: CharacterCardView) {
        cardView.updateUi(card)
    }
}
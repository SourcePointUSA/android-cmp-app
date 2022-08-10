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

package com.sourcepointmeta.metaapp.tv.ui.detail;

import android.content.Context;
import com.sourcepointmeta.metaapp.tv.ui.detail.TextCardView;
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card;

/**
 * The Presenter displays a card consisting of text as a replacement for a big image. The footer is
 * also quite unique since it does contain two images rather than one or non.
 */
public class TextCardPresenter extends AbstractCardPresenter<TextCardView> {

    public TextCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected TextCardView onCreateView() {
        return new TextCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, TextCardView cardView) {
        cardView.updateUi(card);
    }

}

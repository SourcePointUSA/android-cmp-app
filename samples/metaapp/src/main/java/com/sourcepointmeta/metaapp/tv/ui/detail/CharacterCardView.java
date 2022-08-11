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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import com.sourcepointmeta.metaapp.R;
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card;
import androidx.leanback.widget.BaseCardView;

public class CharacterCardView extends BaseCardView {

    public CharacterCardView(Context context) {
        super(context, null, R.style.CharacterCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.character_card, this);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ImageView mainImage = findViewById(R.id.main_image);
                View container = findViewById(R.id.container);
                if (hasFocus) {
                    container.setBackgroundResource(R.drawable.character_focused);
                    mainImage.setBackgroundResource(R.drawable.character_focused);
                } else {
                    container.setBackgroundResource(R.drawable.character_not_focused_padding);
                    mainImage.setBackgroundResource(R.drawable.character_not_focused);
                }
            }
        });
        setFocusable(true);
    }

    public void updateUi(Card card) {
        TextView primaryText = findViewById(R.id.primary_text);
        final ImageView imageView = findViewById(R.id.main_image);

        primaryText.setText(card.getTitle());
        if (card.getLocalImageResourceName() != null) {
            int resourceId = card.getLocalImageResourceId(getContext());
            Bitmap bitmap = BitmapFactory
                    .decodeResource(getContext().getResources(), resourceId);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
            drawable.setAntiAlias(true);
//            drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            imageView.setImageDrawable(drawable);
        }
    }


}

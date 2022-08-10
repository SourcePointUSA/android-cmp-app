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
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.ResourceCache
import com.sourcepointmeta.metaapp.tv.ui.detail.model.DetailedCard
import javax.inject.Inject

/**
 * This presenter is used to render a [DetailedCard] in the [ ].
 */
class DetailsDescriptionPresenter @Inject constructor(private val mContext: Context) : Presenter() {
    private val mResourceCache = ResourceCache()
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.detail_view_content, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val primaryText = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.primary_text)
        val sndText1 = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.secondary_text_first)
        val sndText2 = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.secondary_text_second)
        val extraText = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.extra_text)
        val card = item as DetailedCard
        primaryText.text = card.title
        sndText1.text = card.description
        sndText2.text = card.year.toString() + ""
        extraText.text = card.text
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Nothing to do here.
    }
}
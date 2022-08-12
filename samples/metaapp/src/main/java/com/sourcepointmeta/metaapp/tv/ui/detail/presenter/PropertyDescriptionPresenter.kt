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
package com.sourcepointmeta.metaapp.tv.ui.detail.presenter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.detail.ResourceCache
import com.sourcepointmeta.metaapp.tv.ui.detail.model.PropDto

class PropertyDescriptionPresenter(
    private val mContext: Context,
    private val clickListener: (view: View)-> Unit
) : Presenter() {

    private val mResourceCache = ResourceCache()

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.detail_view_property, null)
//        view.setBackgroundColor(mContext.resources.getColor(R.color.red_200))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val propertyName = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.property)
        val accountId = mResourceCache.getViewById<TextView>(viewHolder.view, R.id.account_id)
        val propertyDetail = item as PropDto
        propertyName.text = propertyDetail.name
        accountId.text = propertyDetail.accId.toString()
        propertyName.setOnClickListener(clickListener)
        accountId.setOnClickListener(clickListener)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Nothing to do here.
    }
}
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
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.PropertyTvDTO
import com.sourcepointmeta.metaapp.tv.ui.edit.PropertyField
import kotlinx.android.synthetic.main.detail_view_property.view.*

class PropertyDescriptionPresenter(
    private val mContext: Context,
    private val clickListener: (view: View, Int) -> Unit
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.detail_view_property, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val propertyDetail = item as PropertyTvDTO

        viewHolder.view.apply {
            property?.let {
                it.text = propertyDetail.propertyName
                it.setOnClickListener { v -> clickListener(v, PropertyField.PROPERTY_NAME.ordinal) }
            }
            account_id?.let {
                it.text = propertyDetail.accountId.toString()
                it.setOnClickListener { v -> clickListener(v, PropertyField.ACCOUNT_ID.ordinal) }
            }
            mess_language?.let {
                it.text = propertyDetail.messageLanguage.toString()
                it.setOnClickListener { v -> clickListener(v, PropertyField.MESSAGE_LANGUAGE.ordinal) }
            }
            timeout?.let {
                it.text = propertyDetail.timeout.toString()
                it.setOnClickListener { v -> clickListener(v, PropertyField.TIMEOUT.ordinal) }
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Nothing to do here.
    }
}

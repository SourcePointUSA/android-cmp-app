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
package com.sourcepointmeta.metaapp.tv.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FakeActivity4Tests : FragmentActivity() {

    companion object {
        internal fun initTest(dataSource: LocalDataSource, activity: Activity) {
            MainScope().launch {
                val list = dataSource.fetchProperties().getOrNull() ?: emptyList()
                val p = if (list.isEmpty()) {
                    dataSource.storeOrUpdateProperty(defaultProperty).getOrNull()!!
                } else list.first()

                val i = Intent(activity, DetailPropertyActivity::class.java)
                i.flags = i.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
                i.putExtra(DetailPropertyActivity.PROPERTY_NAME_KEY, p.propertyName)
                activity.startActivity(i)
                activity.finish()
            }
        }
    }

    private val dataSource by inject<LocalDataSource>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_detail)
        initTest(dataSource, this)
    }
}

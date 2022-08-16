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

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.ui.edit.EditPropertyName

/**
 * Contains a [DetailsFragment] in order to display more details for a given card.
 */
class DetailPropertyActivity : FragmentActivity() {

    val fragment by lazy { DetailPropertyFragment() }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_detail)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commit()
        }

        fragment.navListener = {

            supportFragmentManager
                .beginTransaction()
                .addToBackStack("back_stack")
                .add(R.id.details_fragment, EditPropertyName("Property Name","Edit the property name", "modile.demop"), EditPropertyName::class.java.name)
                .commit()

        }
    }
}

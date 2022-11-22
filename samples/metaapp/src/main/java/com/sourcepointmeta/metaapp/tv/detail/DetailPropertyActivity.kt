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

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.tv.edit.EditProperty
import com.sourcepointmeta.metaapp.tv.edit.PropertyField
import com.sourcepointmeta.metaapp.tv.updatePropertyList

/**
 * Contains a [DetailsFragment] in order to display more details for a given card.
 */
class DetailPropertyActivity : FragmentActivity(), UpdateScreen {

    companion object {
        const val PROPERTY_NAME_KEY = "property_name"
    }

    val fragment by lazy { DetailPropertyFragment() }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_detail)

        val propertyName = intent.extras?.getString(PROPERTY_NAME_KEY)
        fragment.refreshPropertyNameArgument(propertyName)
        savedInstanceState ?: replaceFragment(R.id.details_fragment, fragment)
        update(propertyName)
    }

    override fun update(propertyName: String?) {
        fragment.updateProperty(propertyName)
        fragment.navListener = { propertyName_, type ->
            PropertyField.values().find { it.ordinal == type }?.let {
                val editFragment = EditProperty.instance(propertyName_, it.ordinal)
                    .apply { listener4Update = this@DetailPropertyActivity }
                addFragment(R.id.details_fragment, editFragment)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updatePropertyList()
    }
}

val defaultProperty = Property(
    accountId = 22,
    propertyName = "ott.test.suite",
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = emptyList(),
    statusCampaignSet = setOf(StatusCampaign("ott.test.suite", CampaignType.GDPR, true)),
    gdprPmId = 579231L,
    ccpaPmId = 1L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

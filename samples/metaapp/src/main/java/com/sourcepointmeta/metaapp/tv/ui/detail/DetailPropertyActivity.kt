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
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.addFragment
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.tv.ui.edit.EditProperty
import com.sourcepointmeta.metaapp.tv.ui.edit.PropertyField

/**
 * Contains a [DetailsFragment] in order to display more details for a given card.
 */
class DetailPropertyActivity : FragmentActivity() {

    companion object {
        const val PROPERTY_NAME_KEY = "property_name"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tv_activity_detail)

        val propertyName = intent.extras?.getString(PROPERTY_NAME_KEY) ?: throw RuntimeException("No property name")

        val fragment = DetailPropertyFragment.instance(propertyName)
        savedInstanceState ?: replaceFragment(R.id.details_fragment, fragment)
        fragment.navListener = { propertyName, type ->
            PropertyField.values().find { it.ordinal == type }?.let {
                val editFragment = EditProperty.instance(propertyName, it.ordinal)
                addFragment(R.id.details_fragment, editFragment)
            }
        }
    }
}

private val tp = listOf(
    MetaTargetingParam("test", CampaignType.GDPR, "key1", "val1"),
    MetaTargetingParam("test", CampaignType.GDPR, "key2", "val2"),
    MetaTargetingParam("test", CampaignType.GDPR, "key3", "val3"),
)

val defaultProperty = Property(
    accountId = 22,
    propertyName = "ott.test.suite",
    timeout = 3000,
    authId = null,
    messageLanguage = "ENGLISH",
    pmTab = "DEFAULT",
    is_staging = false,
    targetingParameters = tp,
    statusCampaignSet = setOf(StatusCampaign("ott.test.suite", CampaignType.GDPR, true)),
    messageType = "App",
    gdprPmId = 579231L,
    ccpaPmId = 1L,
    campaignsEnv = CampaignsEnv.PUBLIC
)

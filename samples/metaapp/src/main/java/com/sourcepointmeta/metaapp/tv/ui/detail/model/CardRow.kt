/*
 * Copyright (c) 2015 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */
package com.sourcepointmeta.metaapp.tv.ui.detail.model

import com.google.gson.annotations.SerializedName
import com.sourcepointmeta.metaapp.tv.ui.detail.model.CardRow

/**
 * This class represents a row of cards. In a real world application you might want to store more
 * data than in this example.
 */
class CardRow {
    @SerializedName("type")
    val type = TYPE_DEFAULT

    // Used to determine whether the row shall use shadows when displaying its cards or not.
    @SerializedName("shadow")
    private val mShadow = true

    @SerializedName("title")
    val title: String? = null

    @SerializedName("cards")
    val cards: List<Card>? = null
    fun useShadow(): Boolean {
        return mShadow
    }

    companion object {
        // default is a list of cards
        const val TYPE_DEFAULT = 0

        // section header
        const val TYPE_SECTION_HEADER = 1

        // divider
        const val TYPE_DIVIDER = 2
    }
}
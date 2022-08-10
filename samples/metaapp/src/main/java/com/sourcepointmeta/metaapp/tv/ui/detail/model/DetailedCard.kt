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
package com.sourcepointmeta.metaapp.tv.ui.detail.model

import android.content.Context
import com.google.gson.annotations.SerializedName

class DetailedCard {
    @SerializedName("title")
    val title = ""

    @SerializedName("description")
    val description = ""

    @SerializedName("text")
    val text = ""

    @SerializedName("localImageResource")
    val localImageResource: String? = null

    @SerializedName("price")
    val price: String? = null

    @SerializedName("characters")
    val characters: Array<Card>? = null

    @SerializedName("recommended")
    val recommended: Array<Card>? = null

    @SerializedName("year")
    val year = 0

    @SerializedName("trailerUrl")
    val trailerUrl: String? = null

    @SerializedName("videoUrl")
    val videoUrl: String? = null
    fun getLocalImageResourceId(context: Context): Int {
        return context.resources
            .getIdentifier(localImageResource, "drawable", context.packageName)
    }
}
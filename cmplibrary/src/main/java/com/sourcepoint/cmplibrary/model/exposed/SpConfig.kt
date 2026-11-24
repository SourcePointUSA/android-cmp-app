package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.mobile_core.network.responses.MessagesResponse.MessageMetaData.MessageSubCategory
import com.sourcepoint.mobile_core.network.responses.MessagesResponse.MessageMetaData.MessageSubCategory.CCPAOTT
import com.sourcepoint.mobile_core.network.responses.MessagesResponse.MessageMetaData.MessageSubCategory.NativeOTT
import com.sourcepoint.mobile_core.network.responses.MessagesResponse.MessageMetaData.MessageSubCategory.PrivacyManagerOTT

const val DEFAULT_TIMEOUT = 10000L // in ms

data class SpConfig(
    @JvmField val messageLanguage: MessageLanguage,
    @JvmField val messageTimeout: Long = DEFAULT_TIMEOUT,
    @JvmField val spGppConfig: SpGppConfig? = null,
    @JvmField val didomiConfig: DidomiConfig? = null,
)

enum class MessageType {
    MOBILE,
    OTT,
    LEGACY_OTT;

    internal companion object {
        internal fun fromMessageSubCategory(subCategory: MessageSubCategory) = when (subCategory) {
            PrivacyManagerOTT, CCPAOTT -> LEGACY_OTT
            NativeOTT -> OTT
            else -> MOBILE
        }
    }
}

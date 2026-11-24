@file:Suppress("unused")
package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.SpGppConfig

@SpDSL
class SpConfigDataBuilder {
    var messLanguage: MessageLanguage = MessageLanguage.ENGLISH
    var messageTimeout: Long = 5000
    var spGppConfig: SpGppConfig? = null
    var didomiConfig: DidomiConfig? = null
    var dismissMessageOnBackPress: Boolean = true

    fun addMessageLanguage(messLanguage: MessageLanguage) = apply {
        this.messLanguage = messLanguage
    }

    fun addMessageTimeout(messageTimeout: Long) = apply {
        this.messageTimeout = messageTimeout
    }

    fun addGppConfig(spGppConfig: SpGppConfig) = apply {
        this.spGppConfig = spGppConfig
    }

    fun addDidomiConfig(didomiConfig: DidomiConfig) = apply {
        this.didomiConfig = didomiConfig
    }

    fun dismissMessageOnBackPress(dismiss: Boolean) = apply {
        this.dismissMessageOnBackPress = dismiss
    }

    fun build() = SpConfig(
        messageLanguage = messLanguage,
        messageTimeout = messageTimeout,
        spGppConfig = spGppConfig,
        didomiConfig = didomiConfig,
    )
}

fun config(dsl: SpConfigDataBuilder.() -> Unit) = SpConfigDataBuilder().apply(dsl).build()

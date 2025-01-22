package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import okhttp3.Response

internal interface ResponseManager {
    fun parseMessagesResp(r: Response): MessagesResp

    companion object
}

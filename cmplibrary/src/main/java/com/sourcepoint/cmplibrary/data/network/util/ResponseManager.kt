package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import okhttp3.Response

internal interface ResponseManager {

    fun parseGetChoiceResp(r: Response, choice: ChoiceTypeParam): ChoiceResp
    fun parsePostGdprChoiceResp(r: Response): GdprCS
    fun parsePostCcpaChoiceResp(r: Response): CcpaCS
    fun parsePostUsNatChoiceResp(r: Response): USNatConsentData
    fun parseMessagesResp(r: Response): MessagesResp

    companion object
}

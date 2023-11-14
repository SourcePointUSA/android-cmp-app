package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import okhttp3.Response

/**
 * Component used to parse an OkHttp [Response] and extract a DTO
 */
internal interface ResponseManager {

    fun parseCustomConsentRes(r: Response): CustomConsentResp

    // Optimized
    fun parseMetaDataRes(r: Response): MetaDataResp
    fun parseConsentStatusResp(r: Response): ConsentStatusResp
    fun parseGetChoiceResp(r: Response, choice: ChoiceTypeParam): ChoiceResp
    fun parsePostGdprChoiceResp(r: Response): GdprCS
    fun parsePostCcpaChoiceResp(r: Response): CcpaCS
    fun parsePostUsNatChoiceResp(r: Response): USNatConsentData
    fun parsePvDataResp(r: Response): PvDataResp
    fun parseMessagesResp(r: Response): MessagesResp

    companion object
}

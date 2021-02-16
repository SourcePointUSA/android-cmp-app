package com.sourcepoint.cmplibrary.stub

import android.content.SharedPreferences
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.Either
import io.mockk.mockk

internal class MockService(
    private val getMessageLogic: ((messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val getNativeMessageLogic: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : Service {

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) {
        getMessageLogic?.invoke(messageReq, pSuccess, pError)
    }

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {
        getNativeMessageLogic?.invoke(messageReq, success, error)
    }

    override val preference: SharedPreferences = mockk()
    override fun clearInternalData() {}
    override fun clearAll() {}
    override fun saveTcData(deferredMap: DeferredMap) {}
    override fun saveAuthId(value: String) {}
    override fun saveEuConsent(value: String) {}
    override fun saveMetaData(value: String) {}
    override fun saveConsentUuid(value: String) {}
    override fun saveAppliedLegislation(value: String) {}
    override fun saveCcpa(ccpa: Ccpa) {}
    override fun saveGdpr(gdpr: Gdpr) {}

    override fun getTcData(): DeferredMap = DeferredMap(false)
    override fun getAuthId(): String = ""
    override fun getEuConsent(): String = ""
    override fun getMetaData(): String = ""
    override fun getConsentUuid(): String = ""
    override fun getAppliedLegislation(): String = ""
    override fun getCcpa(): Either<Ccpa> = Either.Left(RuntimeException())
    override fun getGdpr(): Either<Gdpr> = Either.Left(RuntimeException())

    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {
    }

    override var gdprApplies: Boolean = false
    override var ccpaApplies: Boolean = false
}

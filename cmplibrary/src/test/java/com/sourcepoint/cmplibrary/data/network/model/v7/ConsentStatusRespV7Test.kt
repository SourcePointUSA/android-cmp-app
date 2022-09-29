package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Test

class ConsentStatusRespV7Test{

    @Test
    fun `GIVEN a consent_status body resp RETURN a ConsentStatusRespV7`() {
        val json = "v7/consent_status_with_auth_id.json".file2String()
        val obj = converter.decodeFromString<ConsentStatusRespV7>(json)
        converter.encodeToString(obj)
    }

    @Test
    fun `GIVEN a consent_status without authId body resp RETURN a Right(ConsentStatusResp)`() {
        val json = "v7/consent_status_without_auth_id.json".file2String()
        val obj = converter.decodeFromString<ConsentStatusRespV7>(json)
        converter.encodeToString(obj)
    }
}
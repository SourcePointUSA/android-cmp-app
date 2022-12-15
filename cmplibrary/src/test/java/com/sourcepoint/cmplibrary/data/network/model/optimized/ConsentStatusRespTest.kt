package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Test

class ConsentStatusRespTest {

    @Test
    fun `GIVEN a consent_status body resp RETURN a ConsentStatusRespV7`() {
        val json = "v7/consent_status_with_auth_id.json".file2String()
        val obj = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)
        JsonConverter.converter.encodeToString(obj)
    }

    @Test
    fun `GIVEN a consent_status without authId body resp RETURN a Right(ConsentStatusResp)`() {
        val json = "v7/consent_status_without_auth_id.json".file2String()
        val obj = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)
        JsonConverter.converter.encodeToString(obj)
    }
}

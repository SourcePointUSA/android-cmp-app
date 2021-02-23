package com.sourcepoint.cmplibrary.core.layout.model

import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class NativeMessageDtoExtTest {

    @Test
    fun `GIVEN a NativeMessage json RETURN it as a Dto`() {
        val json = "native_message_resp.json".file2String()
        val map = JSONObject(json).toTreeMap().getMap("msgJSON")!!
        val native = map.toNativeMessageDto()
        native.assertNotNull()
    }
}

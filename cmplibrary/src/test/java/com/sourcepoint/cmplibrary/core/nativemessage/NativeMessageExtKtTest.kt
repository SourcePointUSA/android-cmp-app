package com.sourcepoint.cmplibrary.core.nativemessage

import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class NativeMessageExtKtTest{

    @Test
    fun `GIVEN a native message PARSE it to DTO`(){
        val jsonObj = JSONObject("nativemessage/native_ccpa_message.json".file2String())
        val res = jsonObj.toNativeMessageDTO()
        res.assertNotNull()
    }
}
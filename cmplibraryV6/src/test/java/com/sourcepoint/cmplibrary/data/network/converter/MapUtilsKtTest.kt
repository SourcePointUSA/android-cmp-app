package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import org.junit.Test
import kotlin.collections.Map as ConsentMap

class MapUtilsKtTest {
    @Test
    fun `GIVEN a message response parse a gdpr obj`() {
        val messResp: JSONObject = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
        val gdprMap: ConsentMap<String, Any?>? = messResp.toTreeMap().getMap("gdpr")
        gdprMap?.toGDPR()
    }
}

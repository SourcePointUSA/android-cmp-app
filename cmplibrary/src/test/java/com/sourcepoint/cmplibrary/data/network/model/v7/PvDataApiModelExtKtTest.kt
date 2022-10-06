package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class PvDataApiModelExtKtTest {

    @Test
    fun `GIVEN a PvData resp RETURN the parsed obj`() {
        val metadataJson = "v7/pv_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<PvDataResp>(metadataJson)
        JsonConverter.converter.encodeToJsonElement(metadata)
    }
}

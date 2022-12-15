package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class MetaDataRespTest {

    @Test
    fun `GIVEN a MetaData RETURN the parsed obj`() {
        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)
        JsonConverter.converter.encodeToJsonElement(metadata)
    }
}

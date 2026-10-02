package com.sourcepoint.cmplibrary.network

import com.sourcepoint.mobile_core.network.json
import com.sourcepoint.mobile_core.network.responses.MessagesResponse
import kotlinx.serialization.decodeFromString
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageMetadataTest {

    @Test
    fun `message metadata maps the prtnUUID response field`() {
        val metadata = json.decodeFromString<MessagesResponse.MessageMetaData>(
            """
            {
              "categoryId": 1,
              "subCategoryId": 5,
              "messageId": 1429939,
              "prtnUUID": "partition-uuid"
            }
            """.trimIndent()
        )

        assertEquals("partition-uuid", metadata.messagePartitionUUID)
    }
}

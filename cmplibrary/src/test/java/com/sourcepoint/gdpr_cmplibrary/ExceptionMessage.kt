package com.sourcepoint.gdpr_cmplibrary

import org.apache.maven.wagon.ConnectionException
import org.json.JSONException
import org.junit.Assert
import org.junit.Test

class ExceptionMessageTest {

    @Test
    fun `GIVEN an ResourceNotFoundException VERIFY the generated message`() {

        val originalException = JSONException("test_message")
        val sut = ResourceNotFoundException(originalException, "test_description")

        val expected = """
            {
                "code" : "resource_not_found",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${ResourceNotFoundException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an NoInternetConnectionException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = NoInternetConnectionException(originalException, "test_description")

        val expected = """
            {
                "code" : "no_internet_connection",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${NoInternetConnectionException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an InternalServerException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = InternalServerException(originalException, "test_description")

        val expected = """
            {
                "code" : "internal_server_error",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${InternalServerException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an WebViewException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = WebViewException(originalException, "test_description")

        val expected = """
            {
                "code" : "web_view_error",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${WebViewException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an UrlLoadingException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = UrlLoadingException(originalException, "test_description")

        val expected = """
            {
                "code" : "url_loading_error",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${UrlLoadingException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an InvalidEventPayloadException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = InvalidEventPayloadException(originalException, "test_description")

        val expected = """
            {
                "code" : "invalid_event_payload",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${InvalidEventPayloadException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an InvalidResponseException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = InvalidResponseException(originalException, "test_description")

        val expected = """
            {
                "code" : "invalid_response",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${InvalidResponseException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an InvalidLocalDataException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = InvalidLocalDataException(originalException, "test_description")

        val expected = """
            {
                "code" : "invalid_local_data",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${InvalidLocalDataException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

    @Test
    fun `GIVEN an ConnectionTimeoutException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val sut = ConnectionTimeoutException(originalException, "test_description")

        val expected = """
            {
                "code" : "connection_timeout",
                "message" : "test_message",
                "description" : "test_description",
                "class_type" : "${ConnectionTimeoutException::class.java}"
            }
            """.trimIndent()

        Assert.assertEquals(expected, sut.toString())
    }

}
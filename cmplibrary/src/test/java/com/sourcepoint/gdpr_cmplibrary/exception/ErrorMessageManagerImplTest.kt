package com.sourcepoint.gdpr_cmplibrary.exception

import org.apache.maven.wagon.ConnectionException
import org.json.JSONException
import org.junit.Assert
import org.junit.Test

class ErrorMessageManagerImplTest {

    private val accountId = 22
    private val propertyHref = "http://dev.local"
    private val propertyId = 100

    internal val sut by lazy { createErrorManager(accountId, propertyId, propertyHref) }

    @Test
    fun `GIVEN a ResourceNotFoundException VERIFY the generated message`() {

        val originalException = JSONException("test_message")
        val exception = ResourceNotFoundException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.RESOURCE_NOT_FOUND}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN a NoInternetConnectionException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val exception = NoInternetConnectionException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.NO_INTERNET_CONNECTION}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN an InternalServerException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InternalServerException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.INTERNAL_SERVER_ERROR}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN a WebViewException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = WebViewException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.WEB_VIEW_ERROR}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN a UrlLoadingException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = UrlLoadingException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.URL_LOADING_ERROR}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN an InvalidEventPayloadException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidEventPayloadException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.INVALID_EVENT_PAYLOAD}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN an InvalidResponseException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidResponseException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.INVALID_RESPONSE}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN an InvalidLocalDataException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidLocalDataException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.INVALID_LOCAL_DATA}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

    @Test
    fun `GIVEN a ConnectionTimeoutException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = ConnectionTimeoutException(originalException, "test_description")

        val expected = """
            {
                "code" : "${ExceptionCodes.CONNECTION_TIMEOUT}",
                "accountId": "$accountId",
                "propertyHref": "$propertyHref",
                "propertyId": "$propertyId",
                "description": "test_description"
            }
        """.trimIndent()

        Assert.assertEquals(expected, sut.build(exception))
    }

}
package com.sourcepoint.gdpr_cmplibrary.exception

import com.sourcepoint.gdpr_cmplibrary.assertEquals
import org.apache.maven.wagon.ConnectionException
import org.json.JSONException
import org.junit.Test

class ErrorMessageManagerImplTest{

    private val accountId = 22
    private val propertyHref = "http://dev.local"
    private val propertyId = 100
    private val client = ClientInfo(
        clientVersion = "5.X.X",
        deviceFamily = "android",
        osVersion = "30"
    )

    internal val sut by lazy { createErrorManager(accountId, propertyId, propertyHref, client) }

    @Test
    fun `GIVEN a ResourceNotFoundException VERIFY the generated message`() {

        val originalException = JSONException("test_message")
        val exception = ResourceNotFoundException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.RESOURCE_NOT_FOUND.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a NoInternetConnectionException VERIFY the generated message`() {

        val originalException = ConnectionException("test_message")
        val exception = NoInternetConnectionException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.NO_INTERNET_CONNECTION.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InternalServerException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InternalServerException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INTERNAL_SERVER_ERROR.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a WebViewException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = WebViewException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.WEB_VIEW_ERROR.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a UrlLoadingException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = UrlLoadingException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.URL_LOADING_ERROR.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidEventPayloadException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidEventPayloadException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_EVENT_PAYLOAD.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidOnActionEventPayloadException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidOnActionEventPayloadException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_ON_ACTION_EVENT_PAYLOAD.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an RenderingAppException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = RenderingAppException(originalException, "test_description", "test_code")

        val expected = """
            {
                "code" : "test_code",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidResponseException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidResponseException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_RESPONSE_WEB_MESSAGE.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidResponseNativeMessageException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidResponseNativeMessageException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_RESPONSE_NATIVE_MESSAGE.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidResponseConsentException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidResponseConsentException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_RESPONSE_CONSENT.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidResponseCustomConsent VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidResponseCustomConsent(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_RESPONSE_CUSTOM_CONSENT.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN an InvalidLocalDataException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidLocalDataException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_LOCAL_DATA.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a ConnectionTimeoutException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = ConnectionTimeoutException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.CONNECTION_TIMEOUT.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a GenericNetworkRequestException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = GenericNetworkRequestException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.GENERIC_NETWORK_REQUEST.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a GenericSDKException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = GenericSDKException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.GENERIC_SDK_ERROR.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a InvalidRequestException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = InvalidRequestException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.INVALID_REQUEST_ERROR.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

    @Test
    fun `GIVEN a UnableToLoadJSReceiverException VERIFY the generated message`() {

        val originalException = RuntimeException("test_message")
        val exception = UnableToLoadJSReceiverException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.UNABLE_TO_LOAD_JS_RECEIVER.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "propertyId" : "$propertyId",
                "description" : "test_description"
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }

}
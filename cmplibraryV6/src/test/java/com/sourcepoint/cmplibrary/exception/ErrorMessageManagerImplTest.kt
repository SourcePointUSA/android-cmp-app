package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.json.JSONException
import org.junit.Before
import org.junit.Test

class ErrorMessageManagerImplTest {

    private val accountId = 22
    private val propertyHref = "http://dev.local"
    private val client = ClientInfo(
        clientVersion = "5.X.X",
        deviceFamily = "android",
        osVersion = "30"
    )

    private val gdprTemplate = GDPRCampaign(CampaignEnv.STAGE, arrayOf(TargetingParam("location", "EU")))

    private val gdpr = SpCampaign(
        legislation = Legislation.GDPR,
        environment = CampaignEnv.STAGE,
        targetingParams = arrayOf(TargetingParam("location", "EU"))
    )

    private val spConfig = SpConfig(
        accountId = 22,
        propertyName = "http://dev.local",
        campaigns = arrayOf(gdpr)
    )

    @MockK
    internal lateinit var campaignManager: CampaignManager

    internal val sut by lazy { createErrorManager(campaignManager, client) }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        every { campaignManager.getAppliedCampaign() }.returns(Either.Right(Pair(Legislation.GDPR, gdprTemplate)))
        every { campaignManager.spCampaignConfig }.returns(spConfig)
    }

    @Test
    fun `GIVEN a ResourceNotFoundException VERIFY the generated message`() {

        val originalException = JSONException("test_message")
        val exception = ResourceNotFoundException(originalException, "test_description")

        val expected = """
            {
                "code" : "${CodeList.RESOURCE_NOT_FOUND.code}",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
        val exception = RenderingAppException(cause = originalException, description = "test_description", pCode = "test_code")

        val expected = """
            {
                "code" : "test_code",
                "accountId" : "$accountId",
                "propertyHref" : "$propertyHref",
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
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
                "description" : "test_description",
                "clientVersion" : "${client.clientVersion}",
                "OSVersion" : "${client.osVersion}",
                "deviceFamily" : "${client.deviceFamily}",
                "legislation" : "${Legislation.GDPR.name}"
            }
        """.trimIndent()

        sut.build(exception).assertEquals(expected)
    }
}

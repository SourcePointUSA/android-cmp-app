package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.os.CountDownTimer;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibBuilderTest {

    private GDPRConsentLib.OnConsentUIReadyCallback onConsentUIReady;
    private GDPRConsentLib.OnConsentReadyCallback onConsentReady;
    private GDPRConsentLib.OnConsentUIFinishedCallback onConsentUIFinished;
    private GDPRConsentLib.OnErrorCallback onError;

    private PropertyConfig defaultConfig = new PropertyConfig(22, 1234,"mobile.demo", "1234");

    private ConsentLibBuilder spyBuilder(PropertyConfig config){
        ConsentLibBuilder spy =  spy(new ConsentLibBuilder(config.accountId, config.propertyName, config.propertyId, config.pmId, activityMock));
        // mocking dependencies...
        doReturn(storeClientMock).when(spy).getStoreClient();
        doReturn(sourcePointClientMock).when(spy).getSourcePointClient();
        doReturn(consentLibMock).when(spy).getConsentLib();
        doReturn(timerMock).when(spy).getTimer(any());
        return spy;
    }

    private final static String expectedEmptyTargetingParamsString = "{}";

    @Mock
    Activity activityMock;

    @Mock
    StoreClient storeClientMock;

    @Mock
    SourcePointClient sourcePointClientMock;

    @Mock
    GDPRConsentLib consentLibMock;

    @Mock
    CountDownTimer timerMock;

    ConsentLibBuilder defaultBuilder;

    @Before
    public void initConsentLibBuilder() {
        initMocks(this);
        defaultBuilder = spyBuilder(defaultConfig);
        onConsentUIReady = c -> {
        };
        onConsentReady = c -> {
        };
        onConsentUIFinished = c -> {
        };
        onError = c -> {
        };

    }

    private Field getDeclaredFieldAccess(String fieldName) throws Exception {
        Field member = ConsentLibBuilder.class.getDeclaredField(fieldName);
        member.setAccessible(true);
        return member;
    }

    @Test
    public void ConsentLibBuilder(){
        ConsentLibBuilder builder = new ConsentLibBuilder(defaultConfig.accountId, defaultConfig.propertyName, defaultConfig.propertyId, defaultConfig.pmId, activityMock);
        assertEquals(builder.propertyConfig.accountId, defaultConfig.accountId);
        assertEquals(builder.propertyConfig.propertyId, defaultConfig.propertyId);
        assertEquals(builder.propertyConfig.propertyName, defaultConfig.propertyName);
        assertEquals(builder.propertyConfig.pmId, defaultConfig.pmId);
        assertTrue(builder.shouldCleanConsentOnError);
        assertEquals(builder.messageTimeOut, builder.DEFAULT_MESSAGE_TIMEOUT);
    }

    @Test
    public void build() {
        assertEquals(defaultBuilder.build(), consentLibMock);
    }

    @Test
    public void setMessageTimeout() {
        defaultBuilder.setMessageTimeOut(20000);
        assertEquals(20000, defaultBuilder.messageTimeOut);
        defaultBuilder.setMessageTimeOut(0);
        assertEquals(0, defaultBuilder.messageTimeOut);
        //TODO: validate timeout as positive int on setMessageTimeOut()
        defaultBuilder.setMessageTimeOut(-1000);
        assertEquals(-1000, defaultBuilder.messageTimeOut);
    }

    @Test
    public void setOnConsentReady() {
        defaultBuilder.setOnConsentReady(onConsentReady);
        assertEquals(onConsentReady, defaultBuilder.onConsentReady);
    }

    @Test
    public void setOnConsentUIReady() {
        defaultBuilder.setOnConsentUIReady(onConsentUIReady);
        assertEquals(onConsentUIReady, defaultBuilder.onConsentUIReady);
    }

    @Test
    public void setOnConsentUIFinished() {
        defaultBuilder.setOnConsentUIFinished(onConsentUIFinished);
        assertEquals(onConsentUIFinished, defaultBuilder.onConsentUIFinished);
    }

    @Test
    public void setOnError() {
        defaultBuilder.setOnError(onError);
        assertEquals(onError, defaultBuilder.onError);
    }

    @Test
    public void setStage() {
        defaultBuilder.setStagingCampaign(true);
        assertTrue(defaultBuilder.stagingCampaign);
        defaultBuilder.setStagingCampaign(false);
        assertFalse(defaultBuilder.stagingCampaign);
    }

    @Test
    public void setIsOTT() {
        defaultBuilder.setIsOTT(true);
        assertTrue(defaultBuilder.isOTT);
        defaultBuilder.setIsOTT(false);
        assertFalse(defaultBuilder.isOTT);
    }

    @Test
    public void setShouldCleanConsentOnError() {
        defaultBuilder.setShouldCleanConsentOnError(false);
        assertFalse(defaultBuilder.shouldCleanConsentOnError);
        defaultBuilder.setShouldCleanConsentOnError(true);
        assertTrue(defaultBuilder.shouldCleanConsentOnError);
    }

    @Test
    public void setAuthId() {
        defaultBuilder.setAuthId("authId");
        assertEquals("authId", defaultBuilder.authId);
    }

    @Test
    public void setTargetingParamString() throws Exception {
        defaultBuilder.setTargetingParam("key", "stringValue");

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "stringValue");

        assertEquals(jsonObject.toString(), localMember.get(defaultBuilder).toString());

    }

    @Test
    public void targetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);
        defaultBuilder.setTargetingParam(key, stringValue);

        assertEquals(jsonObject.toString(), defaultBuilder.getTargetingParamsString());
    }

    @Test
    public void setTargetingParamIntValue() throws Exception {

        String key = "key";
        int intValue = 2;
        defaultBuilder.setTargetingParam(key, intValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, intValue);

        assertEquals(jsonObject.toString(), localMember.get(defaultBuilder).toString());
    }

    @Test
    public void setTargetingParamStringValue() throws Exception {

        String key = "key";
        String stringValue = "stringValue";
        defaultBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString(), localMember.get(defaultBuilder).toString());
    }

    @Test
    public void nullCheckPMReadyCallback(){
        assertNotNull(defaultBuilder.pmReady);
    }

    @Test
    public void nullCheckPMFinishedCallback(){
        assertNotNull(defaultBuilder.pmFinished);
    }

    @Test
    public void nullCheckMessageReadyCallback(){
        assertNotNull(defaultBuilder.messageReady);
    }

    @Test
    public void nullCheckMessageFinishedCallback(){
        assertNotNull(defaultBuilder.messageFinished);
    }

    @Test
    public void nullCheckOnActionCallback(){
        assertNotNull(defaultBuilder.onAction);
    }
}
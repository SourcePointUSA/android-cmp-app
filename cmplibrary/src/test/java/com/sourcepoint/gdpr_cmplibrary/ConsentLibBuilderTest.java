package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibBuilderTest {

    private GDPRConsentLib.OnConsentUIReadyCallback onConsentUIReady;
    private GDPRConsentLib.OnConsentReadyCallback onConsentReady;
    private GDPRConsentLib.OnConsentUIFinishedCallback onConsentUIFinished;
    private GDPRConsentLib.OnErrorCallback onError;

    class PropertyConfig {
        private final int accountId;
        private final String property           ;
        private final int propertyId;
        private final String pmId;

        PropertyConfig(Integer accountId, String property, Integer propertyId , String pmId) {
            this.accountId = accountId;
            this.property = property;
            this.propertyId = propertyId;
            this.pmId = pmId;
        }
    }

    private PropertyConfig defaultConfig = new PropertyConfig(22, "mobile.demo", 1234, "1234");

    private ConsentLibBuilder spyBuilder(PropertyConfig config){
        ConsentLibBuilder spy =  spy(new ConsentLibBuilder(config.accountId, config.property, config.propertyId, config.pmId, activityMock));
        // mocking dependencies...
        when(spy.getConsetLib()).thenReturn(consentLibMock);
        doAnswer((i) -> spy.sourcePointClient = sourcePointClientMock).when(spy).setSourcePointClient();
        doAnswer((i) -> spy.storeClient = storeClientMock).when(spy).setStoreClient();
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

    ConsentLibBuilder defaultBuilder;

    @Before
    public void initConsentLibBuilder() {
        defaultBuilder = spyBuilder(defaultConfig);
        doNothing().when(defaultBuilder).setStoreClient();
        doNothing().when(defaultBuilder).setSourcePointClient();
        doReturn(consentLibMock).when(defaultBuilder).getConsetLib();
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
        ConsentLibBuilder builder = new ConsentLibBuilder(defaultConfig.accountId, defaultConfig.property, defaultConfig.propertyId, defaultConfig.pmId, activityMock);
        assertEquals(builder.accountId, defaultConfig.accountId);
        assertEquals(builder.propertyId, defaultConfig.propertyId);
        assertEquals(builder.property, defaultConfig.property);
        assertEquals(builder.pmId, defaultConfig.pmId);
        assertEquals(builder.activity, activityMock);
        assertFalse(builder.staging);
        assertTrue(builder.shouldCleanConsentOnError);
        assertEquals(builder.messageTimeOut, builder.DEFAULT_MESSAGE_TIMEOUT);
    }

    @Test
    public void build() {
        assertEquals(defaultBuilder.build(), consentLibMock);
        assertEquals(defaultBuilder.storeClient, storeClientMock);
        assertEquals(defaultBuilder.sourcePointClient, sourcePointClientMock);
        assertEquals(defaultBuilder.targetingParamsString, expectedEmptyTargetingParamsString);
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
        boolean stage = true;
        defaultBuilder.setStagingCampaign(stage);
        assertEquals(stage, defaultBuilder.stagingCampaign);
    }

    @Test
    public void setInternalStage() {
        boolean stage = true;
        defaultBuilder.setInternalStage(stage);
        assertEquals(stage, defaultBuilder.staging);
    }

    @Test
    public void setShouldCleanConsentOnError() {
        boolean shouldCleanConsentOnError = false;
        defaultBuilder.setShouldCleanConsentOnError(shouldCleanConsentOnError);
        assertEquals(shouldCleanConsentOnError, defaultBuilder.shouldCleanConsentOnError);
    }

    @Test
    public void setAuthId() {
        String authId = "authId";
        defaultBuilder.setAuthId(authId);
        assertEquals(authId, defaultBuilder.authId);
    }

    @Test
    public void setTargetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";
        defaultBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString(), localMember.get(defaultBuilder).toString());

    }

    @Test
    public void targetingParamStringIsEncoded() throws Exception {
        String key = "key";
        String stringValue = "stringValue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);
        defaultBuilder.setTargetingParam(key, stringValue);

        Method method = ConsentLibBuilder.class.getDeclaredMethod("setTargetingParamsString");
        method.setAccessible(true);
        method.invoke(defaultBuilder);

        assertEquals(jsonObject.toString(), defaultBuilder.targetingParamsString);
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
    public void setDebugLevel() {
        defaultBuilder.setDebugLevel(GDPRConsentLib.DebugLevel.DEBUG);
        assertEquals(GDPRConsentLib.DebugLevel.DEBUG, defaultBuilder.debugLevel);
    }
}
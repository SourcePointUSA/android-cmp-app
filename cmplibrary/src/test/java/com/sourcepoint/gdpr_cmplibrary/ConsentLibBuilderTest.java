package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    Activity activityMock;

    @Mock
    StoreClient storeClientMock;

    @Mock
    SourcePointClient sourcePointClientMock;

    @Mock
    GDPRConsentLib consentLibMock;

    ConsentLibBuilder builder;

    @Before
    public void initConsentLibBuilder() {
        builder = spy(new ConsentLibBuilder(22, "mobile.demo", 1234, "1234", activityMock));
        doNothing().when(builder).setStoreClient();
        doNothing().when(builder).setSourcePointClient();
        doReturn(consentLibMock).when(builder).getConsetLib();
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
        assertEquals(builder.accountId, 22);
        assertEquals(builder.propertyId, 1234);
        assertEquals(builder.property, "mobile.demo");
        assertEquals(builder.pmId, "1234");
        assertEquals(builder.activity, activityMock);
        assertFalse(builder.staging);
        assertTrue(builder.shouldCleanConsentOnError);
    }

    @Test
    public void build() {
    }

    @Test
    public void setMessageTimeout() {
        builder.setMessageTimeOut(20000);
        assertEquals(20000, builder.defaultMessageTimeOut);
        builder.setMessageTimeOut(0);
        assertEquals(0, builder.defaultMessageTimeOut);
        //TODO: validate timeout as positive int on setMessageTimeOut()
        builder.setMessageTimeOut(-1000);
        assertEquals(-1000, builder.defaultMessageTimeOut);
    }

    @Test
    public void setOnConsentReady() {
        builder.setOnConsentReady(onConsentReady);
        assertEquals(onConsentReady, builder.onConsentReady);
    }

    @Test
    public void setOnConsentUIReady() {
        builder.setOnConsentUIReady(onConsentUIReady);
        assertEquals(onConsentUIReady, builder.onConsentUIReady);
    }

    @Test
    public void setOnConsentUIFinished() {
        builder.setOnConsentUIFinished(onConsentUIFinished);
        assertEquals(onConsentUIFinished, builder.onConsentUIFinished);
    }

    @Test
    public void setOnError() {
        builder.setOnError(onError);
        assertEquals(onError, builder.onError);
    }

    @Test
    public void setStage() {
        boolean stage = true;
        builder.setStagingCampaign(stage);
        assertEquals(stage, builder.stagingCampaign);
    }

    @Test
    public void setInternalStage() {
        boolean stage = true;
        builder.setInternalStage(stage);
        assertEquals(stage, builder.staging);
    }

    @Test
    public void setShouldCleanConsentOnError() {
        boolean shouldCleanConsentOnError = false;
        builder.setShouldCleanConsentOnError(shouldCleanConsentOnError);
        assertEquals(shouldCleanConsentOnError, builder.shouldCleanConsentOnError);
    }

    @Test
    public void setAuthId() {
        String authId = "authId";
        builder.setAuthId(authId);
        assertEquals(authId, builder.authId);
    }

    @Test
    public void setTargetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";
        builder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString(), localMember.get(builder).toString());

    }

    @Test
    public void targetingParamStringIsEncoded() throws Exception {
        String key = "key";
        String stringValue = "stringValue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);
        builder.setTargetingParam(key, stringValue);

        Method method = ConsentLibBuilder.class.getDeclaredMethod("setTargetingParamsString");
        method.setAccessible(true);
        method.invoke(builder);

        assertEquals(jsonObject.toString(), builder.targetingParamsString);
    }

    @Test
    public void setTargetingParamIntValue() throws Exception {

        String key = "key";
        int intValue = 2;
        builder.setTargetingParam(key, intValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, intValue);

        assertEquals(jsonObject.toString(), localMember.get(builder).toString());
    }

    @Test
    public void setTargetingParamStringValue() throws Exception {

        String key = "key";
        String stringValue = "stringValue";
        builder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString(), localMember.get(builder).toString());
    }

    @Test
    public void setDebugLevel() {
        builder.setDebugLevel(GDPRConsentLib.DebugLevel.DEBUG);
        assertEquals(GDPRConsentLib.DebugLevel.DEBUG, builder.debugLevel);
    }
}
package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class ConsentLibBuilderTest {

    private ConsentLibBuilder consentLibBuilder;
    private GDPRConsentLib.OnConsentUIReadyCallback onConsentUIReady;
    private GDPRConsentLib.OnConsentReadyCallback onConsentReady;
    private GDPRConsentLib.OnConsentUIFinishedCallback onConsentUIFinished;
    private GDPRConsentLib.OnErrorCallback onError;

    @Before
    public void initConsentLibBuilder() throws Exception {
        StoreClient storeClient = new StoreClient(PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application.getApplicationContext()));

        consentLibBuilder = new ConsentLibBuilder(123, "example.com", 321, "abcd", mock(Activity.class), storeClient);
        onConsentUIReady = c -> {   };
        onConsentReady = c -> {   };
        onConsentUIFinished = c -> { };
        onError = c ->{ };
    }

    public Field getDeclaredFieldAccess(String fieldName) throws Exception {
        Field member = ConsentLibBuilder.class.getDeclaredField(fieldName);
        member.setAccessible(true);
        return member;
    }

    @Test
    public void build() {
        GDPRConsentLib consentLib = consentLibBuilder.build();
        assertNotNull(consentLib);
    }

    @Test
    public void setMessageTimeout() {
        consentLibBuilder.setMessageTimeOut(20000);
        assertEquals(20000, consentLibBuilder.defaultMessageTimeOut);
    }

    @Test
    public void setOnConsentReady() {
        consentLibBuilder.setOnConsentReady(onConsentReady);
        assertEquals(onConsentReady, consentLibBuilder.onConsentReady);
    }

    @Test
    public void setOnConsentUIReady() {
        consentLibBuilder.setOnConsentUIReady(onConsentUIReady);
        assertEquals(onConsentUIReady, consentLibBuilder.onConsentUIReady);
    }

    @Test
    public void setOnConsentUIFinished(){
        consentLibBuilder.setOnConsentUIFinished(onConsentUIFinished);
        assertEquals(onConsentUIFinished , consentLibBuilder.onConsentUIFinished);
    }

    @Test
    public void setOnError() {
        consentLibBuilder.setOnError(onError);
        assertEquals(onError, consentLibBuilder.onError);
    }

    @Test
    public void setStage() {
        boolean stage = true;
        consentLibBuilder.setStagingCampaign(stage);
        assertEquals(stage, consentLibBuilder.stagingCampaign);
    }

    @Test
    public void setInternalStage() {
        boolean stage = true;
        consentLibBuilder.setInternalStage(stage);
        assertEquals(stage, consentLibBuilder.staging);
    }

    @Test
    public void setShouldCleanConsentOnError() {
        boolean shouldCleanConsentOnError = false;
        consentLibBuilder.setShouldCleanConsentOnError(shouldCleanConsentOnError);
        assertEquals(shouldCleanConsentOnError, consentLibBuilder.shouldCleanConsentOnError);
    }

    @Test
    public void setAuthId() {
        String authId = "authId";
        consentLibBuilder.setAuthId(authId);
        assertEquals(authId, consentLibBuilder.authId);
    }

    @Test
    public void setTargetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());

    }

    @Test
    public void targetingParamStringIsEncoded() throws Exception {
        String key = "key";
        String stringValue = "stringValue";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);
        consentLibBuilder.setTargetingParam(key, stringValue);

        Method method = ConsentLibBuilder.class.getDeclaredMethod("setTargetingParamsString");
        method.setAccessible(true);
        method.invoke(consentLibBuilder);

        assertEquals(jsonObject.toString(), consentLibBuilder.targetingParamsString);
    }

    @Test
    public void setTargetingParamIntValue() throws Exception{

        String key = "key";
        int intValue = 2;
        consentLibBuilder.setTargetingParam(key, intValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, intValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void setTargetingParamStringValue() throws Exception{

        String key = "key";
        String stringValue = "stringValue";
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void setDebugLevel(){
        consentLibBuilder.setDebugLevel(GDPRConsentLib.DebugLevel.DEBUG);
        assertEquals(GDPRConsentLib.DebugLevel.DEBUG , consentLibBuilder.debugLevel);
    }
}
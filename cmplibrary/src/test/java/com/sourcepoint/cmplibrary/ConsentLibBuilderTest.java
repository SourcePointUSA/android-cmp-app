package com.sourcepoint.cmplibrary;

import android.app.Activity;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ConsentLibBuilderTest {


    private ConsentLibBuilder consentLibBuilder;
    private ConsentLib.Callback callback;

    @Before
    public void initConsentLibBuilder() {
        consentLibBuilder = new ConsentLibBuilder(808, "cybage.com", mock(Activity.class));
        callback = new ConsentLib.Callback() {
            @Override
            public void run(ConsentLib c) {

            }
        };
    }

    public Field getDeclaredFieldAccess(String fieldName) throws Exception{
        Field member = ConsentLibBuilder.class.getDeclaredField(fieldName);
        member.setAccessible(true);
        return member;
    }

    @Test(expected = ConsentLibException.class)
    public void testBuild() throws ConsentLibException {
        ConsentLib consentLib = mock(ConsentLib.class);
        assertEquals(consentLib, consentLibBuilder.build());
    }

    @Test
    public void testSetMessageTimeout() {
        consentLibBuilder.setMessageTimeOut(20000);
        assertEquals(20000, consentLibBuilder.defaultMessageTimeOut);
    }

    @Test
    public void testSetPage() {
        String page = "page";
        consentLibBuilder.setPage(page);
        assertEquals(page, consentLibBuilder.page);
    }

    @Test
    public void testSetViewGroup() {
        ViewGroup v = mock(ViewGroup.class);
        consentLibBuilder.setViewGroup(v);
        assertEquals(v, consentLibBuilder.viewGroup);
    }

    @Test
    public void testSetOnMessageChoiceSelect() {
        consentLibBuilder.setOnMessageChoiceSelect(callback);
        assertEquals(callback, consentLibBuilder.onMessageChoiceSelect);
    }

    @Test
    public void testSetOnInteractionComplete() {
        consentLibBuilder.setOnInteractionComplete(callback);
        assertEquals(callback, consentLibBuilder.onInteractionComplete);
    }

    @Test
    public void testSetOnMessageReady() {
        consentLibBuilder.setOnMessageReady(callback);
        assertEquals(callback, consentLibBuilder.onMessageReady);
    }

    @Test
    public void testSetOnErrorOccurred() {
        consentLibBuilder.setOnErrorOccurred(callback);
        assertEquals(callback, consentLibBuilder.onErrorOccurred);
    }

    @Test
    public void testSetStage() {
        boolean stage = true;
        consentLibBuilder.setStage(stage);
        assertEquals(stage, consentLibBuilder.stagingCampaign);
    }

    @Test
    public void testSetInternalStage() {
        boolean stage = true;
        consentLibBuilder.setInternalStage(stage);
        assertEquals(stage, consentLibBuilder.staging);
    }

    @Test
    public void testSetInAppMessagePageUrl() {
        String inAppMessageUrl = "inAppMessageUrl";
        consentLibBuilder.setInAppMessagePageUrl(inAppMessageUrl);
        assertEquals(inAppMessageUrl, consentLibBuilder.msgDomain);
    }

    @Test
    public void testSetMmsDomain() {
        String mmsDomain = "mmsDomain";
        consentLibBuilder.setMmsDomain(mmsDomain);
        assertEquals(mmsDomain, consentLibBuilder.mmsDomain);
    }

    @Test
    public void testSetCmpDomain() {
        String cmpDomain = "cmpDomain";
        consentLibBuilder.setCmpDomain(cmpDomain);
        assertEquals(cmpDomain, consentLibBuilder.cmpDomain);
    }

    @Test
    public void testSetTargetingParamString() throws Exception {
        String key = "key";
        String stringValue = "stringValue";
        EncodedParam targetingParamsString;
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());

        targetingParamsString = new EncodedParam("targetingParams", jsonObject.toString());
        Method method = ConsentLibBuilder.class.getDeclaredMethod("setTargetingParamsString");
        method.setAccessible(true);
        method.invoke(consentLibBuilder);
        assertEquals(targetingParamsString.toString(), consentLibBuilder.targetingParamsString.toString());

    }

    @Test
    public void testSetTargetingParamIntValue() throws Exception{

        String key = "key";
        int intValue = 2;
        consentLibBuilder.setTargetingParam(key, intValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, intValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void testSetTargetingParamStringValue() throws Exception{

        String key = "key";
        String stringValue = "stringValue";
        consentLibBuilder.setTargetingParam(key, stringValue);

        Field localMember = getDeclaredFieldAccess("targetingParams");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, stringValue);

        assertEquals(jsonObject.toString() , localMember.get(consentLibBuilder).toString());
    }

    @Test
    public void testSetDebugLevel(){
        consentLibBuilder.setDebugLevel(ConsentLib.DebugLevel.DEBUG);
        assertEquals(ConsentLib.DebugLevel.DEBUG , consentLibBuilder.debugLevel);
    }
}
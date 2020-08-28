package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class ConsentActionTest {

    @Mock
    JSONObject pmSaveAndExitVariablesMock;

    String choiceIdMock = "122";

    int actionTypeCodeMock = 1;

    boolean requestFromPmMock = true;

    //ActionType validation is not being tested as this class does not have this responsibility
    @Test
    public void ConsentActionDefaults() throws JSONException {
        ConsentAction consentAction = new ConsentAction(actionTypeCodeMock, choiceIdMock, null, requestFromPmMock, pmSaveAndExitVariablesMock);
        assertEquals(consentAction.actionType.code, actionTypeCodeMock);
        assertEquals(choiceIdMock, choiceIdMock);
        assertEquals(pmSaveAndExitVariablesMock, pmSaveAndExitVariablesMock);

        Map pubData = new HashMap();
        pubData.put("foo", "bar");
        consentAction.setPubData(pubData);

        assertEquals("bar", consentAction.getPubData().get("foo"));
    }

    @Test
    public void ConsentActionNulls(){
        ConsentAction consentAction = new ConsentAction(actionTypeCodeMock, null, null, requestFromPmMock, null);
        assertEquals(consentAction.actionType.code, actionTypeCodeMock);
        assertNull(consentAction.choiceId);
        assertNull(consentAction.pmSaveAndExitVariables);
        assertNull(consentAction.getPubData());
    }
}

package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

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
    public void ConsentActionDefaults(){
        ConsentAction consentAction = new ConsentAction(actionTypeCodeMock, choiceIdMock, privacyManagerId, requestFromPmMock, pmSaveAndExitVariablesMock);
        assertEquals(consentAction.actionType.code, actionTypeCodeMock);
        assertEquals(choiceIdMock, choiceIdMock);
        assertEquals(pmSaveAndExitVariablesMock, pmSaveAndExitVariablesMock);
    }

    @Test
    public void ConsentActionNulls(){
        ConsentAction consentAction = new ConsentAction(actionTypeCodeMock, null, privacyManagerId, requestFromPmMock, null);
        assertEquals(consentAction.actionType.code, actionTypeCodeMock);
        assertNull(consentAction.choiceId);
        assertNull(consentAction.pmSaveAndExitVariables);
    }
}

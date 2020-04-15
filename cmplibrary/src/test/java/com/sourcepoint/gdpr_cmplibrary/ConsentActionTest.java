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

    int actionTypeMock = 1;

    boolean requestFromPmMock = true;

    //ActionType validation is not being tested as this class does not have this responsibility
    @Test
    public void ConsentActionDefaults(){
        ConsentAction consentAction = new ConsentAction(actionTypeMock, choiceIdMock, requestFromPmMock, pmSaveAndExitVariablesMock);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertEquals(choiceIdMock, choiceIdMock);
        assertEquals(pmSaveAndExitVariablesMock, pmSaveAndExitVariablesMock);
    }

    @Test
    public void ConsentActionNulls(){
        ConsentAction consentAction = new ConsentAction(actionTypeMock, null, requestFromPmMock, null);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertNull(consentAction.choiceId);
        assertNull(consentAction.pmSaveAndExitVariables);
    }
}

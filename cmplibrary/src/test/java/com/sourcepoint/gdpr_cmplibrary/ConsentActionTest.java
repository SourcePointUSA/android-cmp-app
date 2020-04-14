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

    Integer choiceIdMock = 122;

    int actionTypeMock = 1;

    //ActionType validation is not being tested as this class does not have this responsibility
    @Test
    public void ConsentActionDefaults(){
        ConsentAction consentAction = new ConsentAction(actionTypeMock, choiceIdMock, pmSaveAndExitVariablesMock);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertEquals(choiceIdMock, choiceIdMock);
        assertEquals(pmSaveAndExitVariablesMock, pmSaveAndExitVariablesMock);
    }

    @Test
    public void ConsentActionNulls(){
        ConsentAction consentAction = new ConsentAction(actionTypeMock, null, null);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertNull(consentAction.choiceId);
        assertNull(consentAction.pmSaveAndExitVariables);
    }
}

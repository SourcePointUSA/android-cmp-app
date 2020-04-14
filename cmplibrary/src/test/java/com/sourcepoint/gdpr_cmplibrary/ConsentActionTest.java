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

    @Test
    public void ConsentActionDefaults(){
        ConsentAction consentAction = new ConsentAction(ActionTypes.ACCEPT_ALL, choiceIdMock, pmSaveAndExitVariablesMock);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertEquals(java.util.Optional.ofNullable(consentAction.choiceId), choiceIdMock);
        assertEquals(java.util.Optional.ofNullable(consentAction.pmSaveAndExitVariables), pmSaveAndExitVariablesMock);
    }

    @Test
    public void ConsentActionNulls(){
        ConsentAction consentAction = new ConsentAction(ActionTypes.ACCEPT_ALL, null, null);
        assertEquals(consentAction.actionType, actionTypeMock);
        assertNull(consentAction.choiceId);
        assertNull(consentAction.pmSaveAndExitVariables);
    }
}

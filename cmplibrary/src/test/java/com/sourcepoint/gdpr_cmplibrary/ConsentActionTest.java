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

    @Test
    public void acceptAll(){
        ConsentAction acceptAll_NULL_NULL = new ConsentAction(ActionTypes.ACCEPT_ALL, null, null);
        assertEquals(acceptAll_NULL_NULL.actionType, ActionTypes.ACCEPT_ALL);
        assertNull(acceptAll_NULL_NULL.choiceId);
        assertNull(acceptAll_NULL_NULL.pmSaveAndExitVariables);

        ConsentAction acceptAll_X_NULL = new ConsentAction(ActionTypes.ACCEPT_ALL, choiceIdMock, null);
        assertEquals(acceptAll_X_NULL.actionType, ActionTypes.ACCEPT_ALL);
        assertEquals(java.util.Optional.ofNullable(acceptAll_X_NULL.choiceId), choiceIdMock);
        assertNull(acceptAll_X_NULL.pmSaveAndExitVariables);

        ConsentAction acceptAll_NULL_X = new ConsentAction(ActionTypes.ACCEPT_ALL, choiceIdMock, pmSaveAndExitVariablesMock);
        assertEquals(acceptAll_NULL_X.actionType, ActionTypes.ACCEPT_ALL);
        assertEquals(java.util.Optional.ofNullable(acceptAll_X_NULL.choiceId), choiceIdMock);
        assertEquals(acceptAll_NULL_X.pmSaveAndExitVariables, pmSaveAndExitVariablesMock);
    }

    @Test
    public void rejectAll(){

    }

    @Test
    public void showOptions(){

    }

    @Test
    public void saveAndExit(){

    }

    @Test
    public void msgCancel(){

    }

    @Test
    public void pmDismiss(){

    }

    @Test
    public void invalidActionType(){

    }
}

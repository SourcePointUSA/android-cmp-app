package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

public class ConsentAction {
    public final int actionType;
    public final Integer choiceId;
    public final JSONObject pmSaveAndExitVariables;

    ConsentAction(int actionType, Integer choiceId, JSONObject pmSaveAndExitVariables){
        this.actionType = actionType;
        this.choiceId = choiceId;
        this.pmSaveAndExitVariables = pmSaveAndExitVariables;
    }
}

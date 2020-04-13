package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

public class ActionType {
    private final int actionType;
    private final Integer choiceId;
    private final JSONObject pmSaveAndExitVariables;

    ActionType(int actionType, Integer choiceId, JSONObject pmSaveAndExitVariables){
        this.actionType = actionType;
        this.choiceId = choiceId;
        this.pmSaveAndExitVariables = pmSaveAndExitVariables;
    }

    ActionType(int actionType, Integer choiceId){
        this.actionType = actionType;
        this.choiceId = choiceId;
        this.pmSaveAndExitVariables = null;
    }
}

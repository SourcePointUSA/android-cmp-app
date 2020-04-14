package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

public class ConsentAction {


    public final int actionType;
    public final String choiceId;
    public final boolean requestFromPm;
    public final JSONObject saveAndExitVariables;

    ConsentAction(int actionType, String choiceId, boolean requestFromPm, JSONObject saveAndExitVariables){
        this.actionType = actionType;
        this.choiceId = choiceId;
        this.requestFromPm = requestFromPm;
        this.saveAndExitVariables = saveAndExitVariables;
    }
}

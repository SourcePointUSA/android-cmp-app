package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

public class ConsentAction {


    public final ActionTypes actionType;
    public final String choiceId;
    public final boolean requestFromPm;
    public final JSONObject pmSaveAndExitVariables;

    ConsentAction(int actionType, String choiceId, boolean requestFromPm, JSONObject pmSaveAndExitVariables) {
        this.actionType = ActionTypes.valueOf(actionType);
        this.choiceId = choiceId;
        this.requestFromPm = requestFromPm;
        this.pmSaveAndExitVariables = pmSaveAndExitVariables;
    }

    public static ConsentAction getEmptyDismissAction(boolean isPmOn){
        if (isPmOn){
            return new ConsentAction(2, "", true, new JSONObject());
        }else {
            return new ConsentAction(15, "", false, new JSONObject());
        }
    }

}

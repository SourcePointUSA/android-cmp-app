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
        return isPmOn ? new ConsentAction(ActionTypes.PM_DISMISS.code, "", true, new JSONObject())
                : new ConsentAction(ActionTypes.MSG_CANCEL.code, "", false, new JSONObject());
    }

}

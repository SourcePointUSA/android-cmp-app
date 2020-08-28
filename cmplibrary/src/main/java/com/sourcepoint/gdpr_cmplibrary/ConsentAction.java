package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

import java.util.Map;

public class ConsentAction {


    public final ActionTypes actionType;
    public final String choiceId;
    public final String privacyManagerId;
    public final boolean requestFromPm;
    public final JSONObject pmSaveAndExitVariables;
    private Map pubData = null;

    ConsentAction(int actionType, String choiceId, String privacyManagerId, boolean requestFromPm, JSONObject pmSaveAndExitVariables) {
        this.actionType = ActionTypes.valueOf(actionType);
        this.choiceId = choiceId;
        this.privacyManagerId = privacyManagerId;
        this.requestFromPm = requestFromPm;
        this.pmSaveAndExitVariables = pmSaveAndExitVariables;
    }

    ConsentAction(int actionType, String choiceId, boolean requestFromPm, JSONObject pmSaveAndExitVariables) {
        this(actionType, choiceId, null, requestFromPm, pmSaveAndExitVariables);
    }

    public static ConsentAction getEmptyDismissAction(boolean isPmOn) {
        return isPmOn ? new ConsentAction(ActionTypes.PM_DISMISS.code, "", "", true, new JSONObject())
                : new ConsentAction(ActionTypes.MSG_CANCEL.code, "", "", false, new JSONObject());
    }

    public void setPubData(Map pubData){
        this.pubData = pubData;
    }

    public JSONObject getPubData(){
        return pubData != null ? new JSONObject(pubData) : null;
    }

}

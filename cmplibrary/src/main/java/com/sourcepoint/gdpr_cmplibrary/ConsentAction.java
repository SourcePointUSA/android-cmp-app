package com.sourcepoint.gdpr_cmplibrary;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsentAction {


    public final ActionTypes actionType;
    public final String choiceId;
    public final String privacyManagerId;
    public final String pmTab;
    public final boolean requestFromPm;
    public final JSONObject pmSaveAndExitVariables;
    private Map pubData = new HashMap();

    ConsentAction(int actionType, String choiceId, String privacyManagerId, String defaultPmTab,boolean requestFromPm, JSONObject pmSaveAndExitVariables) {
        this.actionType = ActionTypes.valueOf(actionType);
        this.choiceId = choiceId;
        this.privacyManagerId = privacyManagerId;
        this.pmTab = defaultPmTab;
        this.requestFromPm = requestFromPm;
        this.pmSaveAndExitVariables = pmSaveAndExitVariables;
    }

    ConsentAction(int actionType, String choiceId, boolean requestFromPm, JSONObject pmSaveAndExitVariables) {
        this(actionType, choiceId, null, null,requestFromPm, pmSaveAndExitVariables);
    }

    public void setPubData(Map pubData){
        this.pubData = pubData;
    }

    public JSONObject getPubData(){
        return new JSONObject(pubData);
    }

}

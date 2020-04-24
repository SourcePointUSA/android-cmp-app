package com.sourcepoint.gdpr_cmplibrary;

public enum ActionTypes {

    SHOW_OPTIONS(12),
    REJECT_ALL(13),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    SAVE_AND_EXIT(1),
    PM_DISMISS(2);

    public final int code;

    ActionTypes(int actionTypeCode){
        this.code = actionTypeCode;
    }

    public static ActionTypes fromCode(int code) {
       for (ActionTypes actionTypes : values()){
           if(actionTypes.code == code) return actionTypes;
       }
        /* TODO: throw ConsentLib exception instead. But it will change some public method
            signatures a demand for a minor version bump up of the SDK. */
       return null;
    }
}
package com.sourcepoint.gdpr_cmplibrary;

import android.os.Handler;
import android.os.Looper;

public class UIThreadHandler extends Handler {

    private boolean isEnabled;

    public UIThreadHandler(Looper l){
        super(l);
        enable();
    }

    public void disable(){
        isEnabled = false;
    }

    public void enable(){
        isEnabled = true;
    }

    public boolean postIfEnabled(Runnable r){
        return isEnabled ? super.post(r) : false;
    }

}

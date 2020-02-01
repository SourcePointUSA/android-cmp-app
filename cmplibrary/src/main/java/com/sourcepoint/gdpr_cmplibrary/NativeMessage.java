package com.sourcepoint.gdpr_cmplibrary;

import android.app.NativeActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gdpr_cmplibrary.R;

/**
 * TODO: document your custom view class.
 */
public class NativeMessage extends RelativeLayout {

    public Button cancel;
    public Button acceptAll;
    public Button rejectAll;
    public Button showOptions;

    public TextView body;
    public TextView title;

    public NativeMessage(Context context) {
        super(context);
        init();
    }

    public NativeMessage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NativeMessage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.sample_native_message, this);
        acceptAll = findViewById(R.id.AcceptAll);
        acceptAll.setVisibility(View.INVISIBLE);
        rejectAll = findViewById(R.id.RejectAll);
        rejectAll.setVisibility(View.INVISIBLE);
        showOptions = findViewById(R.id.ShowOptions);
        showOptions.setVisibility(View.INVISIBLE);
        cancel = findViewById(R.id.Cancel);
        cancel.setVisibility(View.INVISIBLE);
        title = findViewById(R.id.Title);
        title.setVisibility(View.INVISIBLE);
        body = findViewById(R.id.MsgBody);
        body.setVisibility(View.INVISIBLE);
    }

    public void setCallBacks(GDPRConsentLib consentLib) {
        acceptAll.setOnClickListener(_v -> consentLib.onMsgAccepted());

        rejectAll.setOnClickListener(_v -> consentLib.onMsgRejected());

        showOptions.setOnClickListener(_v -> consentLib.onMsgShowOptions());

        cancel.setOnClickListener(_v -> consentLib.onMsgCancel());
    }

    public void setAttributes(NativeMessageAttrs attrs){
        setChildAttributes(title, attrs.title);
        setChildAttributes(body, attrs.body);
        for(NativeMessageAttrs.Action action: attrs.actions){
            setChildAttributes(findButton(action.choiceType), action);
        }
    }

    public Button findButton(int choiceType){
        switch (choiceType) {
            case GDPRConsentLib.ActionTypes.MSG_SHOW_OPTIONS:
                return showOptions;
            case GDPRConsentLib.ActionTypes.MSG_ACCEPT:
                return acceptAll;
            case GDPRConsentLib.ActionTypes.MSG_CANCEL:
                return cancel;
            case GDPRConsentLib.ActionTypes.MSG_REJECT:
                return rejectAll;
            default:
                return null;
        }
    }

    public void setChildAttributes(TextView v, NativeMessageAttrs.Attribute attr){
        v.setVisibility(View.VISIBLE);
        v.setText(attr.text);
        v.setTextColor(attr.style.color);
        //v.setTextSize(attr.style.fontSize * getResources().getDisplayMetrics().scaledDensity);
        v.setBackgroundColor(attr.style.backgroundColor);
    }
}

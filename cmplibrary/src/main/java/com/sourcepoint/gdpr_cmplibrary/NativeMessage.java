package com.sourcepoint.gdpr_cmplibrary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.gdpr_cmplibrary.R;

/**
 * TODO: document your custom view class.
 */
public class NativeMessage extends RelativeLayout {

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

    private void init(){
        inflate(getContext(), R.layout.sample_native_message, this);
    }

    public void setCallBacks(GDPRConsentLib consentLib) {
        this.findViewById(com.example.gdpr_cmplibrary.R.id.AcceptAll).setOnClickListener(_v -> {
            consentLib.onMsgAccepted();
        });

        this.findViewById(com.example.gdpr_cmplibrary.R.id.RejectAll).setOnClickListener(_v -> {
            consentLib.onMsgRejected();
        });

        this.findViewById(com.example.gdpr_cmplibrary.R.id.ShowOption).setOnClickListener(_v -> {
            consentLib.onShowPm();
        });

        this.findViewById(R.id.Cancel).setOnClickListener(_v -> {
            consentLib.onDismiss();
        });

    }
}

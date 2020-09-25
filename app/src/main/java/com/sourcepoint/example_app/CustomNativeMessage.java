package com.sourcepoint.example_app;

import android.content.Context;
import android.util.AttributeSet;

import com.sourcepoint.gdpr_cmplibrary.NativeMessage;

public class CustomNativeMessage extends NativeMessage {

    public CustomNativeMessage(final Context context) {
        super(context);
    }

    public CustomNativeMessage(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNativeMessage(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

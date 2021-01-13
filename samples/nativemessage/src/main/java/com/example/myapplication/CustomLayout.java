package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import com.sourcepoint.gdpr_cmplibrary.NativeMessage;

public class CustomLayout extends NativeMessage {
    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

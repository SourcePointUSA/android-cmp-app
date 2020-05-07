package com.sourcepoint.gdpr_cmplibrary;

import androidx.test.core.app.ApplicationProvider;

import com.example.gdpr_cmplibrary.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class NativeMessageTest {

    private NativeMessage nativeMessage;

    @Before
    public void setUp(){
        nativeMessage = new NativeMessage(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void init(){
        NativeMessage spyMessage = spy(nativeMessage);
        spyMessage.init();
        verify(spyMessage ,times(1)).setAcceptAll(nativeMessage.findViewById(R.id.AcceptAll));
        verify(spyMessage ,times(1)).setRejectAll(nativeMessage.findViewById(R.id.RejectAll));
        verify(spyMessage ,times(1)).setShowOptions(nativeMessage.findViewById(R.id.ShowOptions));
        verify(spyMessage ,times(1)).setCancel(nativeMessage.findViewById(R.id.Cancel));
        verify(spyMessage ,times(1)).setTitle(nativeMessage.findViewById(R.id.Title));
        verify(spyMessage ,times(1)).setBody(nativeMessage.findViewById(R.id.MsgBody));

    }

}
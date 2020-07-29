package com.sourcepoint.gdpr_cmplibrary;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;



@RunWith(RobolectricTestRunner.class)
public class UIThreadHandlerTest {

    @Mock
    private Runnable lambdaMock;

    private UIThreadHandler uiThreadHandler;

    @Before
    public void setup(){
        initMocks(this);
        uiThreadHandler = spy(new UIThreadHandler(ApplicationProvider.getApplicationContext().getMainLooper()));
        doReturn(true).when(uiThreadHandler).post(any(Runnable.class));
    }

    @Test
    public void shouldBeEnabledAsDefault(){
        assertTrue(uiThreadHandler.postIfEnabled(lambdaMock));
        verify(uiThreadHandler).post(lambdaMock);
    }

    @Test
    public void shouldNotCallPostWhenDisabledAndCallItOnceEnabledAgain(){
        uiThreadHandler.disable();
        assertFalse(uiThreadHandler.postIfEnabled(lambdaMock));
        verify(uiThreadHandler, never()).post(lambdaMock);
        uiThreadHandler.enable();
        assertTrue(uiThreadHandler.postIfEnabled(lambdaMock));
        verify(uiThreadHandler).post(lambdaMock);
    }
}

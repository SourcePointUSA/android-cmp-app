package com.sourcepointmeta.app.utility;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UtilTest {

    //this test should fail if network not available
    //should pass if network available

    @Test
    public void testIsNetworkAvailable(){

        Context context = InstrumentationRegistry.getTargetContext();

        boolean networkAvailable = Util.isNetworkAvailable(context);

        assertTrue(networkAvailable);

    }
}

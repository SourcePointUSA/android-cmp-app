package com.sourcepoint.cmplibrary.util

import android.os.Looper
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNotEquals
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.create
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ExecutorManagerTest {

    @Test
    fun is_the_execution_on_main_thread() = runBlocking<Unit> {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        /** execution */
        ExecutorManager
            .create(appContext)
            .executeOnMain {
                /** in the main thread */
                Looper.myLooper().assertEquals(Looper.getMainLooper())
            }
        /** out the main thread */
        Looper.myLooper().assertNotEquals(Looper.getMainLooper())
    }
}

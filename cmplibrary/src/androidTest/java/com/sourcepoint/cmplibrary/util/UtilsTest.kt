package com.sourcepoint.cmplibrary.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UtilsTest {

    @Test
    fun checkMainThread_does_nothing_on_the_main_thread() = runBlocking<Unit> {
        Handler(Looper.getMainLooper()).post {
            checkMainThread("tests")
        }
    }

    @Test
    fun checkWorkerThread_does_nothing_on_a_worker_thread() = runBlocking<Unit> {
        checkWorkerThread("test")
    }

    @Test(expected = Throwable::class)
    fun checkMainThread_throws_an_exception_on_a_worker_thread() = runBlocking<Unit> {
        checkMainThread("tests")
    }
}

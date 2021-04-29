package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import org.junit.Test
import java.lang.Exception

class UnityUtilsTest {

    // targetingParamArrayToList tests:
    @Test
    fun `CALLING targetingParamArrayToList with empty Array RETURN empty List`() {
        val someArray: Array<TargetingParam> = emptyArray()
        val list = targetingParamArrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
    }

    @Test
    fun `CALLING targetingParamArrayToList with 1 element Array RETURN 1 element List`() {
        val someArray: Array<TargetingParam> = arrayOf(TargetingParam("language", "UA"))
        val list = targetingParamArrayToList(someArray)
        list.assertNotNull()
        list.size.assertEquals(someArray.size)
        list[0].key.assertEquals(someArray[0].key)
        list[0].value.assertEquals(someArray[0].value)
    }

    @Test(expected = NullPointerException::class)
    fun `CALLING targetingParamArrayToList with null RETURN NullPointerException`() {
        // this simulates when NULL comes from Unity-side
        targetingParamArrayToList(null!!)
        // TODO: make sane implementation with anonymous null
    }

    // throwableToException tests:
    @Test(expected = Exception::class)
    fun `CALLING throwableToException with Throwable RETURN Exception`() {
        val throwable: Throwable = Throwable()
        throwableToException(throwable)
    }

    @Test
    fun `CALLING throwableToException with Throwable with message RETURN Exception with same message`() {
        val myErrMessage: String = "Hello yes this is error!"
        val throwable: Throwable = Throwable(myErrMessage)
        try {
            throwableToException(throwable)
        } catch (exception: Exception) {
            exception.assertNotNull()
            exception.message.assertNotNull()
            exception.stackTrace.assertNotNull()
            exception.message!!.contains(myErrMessage).assertEquals(true)
        }
    }
}

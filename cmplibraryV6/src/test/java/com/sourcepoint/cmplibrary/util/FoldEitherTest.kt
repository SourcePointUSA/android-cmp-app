package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.fold
import com.sourcepoint.cmplibrary.core.getOrNull
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class FoldEitherTest {

    @MockK
    private lateinit var rightFun: (Int) -> Unit
    @MockK
    private lateinit var leftFun: (Throwable) -> Unit

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `GIVEN a Left obj extract the content`() {

        val value = RuntimeException()
        val original = Either.Left(value)

        original.fold(
            { throwable -> leftFun(throwable) },
            { i: Int -> rightFun(i) }
        )

        verify(exactly = 0) { rightFun(any()) }
        verify(exactly = 1) { leftFun(any()) }
    }

    @Test
    fun `GIVEN a Right obj extract the content`() {

        val value = 1
        val original = Either.Right(value)

        original.fold(
            { throwable -> leftFun(throwable) },
            { i: Int -> rightFun(i) }
        )

        verify(exactly = 1) { rightFun(any()) }
        verify(exactly = 0) { leftFun(any()) }
    }

    @Test
    fun `GIVEN an Either obj EXECUTE getOrNull`() {

        val left = Either.Left(RuntimeException())
        val right = Either.Right(1)

        right.getOrNull().assertEquals(1)
        left.getOrNull().assertEquals(null)
    }
}

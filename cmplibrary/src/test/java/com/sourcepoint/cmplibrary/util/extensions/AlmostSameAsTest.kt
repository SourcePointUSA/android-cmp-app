package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.campaign.almostSameAs
import org.junit.Test

class AlmostSameAsTest {
    @Test
    fun returns_false_when_not_almost_the_same() {
        1.0.almostSameAs(1.00001).assertFalse()
    }

    @Test
    fun returns_true_when_almost_the_same() {
        1.0.almostSameAs(1.0000001).assertTrue()
    }
}

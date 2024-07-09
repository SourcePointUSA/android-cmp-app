package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import org.junit.Before
import org.junit.Test

class ConsentManagerUtilsImplTest {

    @MockK
    private lateinit var campaignManager: CampaignManager

    @MockK
    private lateinit var dataStorage: DataStorage

    private val sut: ConsentManagerUtils by lazy {
        ConsentManagerUtils.create(campaignManager, dataStorage)
    }

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun default_sampling_rate_is_1() {
        ConsentManagerUtils.DEFAULT_SAMPLE_RATE.assertEquals(1.0)
    }

    @Test
    fun sample_returns_true_if_random_number_inside_sammpling_rate() {
        mockkConstructor(IntRange::class)
        every { anyConstructed<IntRange>().random() } returns 10
        sut.sample(0.5).assertTrue()
        unmockkConstructor(IntRange::class)
    }

    @Test
    fun sample_returns_false_if_random_number_outside_sammpling_rate() {
        mockkConstructor(IntRange::class)
        every { anyConstructed<IntRange>().random() } returns 90
        sut.sample(0.5).assertFalse()
        unmockkConstructor(IntRange::class)
    }
}

package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before

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

    // TODO: test ConsentManagerUtilsImplTest
}

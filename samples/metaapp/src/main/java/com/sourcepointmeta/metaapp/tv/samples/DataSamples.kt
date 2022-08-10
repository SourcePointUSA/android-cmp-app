package com.sourcepointmeta.metaapp.tv.samples

import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import kotlin.random.Random

object DataSamples {
    val randomManager = Random(System.currentTimeMillis())

    val DATA_CATEGORY = arrayOf(
        "GDPR+CCPA",
        "GDPR",
        "CCPA",
        "Else",
        "Utils"
    )
    private var nameCounter = 1

    fun generatePropertyDTO(): PropertyDTO{
        val name = "Name$nameCounter"
        nameCounter++
        val accountID = randomLong()
        val gdprPmId = randomLong()
        val ccpaPmId = randomLong()
        val ccpaEnabled = randomBoolean()
        val gdprEnabled = randomBoolean()
        return PropertyDTO(
            propertyName = name,
            accountId = accountID,
            campaignEnv = CampaignsEnv.PUBLIC.env,
            messageType = "message type",
            gdprPmId = "$gdprPmId",
            ccpaPmId = "$ccpaPmId",
            authId = "authId",
            pmTab = PMTab.DEFAULT,
            messageLanguage = MessageLanguage.ENGLISH,
            gdprEnabled = gdprEnabled,
            ccpaEnabled = ccpaEnabled,
            property = Property(
                accountId = accountID,
                messageLanguage = MessageLanguage.ENGLISH.value,
                campaignsEnv = CampaignsEnv.PUBLIC,
                ccpaPmId = ccpaPmId,
                gdprPmId = gdprPmId,
                messageType = "message type",
                propertyName = name,
                statusCampaignSet = HashSet(listOf(
                    StatusCampaign(name, CampaignType.GDPR, gdprEnabled),
                    StatusCampaign(name, CampaignType.CCPA, ccpaEnabled)
                ))
            ),
            timeout = 100000L,
        )
    }

    private fun randomLong(): Long = randomManager.nextLong(100000)
    private fun randomBoolean(): Boolean = randomManager.nextBoolean()
}
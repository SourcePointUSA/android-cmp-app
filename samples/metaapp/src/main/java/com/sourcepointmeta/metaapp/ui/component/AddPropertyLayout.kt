package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import kotlinx.android.synthetic.main.add_property_fragment.view.*

class AddPropertyLayout : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

internal fun AddPropertyLayout.bind(property: Property) {
    prop_name_ed.setText(property.propertyName)
    account_id_ed.setText(property.accountId.toString())
    message_type_autocomplete.setText(property.messageType)
    radio_stage.isChecked = property.is_staging
    radio_prod.isChecked = !property.is_staging
    chip_gdpr.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.GDPR }?.enabled ?: false
    chip_ccpa.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.CCPA }?.enabled ?: false
    val (gdprTp, ccpaTp) = property.targetingParameters.partition { it.campaign == CampaignType.GDPR }
    gdprTp.forEach { gdpr_chip_group.addChip("${it.key}:${it.value}") }
    ccpaTp.forEach { ccpa_chip_group.addChip("${it.key}:${it.value}") }
    message_language_autocomplete.setText(property.messageLanguage)
    auth_id_ed.setText(property.authId)
    pm_tab_autocomplete.setText(property.pmTab)
    gdpr_pm_id_ed.setText(property.gdprPmId)
    ccpa_pm_id_ed.setText(property.ccpaPmId)
    message_language_autocomplete.setText(property.messageLanguage)
}

internal fun AddPropertyLayout.toProperty(): Property {

    val gdprTp = gdpr_chip_group.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = prop_name_ed.text.toString(),
                campaign = CampaignType.GDPR,
                key = it[0],
                value = it[1]
            )
        }
        .toList()

    val ccpaTp = ccpa_chip_group.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = prop_name_ed.text.toString(),
                campaign = CampaignType.CCPA,
                key = it[0],
                value = it[1]
            )
        }
        .toList()

    val chipGdprChecked = chip_gdpr.isChecked
    val chipCcpaChecked = chip_ccpa.isChecked

    val gdprStatus = StatusCampaign(prop_name_ed.text.toString(), CampaignType.GDPR, chipGdprChecked)
    val ccpaStatus = StatusCampaign(prop_name_ed.text.toString(), CampaignType.CCPA, chipCcpaChecked)

    return Property(
        propertyName = prop_name_ed.text.toString(),
        accountId = account_id_ed.text.toString().toLong(),
        propertyId = 1,
        authId = auth_id_ed.text.toString(),
        messageLanguage = message_language_autocomplete.text.toString(),
        pmTab = pm_tab_autocomplete.text.toString(),
        is_staging = radio_stage.isChecked,
        targetingParameters = ccpaTp + gdprTp,
        statusCampaignSet = setOf(gdprStatus, ccpaStatus),
        messageType = message_type_autocomplete.text.toString(),
        gdprPmId = gdpr_pm_id_ed.text.toString(),
        ccpaPmId = ccpa_pm_id_ed.text.toString()
    )
}

package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionBinary
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionTernary
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionTernary.NO
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionTernary.NOT_APPLICABLE
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppOptionTernary.YES
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.GPP
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.util.check
import kotlinx.android.synthetic.main.add_property_fragment.view.*
import kotlinx.android.synthetic.main.add_property_fragment.view.chip_ccpa
import kotlinx.android.synthetic.main.add_property_fragment.view.chip_gdpr
import kotlinx.android.synthetic.main.add_property_fragment.view.chip_usnat

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
    prop_id_ed.setText(property.propertyId.toString())
    account_id_ed.setText(property.accountId.toString())
    radio_stage.isChecked = property.is_staging
    radio_prod.isChecked = !property.is_staging
    chip_gdpr.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.GDPR }?.enabled ?: false
    chip_ccpa.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.CCPA }?.enabled ?: false
    chip_usnat.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.USNAT }?.enabled ?: false
    message_language_autocomplete.setText(property.messageLanguage)
    message_type_autocomplete.setText(property.messageType.name)
    auth_id_ed.setText(property.authId)
    pm_tab_autocomplete.setText(property.pmTab)
    gdpr_pm_id_ed.setText(property.gdprPmId?.toString() ?: "")
    ccpa_pm_id_ed.setText(property.ccpaPmId?.toString() ?: "")
    usnat_pm_id_ed.setText(property.usnatPmId?.toString() ?: "")
    message_language_autocomplete.setText(property.messageLanguage)
    val gdprTp = property.targetingParameters.filter { it.campaign == CampaignType.GDPR }
    val ccpaTp = property.targetingParameters.filter { it.campaign == CampaignType.CCPA }
    val usnatTp = property.targetingParameters.filter { it.campaign == CampaignType.USNAT }
    // campaignEnv is presented as a radio btn, no need to add it as chip
    gdprTp.forEach { gdpr_chip_group.addChip("${it.key}:${it.value}") }
    ccpaTp.forEach { ccpa_chip_group.addChip("${it.key}:${it.value}") }
    usnatTp.forEach { usnat_chip_group.addChip("${it.key}:${it.value}") }
    timeout_ed.setText("${property.timeout ?: 3000}")
    group_pm_id_ed.setText(property.gdprGroupPmId ?: "")
    gdpr_groupId_switch.isChecked = property.useGdprGroupPmIfAvailable

    property.gpp
        ?.let {
            gpp_switch.isChecked = true
            it.coveredTransaction?.let { ct -> gpp_field_coveredTransaction.isChecked = (ct == SpGppOptionBinary.YES) }
            it.optOutOptionMode?.let { ooo ->
                when (ooo) {
                    NOT_APPLICABLE -> { opt_out_option_radio_na.isChecked = true }
                    NO -> { opt_out_option_radio_no.isChecked = true }
                    YES -> { opt_out_option_radio_yes.isChecked = true }
                }
            }
            it.serviceProviderMode?.let { spm ->
                when (spm) {
                    NOT_APPLICABLE -> { service_provider_radio_na.isChecked = true }
                    NO -> { service_provider_radio_no.isChecked = true }
                    YES -> { service_provider_radio_yes.isChecked = true }
                }
            }
        }
        ?: run {
            gpp_switch.isChecked = false
            opt_out_option_radio_group.isEnabled = false
            service_provider_mode_radio_group.isEnabled = false
            opt_out_option_radio_na.isEnabled = false
            opt_out_option_radio_no.isEnabled = false
            opt_out_option_radio_yes.isEnabled = false
            service_provider_radio_na.isEnabled = false
            service_provider_radio_no.isEnabled = false
            service_provider_radio_yes.isEnabled = false
            gpp_field_coveredTransaction.isEnabled = false
        }
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
        .toMutableList()

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
        .toMutableList()

    val usNatTp = usnat_chip_group.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = prop_name_ed.text.toString(),
                campaign = CampaignType.USNAT,
                key = it[0],
                value = it[1]
            )
        }
        .toMutableList()

    val gpp = if (gpp_switch.isChecked) {
        GPP(
            propertyName = prop_name_ed.text.toString(),
            coveredTransaction = if (gpp_field_coveredTransaction.isChecked) SpGppOptionBinary.YES else SpGppOptionBinary.NO,
            optOutOptionMode = this.getOptOutOptionMode(),
            serviceProviderMode = this.getServiceProviderMode()
        )
    } else null

    val chipGdprChecked = chip_gdpr.isChecked
    val chipCcpaChecked = chip_ccpa.isChecked
    val chipUsnatChecked = chip_usnat.isChecked

    val gdprStatus = StatusCampaign(prop_name_ed.text.toString(), CampaignType.GDPR, chipGdprChecked)
    val ccpaStatus = StatusCampaign(prop_name_ed.text.toString(), CampaignType.CCPA, chipCcpaChecked)
    val usnatStatus = StatusCampaign(prop_name_ed.text.toString(), CampaignType.USNAT, chipUsnatChecked)

    val gdprGroupPmId = group_pm_id_ed.text.toString()

    return Property(
        propertyName = prop_name_ed.text.toString(),
        accountId = account_id_ed.text.toString().toLongOrNull() ?: 0L,
        gdprPmId = gdpr_pm_id_ed.text.toString().toLongOrNull(),
        ccpaPmId = ccpa_pm_id_ed.text.toString().toLongOrNull(),
        is_staging = radio_stage.isChecked,
        targetingParameters = ccpaTp + gdprTp + usNatTp,
        timeout = timeout_ed.text.toString().toTimeout(),
        authId = auth_id_ed.text.toString(),
        messageLanguage = message_language_autocomplete.text.toString(),
        messageType = MessageType.values().find { it.name == message_type_autocomplete.text.toString() }
            ?: MessageType.MOBILE,
        pmTab = pm_tab_autocomplete.text.toString(),
        statusCampaignSet = setOf(gdprStatus, ccpaStatus, usnatStatus),
        campaignsEnv = if (radio_stage.isChecked) CampaignsEnv.STAGE else CampaignsEnv.PUBLIC,
        gdprGroupPmId = if (gdprGroupPmId.isEmpty() || gdprGroupPmId.isBlank()) null else gdprGroupPmId,
        useGdprGroupPmIfAvailable = gdpr_groupId_switch.isChecked,
        propertyId = prop_id_ed.text.toString().toInt(),
        usnatPmId = usnat_pm_id_ed.text.toString().toLongOrNull(),
        gpp = gpp
    )
}

private fun AddPropertyLayout.getServiceProviderMode(): SpGppOptionTernary? {
    return if (service_provider_radio_na.isChecked) NOT_APPLICABLE
    else if (service_provider_radio_no.isChecked) NO
    else if (service_provider_radio_yes.isChecked) YES
    else null
}

private fun AddPropertyLayout.getOptOutOptionMode(): SpGppOptionTernary? {
    return if (opt_out_option_radio_na.isChecked) NOT_APPLICABLE
    else if (opt_out_option_radio_no.isChecked) NO
    else if (opt_out_option_radio_yes.isChecked) YES
    else null
}

fun String.toTimeout(): Long = check { toLong() }.getOrNull() ?: 3000L

fun AddPropertyLayout.errorField(it: BaseState.StateErrorValidationField) = when (it.uiCode) {
    UIErrorCode.PropertyId -> {
        prop_id_ed.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.PropertyName -> {
        prop_name_ed.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.PmTab -> {
        pm_tab_autocomplete.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.AccountId -> {
        account_id_ed.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.MessageLanguage -> {
        message_language_autocomplete.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.CcpaPmId -> {
        ccpa_pm_id_ed.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.GdprPmId -> {
        gdpr_pm_id_ed.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.AuthId -> {
        auth_id_ed.run {
            requestFocus()
            error = it.message
        }
    }
    else -> {
    }
}

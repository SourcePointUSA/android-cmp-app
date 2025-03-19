package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.SpGppOptionBinary
import com.sourcepoint.cmplibrary.model.exposed.SpGppOptionTernary
import com.sourcepoint.cmplibrary.model.exposed.SpGppOptionTernary.* // ktlint-disable
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.GPP
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.databinding.AddPropertyFragmentBinding
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.util.check

class AddPropertyLayout : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

internal fun AddPropertyLayout.bind(property: Property, binding: AddPropertyFragmentBinding) {
    binding.propNameEd.setText(property.propertyName)
    binding.propIdEd.setText(property.propertyId.toString())
    binding.accountIdEd.setText(property.accountId.toString())
    binding.radioStage.isChecked = property.is_staging
    binding.radioProd.isChecked = !property.is_staging
    binding.chipGdpr.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.GDPR }?.enabled ?: false
    binding.chipCcpa.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.CCPA }?.enabled ?: false
    binding.chipUsnat.isChecked = property.statusCampaignSet.find { it.campaignType == CampaignType.USNAT }?.enabled ?: false
    binding.messageLanguageAutocomplete.setText(property.messageLanguage)
    binding.messageTypeAutocomplete.setText(property.messageType.name)
    binding.authIdEd.setText(property.authId)
    binding.pmTabAutocomplete.setText(property.pmTab)
    binding.gdprPmIdEd.setText(property.gdprPmId?.toString() ?: "")
    binding.ccpaPmIdEd.setText(property.ccpaPmId?.toString() ?: "")
    binding.usnatPmIdEd.setText(property.usnatPmId?.toString() ?: "")
    binding.messageLanguageAutocomplete.setText(property.messageLanguage)
    val gdprTp = property.targetingParameters.filter { it.campaign == CampaignType.GDPR }
    val ccpaTp = property.targetingParameters.filter { it.campaign == CampaignType.CCPA }
    val usnatTp = property.targetingParameters.filter { it.campaign == CampaignType.USNAT }
    // campaignEnv is presented as a radio btn, no need to add it as chip
    gdprTp.forEach { binding.gdprChipGroup.addChip("${it.key}:${it.value}") }
    ccpaTp.forEach { binding.ccpaChipGroup.addChip("${it.key}:${it.value}") }
    usnatTp.forEach { binding.usnatChipGroup.addChip("${it.key}:${it.value}") }
    binding.timeoutEd.setText("${property.timeout ?: 3000}")
    binding.groupPmIdEd.setText(property.gdprGroupPmId ?: "")
    binding.gdprGroupIdSwitch.isChecked = property.useGdprGroupPmIfAvailable
    binding.usnatTransitionSwitch.isChecked = property.ccpa2usnat

    property.gpp
        ?.let {
            binding.gppSwitch.isChecked = true
            it.coveredTransaction?.let { ct -> binding.gppFieldCoveredTransaction.isChecked = (ct == SpGppOptionBinary.YES) }
            it.optOutOptionMode?.let { ooo ->
                when (ooo) {
                    NOT_APPLICABLE -> { binding.optOutOptionRadioNa.isChecked = true }
                    NO -> { binding.optOutOptionRadioNo.isChecked = true }
                    YES -> { binding.optOutOptionRadioYes.isChecked = true }
                }
            }
            it.serviceProviderMode?.let { spm ->
                when (spm) {
                    NOT_APPLICABLE -> { binding.serviceProviderRadioNa.isChecked = true }
                    NO -> { binding.serviceProviderRadioNo.isChecked = true }
                    YES -> { binding.serviceProviderRadioYes.isChecked = true }
                }
            }
        }
        ?: run {
            binding.gppSwitch.isChecked = false
            binding.optOutOptionRadioGroup.isEnabled = false
            binding.serviceProviderModeRadioGroup.isEnabled = false
            binding.optOutOptionRadioNa.isEnabled = false
            binding.optOutOptionRadioNo.isEnabled = false
            binding.optOutOptionRadioYes.isEnabled = false
            binding.serviceProviderRadioNa.isEnabled = false
            binding.serviceProviderRadioNo.isEnabled = false
            binding.serviceProviderRadioYes.isEnabled = false
            binding.gppFieldCoveredTransaction.isEnabled = false
        }
}

internal fun AddPropertyLayout.toProperty(binding: AddPropertyFragmentBinding): Property {

    val gdprTp = binding.gdprChipGroup.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = binding.propNameEd.text.toString(),
                campaign = CampaignType.GDPR,
                key = it[0],
                value = it[1]
            )
        }
        .toMutableList()

    val ccpaTp = binding.ccpaChipGroup.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = binding.propNameEd.text.toString(),
                campaign = CampaignType.CCPA,
                key = it[0],
                value = it[1]
            )
        }
        .toMutableList()

    val usNatTp = binding.usnatChipGroup.children
        .map { (it as Chip).text.split(":") }
        .map {
            MetaTargetingParam(
                propertyName = binding.propNameEd.text.toString(),
                campaign = CampaignType.USNAT,
                key = it[0],
                value = it[1]
            )
        }
        .toMutableList()

    val gpp = if (binding.gppSwitch.isChecked) {
        GPP(
            propertyName = binding.propNameEd.text.toString(),
            coveredTransaction = if (binding.gppFieldCoveredTransaction.isChecked) SpGppOptionBinary.YES else SpGppOptionBinary.NO,
            optOutOptionMode = this.getOptOutOptionMode(binding),
            serviceProviderMode = this.getServiceProviderMode(binding)
        )
    } else null

    val chipGdprChecked = binding.chipGdpr.isChecked
    val chipCcpaChecked = binding.chipCcpa.isChecked
    val chipUsnatChecked = binding.chipUsnat.isChecked

    val gdprStatus = StatusCampaign(binding.propNameEd.text.toString(), CampaignType.GDPR, chipGdprChecked)
    val ccpaStatus = StatusCampaign(binding.propNameEd.text.toString(), CampaignType.CCPA, chipCcpaChecked)
    val usnatStatus = StatusCampaign(binding.propNameEd.text.toString(), CampaignType.USNAT, chipUsnatChecked)

    val gdprGroupPmId = binding.groupPmIdEd.text.toString()

    return Property(
        propertyName = binding.propNameEd.text.toString(),
        accountId = binding.accountIdEd.text.toString().toLongOrNull() ?: 0L,
        gdprPmId = binding.gdprPmIdEd.text.toString().toLongOrNull(),
        usnatPmId = binding.usnatPmIdEd.text.toString().toLongOrNull(),
        is_staging = binding.radioStage.isChecked,
        targetingParameters = ccpaTp + gdprTp + usNatTp,
        timeout = binding.timeoutEd.text.toString().toTimeout(),
        authId = binding.authIdEd.text.toString(),
        messageLanguage = binding.messageLanguageAutocomplete.text.toString(),
        messageType = MessageType.values().find { it.name == binding.messageTypeAutocomplete.text.toString() }
            ?: MessageType.MOBILE,
        pmTab = binding.pmTabAutocomplete.text.toString(),
        statusCampaignSet = setOf(gdprStatus, ccpaStatus, usnatStatus),
        campaignsEnv = if (binding.radioStage.isChecked) CampaignsEnv.STAGE else CampaignsEnv.PUBLIC,
        gdprGroupPmId = if (gdprGroupPmId.isEmpty() || gdprGroupPmId.isBlank()) null else gdprGroupPmId,
        useGdprGroupPmIfAvailable = binding.gdprGroupIdSwitch.isChecked,
        ccpa2usnat = binding.usnatTransitionSwitch.isChecked,
        propertyId = binding.propIdEd.text.toString().toInt(),
        ccpaPmId = binding.ccpaPmIdEd.text.toString().toLongOrNull(),
        gpp = gpp
    )
}

private fun AddPropertyLayout.getServiceProviderMode(binding: AddPropertyFragmentBinding): SpGppOptionTernary? {
    return if (binding.serviceProviderRadioNa.isChecked) NOT_APPLICABLE
    else if (binding.serviceProviderRadioNo.isChecked) NO
    else if (binding.serviceProviderRadioYes.isChecked) YES
    else null
}

private fun AddPropertyLayout.getOptOutOptionMode(binding: AddPropertyFragmentBinding): SpGppOptionTernary? {
    return if (binding.optOutOptionRadioNa.isChecked) NOT_APPLICABLE
    else if (binding.optOutOptionRadioNo.isChecked) NO
    else if (binding.optOutOptionRadioYes.isChecked) YES
    else null
}

fun String.toTimeout(): Long = check { toLong() }.getOrNull() ?: 3000L

fun AddPropertyLayout.errorField(it: BaseState.StateErrorValidationField, binding: AddPropertyFragmentBinding) = when (it.uiCode) {
    UIErrorCode.PropertyId -> {
        binding.propIdEd.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.PropertyName -> {
        binding.propNameEd.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.PmTab -> {
        binding.pmTabAutocomplete.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.AccountId -> {
        binding.accountIdEd.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.MessageLanguage -> {
        binding.messageLanguageAutocomplete.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.CcpaPmId -> {
        binding.ccpaPmIdEd.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.GdprPmId -> {
        binding.gdprPmIdEd.run {
            requestFocus()
            error = it.message
        }
    }
    UIErrorCode.AuthId -> {
        binding.authIdEd.run {
            requestFocus()
            error = it.message
        }
    }
    else -> {
    }
}

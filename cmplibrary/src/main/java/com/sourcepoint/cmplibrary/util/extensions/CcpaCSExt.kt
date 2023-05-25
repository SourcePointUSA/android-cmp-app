package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus

/**
 * Extension function to generate consent string based on CcpaCS properties.
 *
 * This function depends on the standard of generating the CCPA consent string.
 * @see <a href="https://github.com/InteractiveAdvertisingBureau/USPrivacy/blob/master/CCPA/US%20Privacy%20String.md#us-privacy-string-format">US Privacy String Format</a>
 */
internal fun CcpaCS.generateConsentString(): String {
    return if (applies == null || applies == false) {
        DEFAULT_CCPA_CONSENT_STRING
    } else {
        val optOutSale = when (status) {
            CcpaStatus.rejectedAll, CcpaStatus.rejectedSome -> "Y"
            else -> "N"
        }
        val lspaCoveredTransaction = if (signedLspa == null || signedLspa == false) "N" else "Y"
        "$DEFAULT_SPECIFICATION_VERSION$DEFAULT_OPPORTUNITY_TO_OPT_OUT$optOutSale$lspaCoveredTransaction"
    }
}

private const val DEFAULT_CCPA_CONSENT_STRING = "1---"
private const val DEFAULT_SPECIFICATION_VERSION = "1"
private const val DEFAULT_OPPORTUNITY_TO_OPT_OUT = "Y"
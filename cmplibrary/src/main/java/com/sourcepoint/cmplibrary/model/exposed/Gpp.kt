package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.mobile_core.network.requests.IncludeData

data class SpGppConfig(
    val coveredTransaction: SpGppOptionBinary?,
    val optOutOptionMode: SpGppOptionTernary?,
    val serviceProviderMode: SpGppOptionTernary?
) {
    fun toCore() = IncludeData.GPPConfig(
        MspaCoveredTransaction = coveredTransaction?.toCore(),
        MspaOptOutOptionMode = optOutOptionMode?.toCore(),
        MspaServiceProviderMode = serviceProviderMode?.toCore()
    )
}

enum class SpGppOptionBinary(val type: String) {
    YES("yes"),
    NO("no");

    fun toCore() = when (this) {
        YES -> IncludeData.MspaBinaryFlag.yes
        NO -> IncludeData.MspaBinaryFlag.no
    }
}

enum class SpGppOptionTernary(val type: String) {
    YES("yes"),
    NO("no"),
    NOT_APPLICABLE("na");

    fun toCore() = when (this) {
        YES -> IncludeData.MspaTernaryFlag.yes
        NO -> IncludeData.MspaTernaryFlag.no
        NOT_APPLICABLE -> IncludeData.MspaTernaryFlag.na
    }
}

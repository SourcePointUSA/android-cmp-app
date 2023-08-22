package com.sourcepoint.cmplibrary.exposed.gpp

/**
 * Class that is responsible for storing and passing user's config for GPP legislation
 */
data class GppConfig(
    val coveredTransaction: GppOptionBinary = GppOptionBinary.NO,
    val optOutOptionMode: GppOptionTernary = GppOptionTernary.NOT_APPLICABLE,
    val serviceProviderMode: GppOptionTernary = GppOptionTernary.NOT_APPLICABLE,
) {

    private constructor(
        builder: Builder
    ) : this(
        coveredTransaction = builder.coveredTransaction ?: GppOptionBinary.NO,
        optOutOptionMode = builder.optOutOptionMode ?: GppOptionTernary.NOT_APPLICABLE,
        serviceProviderMode = builder.serviceProviderMode ?: GppOptionTernary.NOT_APPLICABLE,
    )

    class Builder {

        internal var coveredTransaction: GppOptionBinary? = null
            private set

        internal var optOutOptionMode: GppOptionTernary? = null
            private set

        internal var serviceProviderMode: GppOptionTernary? = null
            private set

        fun setCoveredTransaction(
            coveredTransaction: GppOptionBinary
        ) = apply { this.coveredTransaction = coveredTransaction }

        fun setOptOutOptionMode(
            optOutOptionMode: GppOptionTernary
        ) = apply { this.optOutOptionMode = optOutOptionMode }

        fun setServiceProviderMode(
            serviceProviderMode: GppOptionTernary
        ) = apply { this.serviceProviderMode = serviceProviderMode }

        fun build() = GppConfig(this)
    }
}

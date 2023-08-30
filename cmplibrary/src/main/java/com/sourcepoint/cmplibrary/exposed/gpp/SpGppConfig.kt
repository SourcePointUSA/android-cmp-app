package com.sourcepoint.cmplibrary.exposed.gpp

/**
 * Class that is responsible for storing and passing user's config for GPP legislation
 */
data class SpGppConfig(
    val coveredTransaction: SpGppOptionBinary? = null,
    val optOutOptionMode: SpGppOptionTernary? = null,
    val serviceProviderMode: SpGppOptionTernary? = null,
)

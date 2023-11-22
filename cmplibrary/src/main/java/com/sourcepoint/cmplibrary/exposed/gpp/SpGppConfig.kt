package com.sourcepoint.cmplibrary.exposed.gpp

/**
 * Class that is responsible for storing and passing user's config for GPP legislation
 */
data class SpGppConfig(
    val coveredTransaction: SpGppOptionBinary? = null,
    val optOutOptionMode: SpGppOptionTernary? = null,
    val serviceProviderMode: SpGppOptionTernary? = null,
)

enum class SpGppOptionBinary(
    val type: String,
) {
    YES("yes"),
    NO("no"),
}

enum class SpGppOptionTernary(
    val type: String,
) {
    YES("yes"),
    NO("no"),
    NOT_APPLICABLE("na"),
}

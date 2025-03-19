package com.sourcepoint.cmplibrary.model.exposed

data class SpGppConfig(
    val coveredTransaction: SpGppOptionBinary?,
    val optOutOptionMode: SpGppOptionTernary?,
    val serviceProviderMode: SpGppOptionTernary?,
)

enum class SpGppOptionBinary(val type: String) {
    YES("yes"),
    NO("no"),
}

enum class SpGppOptionTernary(val type: String) {
    YES("yes"),
    NO("no"),
    NOT_APPLICABLE("na"),
}

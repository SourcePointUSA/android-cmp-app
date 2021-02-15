package com.sourcepoint.cmplibrary.core.layout.attribute

data class StyleK(
    val fontFamily: String,
    val fontSize: Int,
    val color: Int,
    val backgroundColor: Int
) {
    private fun getSixDigitHexValue(colorString: String): String? {
        return if (colorString.length == 4)
            colorString.replace("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(), "#$1$1$2$2$3$3")
        else colorString
    }
}

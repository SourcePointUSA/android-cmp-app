package com.sourcepoint.cmplibrary.exception

enum class LoggerType(val t: String) {
    JSON_TYPE("*JSON*"),
    URL_TYPE("*URL*"),
    ERROR_TYPE("*ERROR*"),
    C_TYPE("*COMPUTATION*"),
    NL(System.getProperty("line.separator") ?: "\\n")
}

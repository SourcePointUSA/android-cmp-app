package com.sourcepoint.gdpr_cmplibrary

import java.lang.RuntimeException

internal sealed class ConsentLibExceptionK : RuntimeException{
    constructor(code : String, description : String) : super()
    constructor(code : String, description : String, cause: Throwable?) : super(cause)
}

internal class NoInternetConnectionException : ConsentLibExceptionK {
    companion object{
        const val code = ""
        const val description : String = ""
    }
    constructor() : super(code, description)
    constructor(cause: Throwable?) : super(code, description, cause)
}

internal class ApiException : ConsentLibExceptionK {
    companion object{
        const val code = ""
        const val description : String = ""
    }
    constructor() : super(code, description)
    constructor(cause: Throwable?) : super(code, description, cause)
}

internal class ParseException : ConsentLibExceptionK {
    companion object{
        const val code = ""
        const val description : String = ""
    }
    constructor() : super(code, description)
    constructor(cause: Throwable?) : super(code, description, cause)
}

package com.sourcepoint.cmplibrary.core.web

internal fun JSReceiver.Companion.build() : JSReceiver =
    JSReceiverImpl(
        // TODO remove this mock implementation

        object : JSReceiverClient {
            override fun onConsentUIReady(isFromPM: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onAction(actionData: String) {
                TODO("Not yet implemented")
            }

            override fun onError(errorMessage: String) {
                TODO("Not yet implemented")
            }
        }
    )

private class JSReceiverImpl(val client : JSReceiverClient) : JSReceiver {

    override fun log(tag: String?, msg: String?) {
        TODO("Not yet implemented")
    }

    override fun log(msg: String?) {
        TODO("Not yet implemented")
    }

    override fun onConsentUIReady(isFromPM: Boolean) {
        client.onConsentUIReady(isFromPM)
    }

    override fun onAction(actionData: String?) {
        actionData
            ?.let { client.onAction(it) }
            ?: fail("actionData is null!!")

    }

    override fun onError(errorMessage: String?) {
        val error = errorMessage ?: "error"
        client.onError(error)
    }

    fun fail(message : String){
        client.onError(message)
    }
}
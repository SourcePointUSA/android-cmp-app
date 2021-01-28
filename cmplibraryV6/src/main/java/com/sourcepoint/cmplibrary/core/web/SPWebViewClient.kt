package com.sourcepoint.cmplibrary.core.web

import android.net.http.SslError
import android.util.Log
import android.webkit.*
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException.ApiException
import com.sourcepoint.gdpr_cmplibrary.exception.Logger
import com.sourcepoint.gdpr_cmplibrary.exception.UnableToLoadJSReceiverException
import com.sourcepoint.gdpr_cmplibrary.exception.WebViewException
import java.io.IOException

class SPWebViewClient(
    val wv: WebView,
    private val log: Logger,
    private val onError : (ConsentLibException) -> Unit,
    private val onNoIntentActivitiesFoundFor : (String) -> Unit

) : WebViewClient() {

    companion object {
        val TAG = SPWebViewClient::class.simpleName
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        try {
            view.loadUrl("javascript:" + "js_receiver.js".file2String())
        } catch (e: IOException) {
            onError(ConsentLibException(e, "Unable to load jsReceiver into ConasentLibWebview."))
            log.error(UnableToLoadJSReceiverException(e, "Unable to load jsReceiver into ConasentLibWebview."))
        }
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        Log.d(TAG, "onReceivedError: $error")
        onError(ApiException(error.toString()))
        log.error(UnableToLoadJSReceiverException(description = error.toString()))
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Log.d(TAG, "onReceivedError: Error $errorCode: $description")
        onError(ApiException(description))
        log.error(UnableToLoadJSReceiverException(description = description))
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError) {
        super.onReceivedSslError(view, handler, error)
        Log.d(TAG, "onReceivedSslError: Error $error")
        onError(ApiException(error.toString()))
        log.error(UnableToLoadJSReceiverException(description = error.toString()))
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        val message = "The WebView rendering process crashed!"
        Log.e(TAG, message)
        onError(ConsentLibException(message))
        log.error(WebViewException(description = message))
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        wv.context.loadLinkOnExternalBrowser(url){
            onNoIntentActivitiesFoundFor(it)
        }
        return true
    }
}
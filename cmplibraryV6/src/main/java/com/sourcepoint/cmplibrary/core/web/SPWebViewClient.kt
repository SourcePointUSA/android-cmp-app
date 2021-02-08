package com.sourcepoint.cmplibrary.core.web

import android.net.http.SslError
import android.webkit.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser
import com.sourcepoint.gdpr_cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.exception.WebViewException

class SPWebViewClient(
    val wv: WebView,
    private val onError: (ConsentLibExceptionK) -> Unit,
    private val onNoIntentActivitiesFoundFor: (String) -> Unit,
    private val onPageFinishedLambda: (view: WebView, url: String?) -> Unit

) : WebViewClient() {

    companion object {
        val TAG = SPWebViewClient::class.simpleName
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        try {
            view.loadUrl("javascript:" + "js_receiver.js".file2String())
            onPageFinishedLambda(view, url)
        } catch (e: Throwable) {
            onError(WebViewException(cause = e, description = "Unable to load jsReceiver into ConasentLibWebview."))
        }
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest?, error: WebResourceError) {
        super.onReceivedError(view, request, error)
        onError(WebViewException(description = error.toString()))
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        onError(WebViewException(description = description))
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError) {
        super.onReceivedSslError(view, handler, error)
        onError(WebViewException(description = error.toString()))
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        val message = "The WebView rendering process crashed!"
        onError(WebViewException(description = message))
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        wv.context.loadLinkOnExternalBrowser(url) {
            onNoIntentActivitiesFoundFor(it)
        }
        return true
    }
}

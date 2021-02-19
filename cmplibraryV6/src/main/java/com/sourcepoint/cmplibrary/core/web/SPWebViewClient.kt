package com.sourcepoint.cmplibrary.core.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*  //ktlint-disable
import com.sourcepoint.cmplibrary.exception.ConnectionTimeoutException
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.WebViewException
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser

internal class SPWebViewClient(
    val wv: WebView,
    private val onError: (ConsentLibExceptionK) -> Unit,
    private val onNoIntentActivitiesFoundFor: (String) -> Unit,
    private val timer: SpTimer,
    private val messageTimeout: Long = 10000
) : WebViewClient() {

    var onPageFinishedLambda: ((view: WebView, url: String?) -> Unit)? = null

    companion object {
        val TAG = SPWebViewClient::class.simpleName
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        timer.executeDelay(messageTimeout) {
            onError(ConnectionTimeoutException(description = "A timeout has occurred when loading the message"))
            view?.stopLoading()
        }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        timer.cancel()
        try {
            view.loadUrl("javascript:" + "js_receiver.js".file2String())
            /** make it crash if [onPageFinishedLambda] is null!!! */
            onPageFinishedLambda?.invoke(view, url)
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

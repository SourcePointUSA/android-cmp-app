package com.sourcepoint.cmplibrary.core.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.webkit.*  //ktlint-disable
import com.sourcepoint.cmplibrary.exception.*  //ktlint-disable
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.RenderingAppConnectionTimeoutException
import com.sourcepoint.cmplibrary.exception.UrlLoadingException
import com.sourcepoint.cmplibrary.exception.WebViewException
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser

internal class SPWebViewClient(
    val wv: WebView,
    private val messageTimeout: Long,
    private val onError: (ConsentLibExceptionK) -> Unit,
    private val onNoIntentActivitiesFoundFor: (String) -> Unit,
    private val timer: SpTimer,
    private val logger: Logger
) : WebViewClient() {

    var jsReceiverConfig: (() -> String)? = null

    companion object {
        val TAG = SPWebViewClient::class.simpleName
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        timer.executeDelay(messageTimeout) {
            onError(RenderingAppConnectionTimeoutException(description = "There was an error while loading the rendering app. onConsentReady was not called within $messageTimeout seconds."))
            view?.stopLoading()
        }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        try {
            jsReceiverConfig
                ?.let {
                    view.evaluateJavascript(it()) {}
                }
                ?: let {
                    view.loadUrl("javascript:" + "js_receiver.js".file2String())
                    logger.d(
                        SPWebViewClient::class.java.name,
                        """
                        jsReceiverConfig is null!! 
                        This means that the Legislation is not set and cannot deciding which is the correct link GDPR or CCPA?
                        """.trimIndent()
                    )
                }
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

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val nl = System.getProperty("line.separator")
            val message = errorResponse?.responseHeaders!!.toList().fold(StringBuilder()) { acc, pair ->
                acc.append("${pair.first}:${pair.second} $nl")
                acc
            }.toString()
            val errMess = "Error loading SPWebViewClient $nl StatusCode ---> ${errorResponse.statusCode} $nl$message "
            logger.e(this::class.java.name, errMess)
            onError(UrlLoadingException(description = "The client failed to load the resource!!"))
        }
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
    }

    fun cancelTimer() {
        timer.cancel()
    }
}

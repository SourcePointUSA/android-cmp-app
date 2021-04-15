package com.sourcepoint.cmplibrary.core.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*  //ktlint-disable
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.WebViewException
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser

internal class SPWebViewClient(
    val wv: WebView,
    private val onError: (ConsentLibExceptionK) -> Unit,
    private val onNoIntentActivitiesFoundFor: (String) -> Unit,
    private val timer: SpTimer,
    private val logger: Logger,
    private val messageTimeout: Long = 1000
) : WebViewClient() {

    var jsReceiverConfig: (() -> String)? = null

    companion object {
        val TAG = SPWebViewClient::class.simpleName
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        logger.d(this::class.java.name, "1234 onPageStarted...")
        timer.executeDelay(messageTimeout) {
//            onError(ConnectionTimeoutException(description = "A timeout has occurred when loading the message"))
//            view?.stopLoading()
            logger.d(this::class.java.name, "1234 executedDelay progress ${wv.progress}")
        }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        timer.cancel()
        logger.d(this::class.java.name, "1234 onPageFinished progress ${wv.progress}")
        logger.d(this::class.java.name, "1234 ==========================================")
        try {
            jsReceiverConfig
                ?.let {
                    view.loadUrl(it())
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
        logger.d(this::class.java.name, "1234 onReceivedError progress ${wv.progress}")
        onError(WebViewException(description = error.toString()))
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        logger.d(this::class.java.name, "1234 onReceivedError progress ${wv.progress}")
        onError(WebViewException(description = description))
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError) {
        super.onReceivedSslError(view, handler, error)
        logger.d(this::class.java.name, "1234 onReceivedSslError progress ${wv.progress}")
        onError(WebViewException(description = error.toString()))
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        val message = "The WebView rendering process crashed!"
        logger.d(this::class.java.name, "1234 onRenderProcessGone progress ${wv.progress}")
        onError(WebViewException(description = message))
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        logger.d(this::class.java.name, "1234 shouldOverrideUrlLoading progress ${wv.progress} url[$url}")
        wv.context.loadLinkOnExternalBrowser(url) {
            onNoIntentActivitiesFoundFor(it)
        }
        return true
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        logger.d(this::class.java.name, "1234 onLoadResource progress ${wv.progress} link[$url]")
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)
        logger.e(this::class.java.name, "1234 onReceivedHttpError progress $errorResponse")
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        logger.d(this::class.java.name, "1234 onPageCommitVisible progress ${wv.progress}")
    }
}

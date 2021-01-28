package com.sourcepoint.cmplibrary.core.web

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.util.getLinkUrl
import com.sourcepoint.cmplibrary.util.loadLinkOnExternalBrowser
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.exception.Logger

abstract class ConsentWebView : WebView {

    constructor(context: Context?) : super(context){ setup() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){ setup() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){ setup() }

    val spWebViewClient : SPWebViewClient by lazy {
        SPWebViewClient(
            wv = this,
            log = logger,
            onError = { onError(it) },
            onNoIntentActivitiesFoundFor = { onNoIntentActivitiesFoundFor(it) }
        )
    }

    internal abstract val jsReceiver: JSReceiver
    internal abstract val logger: Logger
    private val  chromeClient = object : WebChromeClient() {
        override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message): Boolean {
            context.loadLinkOnExternalBrowser(getLinkUrl(view.hitTestResult)){
                onNoIntentActivitiesFoundFor(it)
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getLinkUrl(view.hitTestResult)))
            view.context.startActivity(browserIntent)
            return false
        }
    }

    private fun setup(){
        enableDebug()
        setStyle()
        webViewClient = spWebViewClient
        webChromeClient = chromeClient
        addJavascriptInterface(jsReceiver, "JSReceiver")
    }

    private fun setStyle(){
        settings.javaScriptEnabled = true
        setBackgroundColor(Color.TRANSPARENT)
        this.requestFocus()
    }

    private fun enableDebug(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
                setWebContentsDebuggingEnabled(true)
                enableSlowWholeDocumentDraw()
            }
        }
    }

    abstract fun onNoIntentActivitiesFoundFor(url: String)
    abstract fun onError(error: ConsentLibException)
}

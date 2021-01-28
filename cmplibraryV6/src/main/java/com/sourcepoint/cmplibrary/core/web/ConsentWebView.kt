package com.sourcepoint.cmplibrary.core.web

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.sourcepoint.cmplibrary.data.network.converted.JsonConverter
import com.sourcepoint.cmplibrary.util.*
import com.sourcepoint.gdpr_cmplibrary.ConsentAction
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException.NoInternetConnectionException
import com.sourcepoint.gdpr_cmplibrary.CustomJsonParser
import com.sourcepoint.gdpr_cmplibrary.exception.Logger
import org.json.JSONObject

abstract class ConsentWebView : WebView, JSReceiver{

    constructor(context: Context?) : super(context){ setup() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){ setup() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){ setup() }

    companion object {
        val TAG = ConsentWebView::class.simpleName
    }

    private val spWebViewClient : SPWebViewClient by lazy {
        SPWebViewClient(
            wv = this,
            log = logger,
            onError = { onError(it) },
            onNoIntentActivitiesFoundFor = { onNoIntentActivitiesFoundFor(it) }
        )
    }

    internal abstract val jsReceiver: JSReceiver
    internal abstract val logger: Logger
    internal abstract val connectionManager: ConnectionManager
    internal abstract val jsonConverter: JsonConverter

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

    internal fun loadConsentUIFromUrl(url: String) : Either<Boolean> =  check {
        if (connectionManager.isConnected) throw NoInternetConnectionException()
        Log.d(TAG, "Loading Webview with: $url")
        Log.d(TAG, "User-Agent: " + settings.userAgentString)
        loadUrl(url)
        true
    }

    abstract fun onNoIntentActivitiesFoundFor(url: String)
    abstract fun onError(error: ConsentLibException)
    abstract override fun onConsentUIReady(isFromPM: Boolean)
    abstract fun onAction(action: ConsentAction?)

    override fun log(tag: String?, msg: String?) { Log.i(tag, msg) }

    override fun log(msg: String?) { Log.i(TAG, msg) }

    override fun onAction(actionData: String?) {
        jsonConverter.toConsentAction(actionData!!)
            .map { onAction(it) }
    }

    override fun onError(errorMessage: String?) {
        TODO("Not yet implemented")
    }

//    private open fun consentAction(actionFromJS: JSONObject, logger: Logger): ConsentAction? {
//        return ConsentAction(
//            CustomJsonParser.getInt("actionType", actionFromJS, getLogger()),
//            CustomJsonParser.getString("choiceId", actionFromJS, logger),
//            CustomJsonParser.getString("privacyManagerId", actionFromJS, logger),
//            CustomJsonParser.getString("pmTab", actionFromJS, logger),
//            CustomJsonParser.getBoolean("requestFromPm", actionFromJS, getLogger()),
//            CustomJsonParser.getJson("saveAndExitVariables", actionFromJS, getLogger()),
//            CustomJsonParser.getString("consentLanguage", actionFromJS, logger)
//        )
//    }
}

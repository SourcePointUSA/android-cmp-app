package com.sourcepoint.cmplibrary.core.web

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import java.util.logging.Logger

abstract class ConsentWebView : WebView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    internal abstract val jsReceiver: JSReceiver
    internal abstract val logger: Logger
}

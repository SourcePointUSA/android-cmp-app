package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.webkit.WebView

internal fun Context.loadLinkOnExternalBrowser(
    url: String,
    onNoIntentActivitiesFound: (url: String) -> Unit
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    val l = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (l.size != 0)
        startActivity(intent)
    else
        onNoIntentActivitiesFound(url)
}

internal fun WebView.getLinkUrl(testResult: WebView.HitTestResult): String {
    if (doesLinkContainImage(testResult)) {
        val handler = Handler()
        val message = handler.obtainMessage()
        requestFocusNodeHref(message)
        return message.data["url"] as? String ?: ""
    }
    return testResult.extra ?: ""
}

internal fun doesLinkContainImage(testResult: WebView.HitTestResult): Boolean {
    return testResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
}

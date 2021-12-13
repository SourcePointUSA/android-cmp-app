package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.webkit.WebView

internal fun Context.loadLinkOnExternalBrowser(
    url: String,
    onNoIntentActivitiesFound: (url: String) -> Unit
) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    val canBeProcessed = isIntentExecutable(this, intent)
    if (canBeProcessed)
        startActivity(intent)
    else
        onNoIntentActivitiesFound(url)
}

internal fun isIntentExecutable(context: Context, uriIntent: Intent): Boolean {
    val packageManager = context.packageManager
    val scheme = uriIntent.scheme
    if (scheme == null || scheme != "http" && scheme != "https") {
        return false
    }

    val resolvedActivityList: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        packageManager.queryIntentActivities(uriIntent, PackageManager.MATCH_ALL)
    } else {
        packageManager.queryIntentActivities(uriIntent, 0)
    }

    for (activityResolveInfo in resolvedActivityList) {
        val match = activityResolveInfo.match
        val matchesPath = match and IntentFilter.MATCH_CATEGORY_PATH > 0
        val isStub = activityResolveInfo.activityInfo?.packageName?.startsWith("com.google.android.tv.frameworkpackagestubs") ?: false
        if (matchesPath || isStub) {
            return false
        }
    }
    return true
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

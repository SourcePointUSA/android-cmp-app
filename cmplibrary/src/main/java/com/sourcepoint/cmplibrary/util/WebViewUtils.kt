package com.sourcepoint.cmplibrary.util

import android.content.Context
import android.content.Intent
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
    if (canOpenURLIntent(this, intent))
        startActivity(intent)
    else
        onNoIntentActivitiesFound(url)
}

internal fun canOpenURLIntent(context: Context, uriIntent: Intent): Boolean {
    val packageManager = context.packageManager
    val scheme = uriIntent.scheme
    if (scheme == null || scheme != "http" && scheme != "https") {
        return false
    }

    val resolvedActivityList: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        packageManager.queryIntentActivities(uriIntent, PackageManager.MATCH_ALL)
    } else {
        packageManager.queryIntentActivities(uriIntent, 0)
    }.filterNot { it.activityInfo?.packageName?.startsWith("com.google.android.tv.frameworkpackagestubs") ?: false }

    return resolvedActivityList.isNotEmpty()
}

internal fun WebView.getLinkUrl(testResult: WebView.HitTestResult): String {
    if (doesLinkContainImage(testResult)) {
        val message = Handler().obtainMessage()
        requestFocusNodeHref(message)
        return message.data["url"] as? String ?: ""
    }
    return testResult.extra ?: ""
}

internal fun doesLinkContainImage(testResult: WebView.HitTestResult): Boolean =
    testResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE

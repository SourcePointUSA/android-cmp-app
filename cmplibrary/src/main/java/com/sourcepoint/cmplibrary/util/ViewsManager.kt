package com.sourcepoint.cmplibrary.util

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.cmplibrary.SpConsentLibImpl
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.web.CampaignModel
import com.sourcepoint.cmplibrary.core.web.ConsentWebView
import com.sourcepoint.cmplibrary.core.web.IConsentWebView
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.exception.WebViewCreationException
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import java.lang.ref.WeakReference
import java.util.* // ktlint-disable

/**
 * Entity used to handle the views of the activity
 */
internal interface ViewsManager {

    val isViewInLayout: Boolean

    fun showView(view: View)
    fun createWebView(
        lib: SpConsentLibImpl,
        jsReceiverDelegate: SpConsentLibImpl.JSReceiverDelegate,
        messageType: MessageType,
        cmpViewId: Int?
    ): Either<IConsentWebView>
    fun createWebView(
        lib: SpConsentLibImpl,
        jsReceiverDelegate: SpConsentLibImpl.JSReceiverDelegate,
        campaignQueue: Queue<CampaignModel>,
        messageType: MessageType,
        cmpViewId: Int?
    ): Either<IConsentWebView>
    fun removeView(view: View)
    fun removeAllViews()
    fun removeAllViewsExcept(pView: View)
    fun dispose()

    fun handleOnBackPress()
    companion object
}

/**
 * Factory method to create an instance of a [ViewsManager] using its implementation
 * @param actWeakReference it is a weak reference which contains an Activity reference
 * @return an instance of the [ViewsManagerImpl] implementation
 */
internal fun ViewsManager.Companion.create(
    actWeakReference: WeakReference<Activity>,
    connectionManager: ConnectionManager,
    messageTimeout: Long
): ViewsManager = ViewsManagerImpl(
    weakReference = actWeakReference,
    connectionManager = connectionManager,
    messageTimeout = messageTimeout
)

private class ViewsManagerImpl(
    val weakReference: WeakReference<Activity>,
    val connectionManager: ConnectionManager,
    val messageTimeout: Long
) : ViewsManager {

    private var webview: IConsentWebView? = null
    val idsSet = LinkedHashSet<Int>()

    val mainView: ViewGroup?
        get() = weakReference.get()?.findViewById(R.id.content)

    override fun removeView(view: View) {
        idsSet.remove(view.id)
        mainView?.run {
            post { this.removeView(view) }
        }
    }

    override fun showView(view: View) {
        idsSet.add(view.id)
        if (view.parent == null) {
            mainView?.let {
                it.post {
                    view.layoutParams = ViewGroup.LayoutParams(0, 0)
                    view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    view.bringToFront()
                    view.requestLayout()
                    it.addView(view)
                }
            }
        }
        removeAllViewsExcept(view)
    }

    override fun removeAllViews() {
        val idsList = idsSet.toMutableList()
        idsList.forEach { id ->
            mainView?.findViewById<View>(id)?.let { view ->
                mainView?.run {
                    post { this.removeView(view) }
                }
            }
        }
        webview = null
        idsSet.clear()
    }

    override fun removeAllViewsExcept(pView: View) {
        val idsList = idsSet.toMutableList()
        if (webview != null && webview is ConsentWebView && pView != webview) {
            this.removeView(webview as ConsentWebView)
            webview = null
        }
        idsList.forEach { id ->
            mainView?.findViewById<View>(id)?.let { view ->
                if (pView != view) {
                    mainView?.run {
                        post {
                            this.removeView(view)
                            idsSet.remove(id)
                        }
                    }
                }
            }
        }
    }

    override fun createWebView(
        lib: SpConsentLibImpl,
        jsReceiverDelegate: SpConsentLibImpl.JSReceiverDelegate,
        messageType: MessageType,
        cmpViewId: Int?
    ): Either<IConsentWebView> {
        return weakReference.get()?.let {
            check {
                val newWebView = ConsentWebView(
                    context = it,
                    connectionManager = connectionManager,
                    jsClientLib = jsReceiverDelegate,
                    logger = lib.pLogger,
                    executorManager = lib.executor,
                    messageTimeout = messageTimeout,
                    messageType = messageType,
                    viewId = cmpViewId
                )
                this.webview = newWebView
                newWebView
            }
        } ?: Either.Left(WebViewCreationException(description = "The activity reference in the ViewManager is null!!!"))
    }

    override fun createWebView(
        lib: SpConsentLibImpl,
        jsReceiverDelegate: SpConsentLibImpl.JSReceiverDelegate,
        campaignQueue: Queue<CampaignModel>,
        messageType: MessageType,
        cmpViewId: Int?
    ): Either<IConsentWebView> {
        return weakReference.get()?.let {
            check {
                val newWebView = ConsentWebView(
                    context = it,
                    connectionManager = connectionManager,
                    jsClientLib = jsReceiverDelegate,
                    logger = lib.pLogger,
                    executorManager = lib.executor,
                    campaignQueue = campaignQueue,
                    messageTimeout = messageTimeout,
                    messageType = messageType,
                    viewId = cmpViewId
                )
                this.webview = newWebView
                newWebView
            }
        } ?: Either.Left(WebViewCreationException(description = "The activity reference in the ViewManager is null!!!"))
    }
    override fun dispose() {
        webview = null
        weakReference.clear()
    }

    override fun handleOnBackPress() {
        webview?.handleOnBackPress()
    }

    override val isViewInLayout: Boolean
        get() = idsSet.isNotEmpty()
}

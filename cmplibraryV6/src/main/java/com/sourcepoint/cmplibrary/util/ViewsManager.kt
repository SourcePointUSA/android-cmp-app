package com.sourcepoint.cmplibrary.util

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.sourcepoint.cmplibrary.ConsentLibImpl
import com.sourcepoint.cmplibrary.core.web.ConsentWebView
import com.sourcepoint.cmplibrary.core.web.IConsentWebView
import com.sourcepoint.cmplibrary.util.IDS.idsSet
import java.lang.ref.WeakReference

/**
 * Entity used to handle the activity view attached to
 */
internal interface ViewsManager {
    fun removeView(view: View?)
    fun showView(view: View)
    fun createWebView(lib: ConsentLibImpl): IConsentWebView?
    companion object
}

/**
 * Factory method to create an instance of a [ViewsManager] using its implementation
 * @param actWeakReference it is a weak reference which contains an Activity reference
 * @return an instance of the [ViewsManagerImpl] implementation
 */
internal fun ViewsManager.Companion.create(actWeakReference: WeakReference<Activity>): ViewsManager = ViewsManagerImpl(actWeakReference)

object IDS {
    val idsSet = mutableSetOf<Int>()
}

private class ViewsManagerImpl(val weakReference: WeakReference<Activity>) : ViewsManager {

    val mainView: ViewGroup?
        get() = weakReference.get()?.findViewById(R.id.content)

    override fun removeView(view: View?) {
        val idsList = idsSet.toMutableList()
        idsList.forEach { id ->
            mainView?.findViewById<View>(id)?.let { view ->
                mainView?.run {
                    post { this.removeView(view) }
                }
            }
        }
        idsSet.clear()
    }

    override fun showView(view: View) {
        println("[ids]===================================================")
        println("[ids]===================================================")
        println("current [ids][${idsSet.size}]")
        removeView(null)
        println("[ids] after deletion[${idsSet.size}]")
        idsSet.add(view.id)
        println("current [ids][${idsSet.size}]")
        println("[ids]===================================================")
        println("[ids]===================================================")
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
    }

    override fun createWebView(lib: ConsentLibImpl): ConsentWebView? {
        return weakReference.get()?.let {
            ConsentWebView(
                context = it,
                connectionManager = lib.pConnectionManager,
                jsReceiver = lib.JSReceiverDelegate(),
                logger = lib.pLogger
            )
        }
    }
}

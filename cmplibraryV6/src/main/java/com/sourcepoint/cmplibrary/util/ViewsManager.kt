package com.sourcepoint.cmplibrary.util

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference

internal interface ViewsManager {
    fun removeView(view: View)
    fun showView(view: View)

    companion object
}

internal fun ViewsManager.Companion.create(weakReference : WeakReference<Activity>): ViewsManager = ViewsManagerImpl(weakReference)

private class ViewsManagerImpl(val weakReference : WeakReference<Activity>) : ViewsManager {

    val mainView : ViewGroup? by lazy {
        weakReference.get()?.findViewById<ViewGroup>(R.id.content)
    }

    override fun removeView(view: View) {
        view.parent?.let { mainView?.removeView(view) }
    }

    override fun showView(view: View) {
        if (view.parent == null) {
            view.layoutParams = ViewGroup.LayoutParams(0, 0)
            view.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            view.bringToFront()
            view.requestLayout()
            mainView?.addView(view)
        }
    }
}
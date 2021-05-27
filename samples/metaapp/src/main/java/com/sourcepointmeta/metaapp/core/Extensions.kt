package com.sourcepointmeta.metaapp.core

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.sourcepointmeta.metaapp.R

fun AppCompatActivity.init(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager.commit {
        add(resId, fragment, fragment::class.java.name)
    }
}

fun AppCompatActivity.addFragment(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager.commit {
        addToBackStack("back_stack")
        setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
        add(resId, fragment, fragment::class.java.name)
    }
}

val RecyclerView.ViewHolder.layoutPositionOrNull
    get() = when (layoutPosition == 0) {
        true -> null
        false -> layoutPosition
    }

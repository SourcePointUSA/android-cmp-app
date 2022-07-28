package com.sourcepointmeta.metaapp.core

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

fun AppCompatActivity.init(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .add(resId, fragment, fragment::class.java.name)
        .commit()
}

fun AppCompatActivity.addFragment(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .addToBackStack("back_stack")
        .add(resId, fragment, fragment::class.java.name)
        .commit()
}

fun AppCompatActivity.replaceFragment(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .addToBackStack("back_stack")
        .replace(resId, fragment, fragment::class.java.name)
        .commit()
}

fun AppCompatActivity.replaceWithoutBackstackFragment(@IdRes resId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .replace(resId, fragment, fragment::class.java.name)
        .commit()
}

fun <T> List<T>.moveToBegin(position: Int): MutableList<T> {
    if (position > lastIndex) throw RuntimeException("Position not valid!!!")
    return (
        mutableListOf(get(position)) +
            subList(0, position) +
            subList(position + 1, size)
        ).toMutableList()
}

val RecyclerView.ViewHolder.layoutPositionOrNull
    get() = when (layoutPosition == 0) {
        true -> null
        false -> layoutPosition
    }

fun String.formatToDouble(pattern: String = "#.00"): Double {
    return DecimalFormat(pattern).parse(this)!!.toDouble()
}

fun Double.formatToString(pattern: String = "%.2f"): String {
    return String.format(pattern, this)
}

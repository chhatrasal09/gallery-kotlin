package com.app.zee5test.utils

import android.content.res.Resources
import android.view.View

val Int.dp
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Float.dp
    get() = this * Resources.getSystem().displayMetrics.density
val Float.px
    get() = this / Resources.getSystem().displayMetrics.density

fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}